
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

    // One row for each genome segment...
    static final int ROW_COUNT = GenomeSegment.count();

    // One column for each allowed copy number...
    static final int COL_COUNT = SegmentCNGenotype.maxCopyNumber() + 1;

    static JamMatrix load(String fileName) {
        FitnessMatrixLoader loader = new FitnessMatrixLoader(fileName);
        return loader.load();
    }

    static void validateFitness(MatrixView fitnessMatrix) {
        if (fitnessMatrix.nrow() != ROW_COUNT)
            throw new IllegalArgumentException("Invalid fitness matrix row count.");

        if (fitnessMatrix.ncol() != COL_COUNT)
            throw new IllegalArgumentException("Invalid fitness matrix column count.");

        for (int row = 0; row < fitnessMatrix.nrow(); ++row)
            for (int col = 0; col < fitnessMatrix.ncol(); ++col)
                if (fitnessMatrix.get(row, col) < 0.0)
                    throw new IllegalArgumentException("Negative fitness value.");
    }

    private JamMatrix load() {
        lines = DataReader.read(fileName, RegexUtil.PYTHON_COMMENT);
        matrix = initMatrix();

        if (ChainedMatrixLoader.isChained(lines)) {
            ChainedMatrixLoader.load(matrix, lines);
        }
        else if (ExplicitMatrixLoader.isExplicit(lines)) {
            ExplicitMatrixLoader.load(matrix, lines);
        }
        else {
            throw JamException.runtime("Unexpected line count in [%s].", fileName);
        }

        validateFitness(matrix);
        return matrix;
    }

    private static JamMatrix initMatrix() {
        //
        // Start with a matrix of negative values and check after
        // processing the data file that all elements were assigned
        // valid, non-negative values...
        //
        return new JamMatrix(ROW_COUNT, COL_COUNT, -1.0);
    }
}
