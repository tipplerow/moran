
package moran.segment;

import jam.app.JamProperties;
import jam.io.DataReader;
import jam.matrix.JamMatrix;
import jam.matrix.MatrixView;
import jam.util.RegexUtil;

import moran.cell.Genotype;
import moran.cell.Phenotype;

/**
 * Implements a phenotype that maps gene segment copy number states to
 * scalar fitness values.
 *
 * <p><b>Data file format.</b> The underlying fitness matrix for the
 * copy number phenotype is defined by a user-specified data file.
 * This class supports two formats: (1) fully explicit, and (2)
 * chained operation.  The file parser will automatically determine
 * which file format has been used.
 *
 * <p>Fully explicit files follow this format:
 * <pre>
       Segment,    0,    1,   2,    3,    4,    5,    6
       6p,      0.82, 0.93, 1.0, 1.02, 1.05, 1.06, 1.10
       9q,      1.11, 1.02, 1.0, 0.99, 0.97, 0.96, 0.96
 * </pre>
 *
 * The comma-separated header line must contain a segment label (which
 * is ignored) and copy-number labels which must run from zero to the
 * maximum copy number defined in the {@code SegmentCNGenotype} class.
 * There must be exactly one comma-separated data line for each genome
 * segment with the fitness values given for each copy number state.
 * Blank lines and comment text (noted by a {@code #} character) are
 * permitted.
 *
 * <p>Chained operation files follow this format:
 * <pre>
       6p, gain,  0.05
       6p, loss, -0.02
       9p, gain, -0.01
       9p, loss,  0.02
 * </pre>
 *
 * There is no header line.  The file must contain two data lines for
 * each genome segment, one with the fitness change for a copy number
 * gain event, and another for a loss event.  The tags {@code gain}
 * and {@code loss} must be used to distinguish the event types (but
 * they are not case-sensitive).  Blank lines and comment text (noted
 * by a {@code #} character) are permitted.
 */
public final class SegmentCNPhenotype implements Phenotype {
    //
    // Explicitly enumerated fitness values for every possible copy
    // number state. The ordinal index of the genome segment is used
    // as the row index, the copy number is used as the column index.
    //
    private final MatrixView fitnessMatrix;

    private static SegmentCNPhenotype global = null;

    private SegmentCNPhenotype(MatrixView fitnessMatrix) {
        validateFitness(fitnessMatrix);
        this.fitnessMatrix = fitnessMatrix;
    }

    /**
     * Name of the system property that defines the path to the file
     * containing the fitness matrix.
     */
    public static final String FITNESS_MATRIX_FILE_PROPERTY = "moran.segment.fitnessMatrixFile";

    /**
     * Name of the system property that defines the chain operation
     * used to construct the full fitness matrix from the changes in
     * fitness for single gain and loss events.  This property does
     * not need to be set if the fitness matrix is fully explicit.
     */
    public static final String FITNESS_CHAIN_OPERATION_PROPERTY = "moran.segment.fitnessChainOperation";

    /**
     * Returns the global phenotype defined by system properties.
     *
     * @return the global phenotype defined by system properties.
     */
    public static final SegmentCNPhenotype global() {
        if (global == null)
            global = new SegmentCNPhenotype(createGlobalMatrix());

        return global;
    }

    private static JamMatrix createGlobalMatrix() {
        return FitnessMatrixLoader.load(resolveMatrixFile());
    }

    private static String resolveMatrixFile() {
        return JamProperties.getRequired(FITNESS_MATRIX_FILE_PROPERTY);
    }

    /**
     * Returns a read-only view of the underlying fitness matrix.
     *
     * @return a read-only view of the underlying fitness matrix.
     */
    public MatrixView viewFitnessMatrix() {
        return fitnessMatrix;
    }
   
    @Override public double getFitness(Genotype genotype) {
        if (genotype instanceof SegmentCNGenotype)
            return getCNFitness((SegmentCNGenotype) genotype);
        else
            throw new IllegalArgumentException("Invalid genotype runtime type.");
    }

    private double getCNFitness(SegmentCNGenotype genotype) {
        double fitness = 0.0;

        for (GenomeSegment segment : GenomeSegment.list()) {
            int row = segment.indexOf();
            int col = genotype.count(segment);

            fitness += fitnessMatrix.get(row, col);
        }

        return fitness;
    }

    private static void validateFitness(MatrixView fitnessMatrix) {
        if (fitnessMatrix.nrow() != rowCount())
            throw new IllegalArgumentException("Invalid fitness matrix row count.");

        if (fitnessMatrix.ncol() != colCount())
            throw new IllegalArgumentException("Invalid fitness matrix column count.");

        for (int row = 0; row < fitnessMatrix.nrow(); ++row)
            for (int col = 0; col < fitnessMatrix.ncol(); ++col)
                if (fitnessMatrix.get(row, col) < 0.0)
                    throw new IllegalArgumentException("Negative fitness value.");
    }

    private static int rowCount() {
        //
        // One row for each genome segment...
        //
        return GenomeSegment.count();
    }

    private static int colCount() {
        //
        // One column for each allowed copy number...
        //
        return SegmentCNGenotype.maxCopyNumber() + 1;
    }

    private static JamMatrix createZeroMatrix() {
        return JamMatrix.zeros(rowCount(), colCount());
    }
}
