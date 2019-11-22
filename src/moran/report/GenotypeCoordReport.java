
package moran.report;

import java.io.PrintWriter;
import java.text.DecimalFormat;

import jam.app.JamProperties;
import jam.io.IOUtil;
import jam.math.IntRange;
import jam.report.LineBuilder;

import moran.cell.Cell;
import moran.cell.Genotype;
import moran.driver.MoranDriver;

/**
 * Reports cellular coordinates and genotype details at regular
 * intervals during a simulation.
 */
public final class GenotypeCoordReport extends MoranReport {
    private final int interval;
    private PrintWriter writer;

    private static final DecimalFormat TIME_CLOCK_FORMAT = new DecimalFormat("#0.0###");

    private GenotypeCoordReport(MoranDriver driver) {
        super(driver);
        this.interval = resolveInterval();
    }

    private static int resolveInterval() {
        return JamProperties.getRequiredInt(REPORT_INTERVAL_PROPERTY, IntRange.POSITIVE);
    }

    /**
     * Name of the system property that specifies whether to run the
     * genotype coordinate report.
     */
    public static final String RUN_REPORT_PROPERTY = "moran.report.runGenotypeCoordReport";

    /**
     * Name of the system property that specifies the reporting
     * interval (the number of time steps between snapshots).
     */
    public static final String REPORT_INTERVAL_PROPERTY = "moran.report.genotypeCoordReportInterval";

    /**
     * Name of the system property that specifies whether to run the
     * genotype coordinate report.
     */
    public static final String REPORT_FILE_NAME = "genotype-coord.csv";

    /**
     * Creates a new report for a given driver application.
     *
     * @param driver the governing driver application.
     *
     * @return the new report object.
     */
    public static GenotypeCoordReport create(MoranDriver driver) {
        return new GenotypeCoordReport(driver);
    }

    /**
     * Determines whether this report will be executed.
     *
     * @return {@code true} iff this report should be executed.
     */
    public static boolean reportRequested() {
        return JamProperties.getOptionalBoolean(RUN_REPORT_PROPERTY, false);
    }

    @Override public void initializeSimulation() {
        //
        // We need a cellular location (for the dimensionality) and
        // genotype (for the genotype data structure) to write the
        // header line, so wait until the first sample interval to
        // create the writer and write the header...
        //
    }

    @Override public void initializeTrial() {
        //
        // Nothing special needed at the end of a trial...
        //
    }

    @Override public void processStep() {
        if (isSampleStep(interval))
            writeGenotypeCoord();
    }

    private void writeGenotypeCoord() {
        if (writer == null) {
            openWriter();
            writeHeader();
        }

        for (Cell cell : listCells())
            writeGenotypeCoord(cell);

        writer.flush();
    }

    private void openWriter() {
        writer = IOUtil.openWriter(getReportFile(REPORT_FILE_NAME));
    }

    private void writeHeader() {
        LineBuilder builder = LineBuilder.csv();

        builder.append("trialIndex");
        builder.append("stepIndex");
        builder.append("timeClock");
        builder.append(coordHeader());
        builder.append(genotypeHeader());

        writer.println(builder.toString());
    }

    private void writeGenotypeCoord(Cell cell) {
        LineBuilder builder = LineBuilder.csv();

        builder.append(getTrialIndex());
        builder.append(getTimeStep());
        builder.append(getTimeClock(), TIME_CLOCK_FORMAT);
        builder.append(formatCoord(cell));
        builder.append(cell.getGenotype().format());

        writer.println(builder.toString());
    }

    @Override public void finalizeTrial() {
        //
        // Nothing special needed at the end of a trial...
        //
    }

    @Override public void finalizeSimulation() {
        writer.close();
    }
}
