
package moran.segment;

import java.util.List;

import jam.io.DataReader;
import jam.lang.JamException;
import jam.matrix.JamMatrix;
import jam.util.RegexUtil;

final class ExplicitMatrixLoader {
    private final JamMatrix matrix;
    private final List<String> lines;

    private ExplicitMatrixLoader(JamMatrix matrix, List<String> lines) {
        this.lines = lines;
        this.matrix = matrix;
    }

    static void load(JamMatrix matrix, List<String> lines) {
        ExplicitMatrixLoader loader = new ExplicitMatrixLoader(matrix, lines);
        loader.load();
    }

    private void load() {
        validateHeader();

        for (int lineNumber = 1; lineNumber < lines.size(); ++lineNumber)
            processLine(lines.get(lineNumber));
    }

    private void validateHeader() {
        //
        // Ignore the first label but all other labels must be the
        // integers from zero to the maximum allowed copy number.
        //
        String[] fields = splitLine(lines.get(0));

        for (int fieldIndex = 1; fieldIndex < fields.length; ++fieldIndex)
            if (Integer.parseInt(fields[fieldIndex]) != fieldIndex - 1)
                throw JamException.runtime("Invalid header line: [%s].", lines.get(0));
    }

    private static String[] splitLine(String line) {
        return RegexUtil.split(FitnessMatrixLoader.DELIM, line, 1 + FitnessMatrixLoader.colCount());
    }

    private void processLine(String line) {
        String[] fields = splitLine(line);

        GenomeSegment segment = GenomeSegment.require(fields[0]);
        int row = segment.indexOf();

        for (int fieldIndex = 1; fieldIndex < fields.length; ++fieldIndex)
            matrix.set(row, fieldIndex - 1, Double.parseDouble(fields[fieldIndex]));
    }
}
