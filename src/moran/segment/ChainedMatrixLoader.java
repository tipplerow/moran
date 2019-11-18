
package moran.segment;

import java.util.List;

import jam.app.JamProperties;
import jam.matrix.JamMatrix;
import jam.util.RegexUtil;

import moran.cna.CNAType;
import moran.cna.FitnessChainOperation;

final class ChainedMatrixLoader {
    private final JamMatrix matrix;
    private final List<String> lines;
    private final FitnessChainOperation operation;

    private ChainedMatrixLoader(JamMatrix matrix, List<String> lines) {
        this.lines = lines;
        this.matrix = matrix;
        this.operation = resolveOperation();
    }

    private static FitnessChainOperation resolveOperation() {
        return JamProperties.getRequiredEnum(SegmentCNPhenotype.FITNESS_CHAIN_OPERATION_PROPERTY, FitnessChainOperation.class);
    }

    static void load(JamMatrix matrix, List<String> lines) {
        ChainedMatrixLoader loader = new ChainedMatrixLoader(matrix, lines);
        loader.load();
    }

    private void load() {
        for (String line : lines)
            processLine(line);
    }

    private void processLine(String line) {
        String[] fields = RegexUtil.split(FitnessMatrixLoader.DELIM, line, 3);

        GenomeSegment segment = GenomeSegment.require(fields[0]);
        CNAType       cnaType = CNAType.instance(fields[1]);
        double        fitness = Double.parseDouble(fields[2]);

        // Unit fitness for the wild type...
        int row = segment.indexOf();
        matrix.set(row, 2, 1.0);

        switch (cnaType) {
        case GAIN:
            processGain(row, fitness);
            break;

        case LOSS:
            processLoss(row, fitness);
            break;

        default:
            throw new IllegalStateException("Invalid event type.");
        }
    }

    private void processGain(int row, double fitness) {
        for (int col = 3; col < matrix.ncol(); ++col)
            matrix.set(row, col, operation.computeFitness(fitness, col));
    }

    private void processLoss(int row, double fitness) {
        matrix.set(row, 0, operation.computeFitness(fitness, 0));
        matrix.set(row, 1, operation.computeFitness(fitness, 1));
    }
}
