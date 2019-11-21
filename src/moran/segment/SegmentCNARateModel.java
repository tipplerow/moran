
package moran.segment;

import java.util.ArrayList;
import java.util.List;

import jam.app.JamProperties;
import jam.io.DataReader;
import jam.math.EventSet;
import jam.math.Probability;
import jam.util.RegexUtil;

import moran.cna.CNAType;

/**
 * Governs the rates at which copy number alterations (CNA) occur.
 *
 * <p><b>Uniform rates.</b> In the simplest CNA model, every genome
 * segment has the same rate of gain or loss (although the gain and
 * loss rates may differ).  This model is specified through system
 * properties that define the uniform parameters.
 *
 * <p><b>Segment-specific rates.</b> At the next level of complexity,
 * the rates of gain and loss may differ for each genome segment (but
 * they are independent of copy number).  Models of this type are
 * defined by a parameter file with the following format:
 *
 * <pre>
       6p, gain, 1.0E-04
       6p, loss, 2.0E-04
       9q, gain, 5.0E-06
       9q, loss, 1.0E-08
 * </pre>
 *
 * There is no header line.  The file must contain two data lines for
 * each genome segment, one with the rate of copy number gain events
 * (the probability per cell division), and another for a loss event.
 * The tags {@code gain} and {@code loss} must be used to distinguish
 * the event types (but they are not case-sensitive).  Blank lines and
 * comment text (noted by a {@code #} character) are permitted.
 *
 * <p><b>Segment-specifc and copy-number specific rates.</b> The most
 * granular CNA model allows gain and loss rates to vary by genome
 * segment and the segment copy number.  These models are defined by
 * parameter files formatted as follows:
 *
 * <pre>
       6p, 1, gain, 1.0E-04
       6p, 1, loss, 2.0E-04
       6p, 2, gain, 2.0E-04
       6p, 2, loss, 2.4E-04
       6p, 3, gain, 8.0E-05
       6p, 3, loss, 7.5E-05
       6p, 4, gain, 1.0E-05
       6p, 4, loss, 5.0E-02
       .
       .
       .
 * </pre>
 * 
 * There is no header line.  The file must contain two data lines for
 * each genome segment and copy number ranging from one to the maximum
 * copy number defined in the {@code SegmentCNGenotype} class.  The
 * comma-separated fields must list the genome segment, copy number, a
 * {@code gain} or {@code loss} tag, ant the rate of gain or loss (the
 * probability per cell division) for that segment and copy number.
 * Blank lines and comment text (noted by a {@code #} character) are
 * permitted.
 *
 * <p><b>Whole genome doubling.</b> The rate of whole genome doubling
 * is specified by the system property {@code moran.segment.rateWGD}.
 *
 * <p><b>Exclusive events.</b> When simulating the possible mutations
 * of a genotype, we first consider the probability of a whole-genome
 * doubling event.  If that occurs, no other events are considered.
 * If whole-genome doubling does not occur, we consider each genome
 * segment independently.  Gains and losses are mutually exclusive
 * within a segment (one or the other may occur but not both), but
 * copy-number events may occur on multiple genome segments during
 * a single cell division.
 */
public final class SegmentCNARateModel {
    //
    // Gain rates and loss rates stored in matrix form, using the
    // index of the genome segment as the row index and the copy
    // number as the column index.
    //
    private final SegmentCNARateMatrix gainRates;
    private final SegmentCNARateMatrix lossRates;

    // Whole genome doubling rate...
    private final Probability rateWGD;

    // Gain/loss event sets with genome segments providing the
    // row indexes and copy number states the column indexes...
    private final SegmentCNASet eventSets;

    private static SegmentCNARateModel global = null;

    private SegmentCNARateModel(SegmentCNARateMatrix gainRates,
                                SegmentCNARateMatrix lossRates) {
        validateRates(gainRates, CNAType.GAIN);
        validateRates(lossRates, CNAType.LOSS);

        this.rateWGD   = resolveRateWGD();
        this.gainRates = gainRates;
        this.lossRates = lossRates;
        this.eventSets = SegmentCNASet.create(rateWGD, gainRates, lossRates);
    }

    private static void validateRates(SegmentCNARateMatrix rates, CNAType type) {
        rates.validate();

        if (!rates.getType().equals(type))
            throw new IllegalArgumentException("Invalid rate matrix type.");
    }

    private static Probability resolveRateWGD() {
        return Probability.parse(JamProperties.getRequired(WGD_RATE_PROPERTY));
    }

    /**
     * Name of the system property that defines the rate (probability
     * per cell division) of whole genome doubling.
     */
    public static final String WGD_RATE_PROPERTY = "moran.segment.rateWGD";

    /**
     * Name of the system property that defines the uniform rate
     * (probability per cell division) of copy number gains.
     *
     * <p>If this property is defined, the {@code LOSS_RATE_PROPERTY}
     * must also be defined and the {@code RATE_FILE_PROPERTY} must
     * <em>not</em> be defined.
     */
    public static final String GAIN_RATE_PROPERTY = "moran.segment.gainRate";

    /**
     * Name of the system property that defines the uniform rate
     * (probability per cell division) of copy number losses.
     *
     * <p>If this property is defined, the {@code GAIN_RATE_PROPERTY}
     * must also be defined and the {@code RATE_FILE_PROPERTY} must
     * <em>not</em> be defined.
     */
    public static final String LOSS_RATE_PROPERTY = "moran.segment.lossRate";

    /**
     * Name of the system property that defines the file containing
     * the segment-specific (and possibly copy-number specific) gain
     * and loss rates.
     *
     * <p>If this property is defined, the {@code GAIN_RATE_PROPERTY}
     * and {@code LOSS_RATE_PROPERTY} must <em>not</em> be defined.
     */
    public static final String RATE_FILE_PROPERTY = "moran.segment.cnaRateFile";

    /**
     * Returns the global CNA rate model defined by system properties.
     *
     * @return the global CNA rate model defined by system properties.
     */
    public static final SegmentCNARateModel global() {
        if (global == null)
            global = createGlobal();

        return global;
    }

    private static SegmentCNARateModel createGlobal() {
        boolean isGainRateSet = JamProperties.isSet(GAIN_RATE_PROPERTY);
        boolean isLossRateSet = JamProperties.isSet(LOSS_RATE_PROPERTY);
        boolean isRateFileSet = JamProperties.isSet(RATE_FILE_PROPERTY);
        
        if (isRateFileSet && !isGainRateSet && !isLossRateSet)
            return loadRateFile();

        if (isGainRateSet && isLossRateSet)
            return createUniform();

        throw new IllegalStateException("Missing or conflicting CNA rate properties.");
    }

    private static SegmentCNARateModel loadRateFile() {
        return RateLoader.load(JamProperties.getRequired(RATE_FILE_PROPERTY));
    }

    private static SegmentCNARateModel createUniform() {
        Probability gainRate = Probability.parse(JamProperties.getRequired(GAIN_RATE_PROPERTY));
        Probability lossRate = Probability.parse(JamProperties.getRequired(LOSS_RATE_PROPERTY));

        SegmentCNARateMatrix gainMatrix = SegmentCNARateMatrix.uniform(CNAType.GAIN, gainRate);
        SegmentCNARateMatrix lossMatrix = SegmentCNARateMatrix.uniform(CNAType.LOSS, lossRate);

        return new SegmentCNARateModel(gainMatrix, lossMatrix);
    }

    /**
     * Returns the event set for a given genome segment and copy
     * number.
     *
     * @param segment the genome segment of interest.
     *
     * @param copyNum the copy number of interest.
     *
     * @return the event set for the specified genome segment and copy
     * number.
     *
     * @throws IllegalArgumentException if the copy number is outside
     * of the valid range (less than zero or greater than the maximum
     * number specified by the {@code SegmentCNGenotype} class).
     */
    public EventSet<CNAType> getEventSet(GenomeSegment segment, int copyNum) {
        return eventSets.getEventSet(segment, copyNum);
    }

    /**
     * Returns the rate (probability per cell division) of copy-number
     * gains for a given genome segment and its current copy number.
     *
     * @param segment the segment of interest.
     *
     * @param copyNumber the current copy number of the input segment.
     *
     * @return the rate of copy-number gains for the specified genome
     * segment and copy number.
     *
     * @throws IllegalArgumentException if the copy number is outside
     * of the valid range (less than zero or greater than the maximum
     * number specified by the {@code SegmentCNGenotype} class).
     */
    public Probability getGainRate(GenomeSegment segment, int copyNumber) {
        return getRate(gainRates, segment, copyNumber);
    }

    private static Probability getRate(SegmentCNARateMatrix matrix, GenomeSegment segment, int copyNumber) {
        return matrix.getRate(segment, copyNumber);
    }

    /**
     * Returns the rate (probability per cell division) of copy-number
     * losses for a given genome segment and its current copy number.
     *
     * @param segment the segment of interest.
     *
     * @param copyNumber the current copy number of the input segment.
     *
     * @return the rate of copy-number losses for the specified genome
     * segment and copy number.
     *
     * @throws IllegalArgumentException if the copy number is outside
     * of the valid range (less than zero or greater than the maximum
     * number specified by the {@code SegmentCNGenotype} class).
     */
    public Probability getLossRate(GenomeSegment segment, int copyNumber) {
        return getRate(lossRates, segment, copyNumber);
    }

    /**
     * Returns the rate (probability per cell division) of whole
     * genome doubling.
     *
     * @return the rate (probability per cell division) of whole
     * genome doubling. 
     */
    public Probability getWGDRate() {
        return rateWGD;
    }

    /**
     * Simulates the possible mutation of a genotype during cell
     * division.
     *
     * @param parent the genotype of the parent cell.
     *
     * @return either a new daughter genotype (if one or more copy
     * number changes occur) or the original parent genotype (if no
     * copy number changes occur).
     */
    public SegmentCNGenotype mutate(SegmentCNGenotype parent) {
        if (rateWGD.accept())
            return parent.doubleWG();

        SegmentCNGenotype daughter = parent;

        for (GenomeSegment segment : GenomeSegment.list())
            daughter = mutate(daughter, segment);

        return daughter;
    }

    private SegmentCNGenotype mutate(SegmentCNGenotype parent, GenomeSegment segment) {
        CNAType cnaType = selectEvent(parent, segment);

        switch (cnaType) {
        case GAIN:
            return parent.gain(segment);

        case LOSS:
            return parent.lose(segment);

        case NONE:
            return parent;

        default:
            throw new IllegalStateException("Unknown CNA type.");
        }
    }

    private CNAType selectEvent(SegmentCNGenotype parent, GenomeSegment segment) {
        int copyNum = parent.count(segment);
        return getEventSet(segment, copyNum).select();
    }

    // -------------------------------------------------------------- //

    private static final class RateLoader {
        private final String fileName;

        private final SegmentCNARateMatrix gainMatrix;
        private final SegmentCNARateMatrix lossMatrix;

        private int fieldCount;
        private List<String> rateLines;
        private List<String[]> rateFields;

        private RateLoader(String fileName) {
            this.fileName = fileName;
            this.gainMatrix = SegmentCNARateMatrix.create(CNAType.GAIN);
            this.lossMatrix = SegmentCNARateMatrix.create(CNAType.LOSS);
        }

        private static SegmentCNARateModel load(String fileName) {
            RateLoader loader = new RateLoader(fileName);
            return loader.load();
        }

        private SegmentCNARateModel load() {
            rateLines = DataReader.read(fileName, RegexUtil.PYTHON_COMMENT);

            if (rateLines.isEmpty())
                throw new IllegalStateException("No data in rate file.");

            fieldCount = RegexUtil.countFields(RegexUtil.COMMA, rateLines.get(0));
            rateFields = RegexUtil.split(RegexUtil.COMMA, rateLines, fieldCount);

            switch (fieldCount) {
            case 3:
                parseSegmentSpecific();
                break;

            case 4:
                parseSegmentAndCopyNumberSpecific();
                break;

            default:
                throw new IllegalStateException("Rate data lines must contain three or four fields.");
            }

            return new SegmentCNARateModel(gainMatrix, lossMatrix);
        }

        private void parseSegmentSpecific() {
            for (String[] fields : rateFields)
                parseSegmentSpecific(fields);
        }

        private void parseSegmentSpecific(String[] fields) {
            GenomeSegment segment = GenomeSegment.require(fields[0]);
            CNAType       cnaType = CNAType.instance(fields[1]);
            Probability   rate    = Probability.parse(fields[2]);

            setRates(segment, cnaType, rate);
        }

        private void setRates(GenomeSegment segment, CNAType cnaType, Probability rate) {
            setRates(matrixFor(cnaType), segment, rate);
        }

        private SegmentCNARateMatrix matrixFor(CNAType cnaType) {
            switch (cnaType) {
            case GAIN:
                return gainMatrix;

            case LOSS:
                return lossMatrix;

            default:
                throw new IllegalStateException("Invalid event type.");
            }
        }

        private void setRates(SegmentCNARateMatrix matrix, GenomeSegment segment, Probability rate) {
            for (int copyNum = 1; copyNum < matrix.ncol(); ++copyNum)
                if (!matrix.isAbsorbing(copyNum))
                    matrix.setRate(segment, copyNum, rate);
        }

        private void parseSegmentAndCopyNumberSpecific() {
            for (String[] fields : rateFields)
                parseSegmentAndCopyNumberSpecific(fields);
        }

        private void parseSegmentAndCopyNumberSpecific(String[] fields) {
            GenomeSegment segment = GenomeSegment.require(fields[0]);
            int           copyNum = Integer.parseInt(fields[1]);
            CNAType       cnaType = CNAType.instance(fields[2]);
            Probability   rate    = Probability.parse(fields[3]);

            matrixFor(cnaType).setRate(segment, copyNum, rate);
        }
    }
}
