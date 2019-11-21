
package moran.report;

import java.text.DecimalFormat;

import jam.report.LineBuilder;
import jam.util.CollectionUtil;

import moran.cell.Cell;
import moran.driver.MoranDriver;
import moran.segment.GenomeSegment;
import moran.segment.SegmentCNGenotype;

/**
 * Records the mean copy number across the cell population for each
 * genome segment.
 */
public class MeanCopyNumberRecord extends MoranRecord {
    private final double[] meanCN;

    private static final DecimalFormat MEAN_COPY_NUMBER_FORMAT = new DecimalFormat("#0.0#####");

    private MeanCopyNumberRecord(MoranDriver driver) {
        super(driver);
        this.meanCN = computeMeanCN(driver);
    }

    private double[] computeMeanCN(MoranDriver driver) {
        double[] meanCN = new double[GenomeSegment.count()];

        for (GenomeSegment segment : GenomeSegment.list())
            meanCN[segment.indexOf()] = computeMeanCN(driver, segment);

        return meanCN;
    }

    private double computeMeanCN(MoranDriver driver, GenomeSegment segment) {
        return CollectionUtil.average(driver.listCells(), cell -> getCopyNumber(cell, segment));
    }

    private static double getCopyNumber(Cell cell, GenomeSegment segment) {
        return ((SegmentCNGenotype) cell.getGenotype()).count(segment);
    }

    /**
     * Base name for the fitness trajectory report.
     */
    public static final String MEAN_COPY_NUMBER_BASE_NAME = "mean-copy-number.csv";

    /**
     * Creates a new record with mean copy numbers computed for the
     * current state of the driver application.
     *
     * @param driver the active driver application.
     *
     * @return the mean copy number record for the current state of
     * the driver application.
     */
    public static MeanCopyNumberRecord create(MoranDriver driver) {
        return new MeanCopyNumberRecord(driver);
    }

    /**
     * Returns the mean copy number for a genome segment at the time
     * this record was collected.
     *
     * @param segment the segment of interest.
     *
     * @return the mean copy number for the specified genome segment
     * at the time this record was collected.
     */
    public double getMeanCopyNumber(GenomeSegment segment) {
        return meanCN[segment.indexOf()];
    }

    @Override public String formatLine() {
        LineBuilder builder = LineBuilder.csv();
        builder.append(super.formatLine());

        for (double mean : meanCN)
            builder.append(mean, MEAN_COPY_NUMBER_FORMAT);

        return builder.toString();
    }

    @Override public String getBaseName() {
        return MEAN_COPY_NUMBER_BASE_NAME;
    }

    @Override public String getHeaderLine() {
        LineBuilder builder = LineBuilder.csv();
        builder.append(super.getHeaderLine());

        for (GenomeSegment segment : GenomeSegment.list())
            builder.append(segment.getKey());

        return builder.toString();
    }
}
