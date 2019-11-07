
package moran.segment;

import java.util.List;
import java.util.regex.Pattern;

import jam.io.DataReader;
import jam.lang.JamException;
import jam.matrix.JamMatrix;
import jam.matrix.MatrixView;
import jam.util.RegexUtil;

final class FitnessMatrixLoader {
    private final String fileName;

    private JamMatrix matrix;
    private List<String> lines;

    private FitnessMatrixLoader(String fileName) {
        this.fileName = fileName;
    }

    static final Pattern DELIM = RegexUtil.COMMA;

    static JamMatrix load(String fileName) {
        FitnessMatrixLoader loader = new FitnessMatrixLoader(fileName);
        return loader.load();
    }

    static void validateFitness(MatrixView fitnessMatrix) {
        if (fitnessMatrix.nrow() != rowCount())
            throw new IllegalArgumentException("Invalid fitness matrix row count.");

        if (fitnessMatrix.ncol() != colCount())
            throw new IllegalArgumentException("Invalid fitness matrix column count.");

        for (int row = 0; row < fitnessMatrix.nrow(); ++row)
            for (int col = 0; col < fitnessMatrix.ncol(); ++col)
                if (fitnessMatrix.get(row, col) < 0.0)
                    throw new IllegalArgumentException("Negative fitness value.");
    }

    static int rowCount() {
        //
        // One row for each genome segment...
        //
        return GenomeSegment.count();
    }

    static int colCount() {
        //
        // One column for each allowed copy number...
        //
        return SegmentCNGenotype.maxCopyNumber() + 1;
    }

    private JamMatrix load() {
        lines = DataReader.read(fileName, RegexUtil.PYTHON_COMMENT);
        matrix = initMatrix();

        if (lines.size() == 2 * rowCount())
            ChainedMatrixLoader.load(matrix, lines);
        else if (lines.size() == rowCount() + 1)
            ExplicitMatrixLoader.load(matrix, lines);
        else
            throw JamException.runtime("Unexpected line count in [%s].", fileName);

        validateFitness(matrix);
        return matrix;
    }

    private static JamMatrix initMatrix() {
        //
        // Start with a matrix of negative values and check after
        // processing the data file that all elements were assigned
        // valid, non-negative values...
        //
        return new JamMatrix(rowCount(), colCount(), -1.0);
    }
}
