
package moran.report;

import java.util.Collection;

import jam.app.JamProperties;
import jam.report.ReportWriter;
import jam.sim.StepRecordCache;

import moran.driver.MoranDriver;
import moran.space.SpaceView;

/**
 * Reports the mean fitness of the cell population for each trial and
 * time step.
 */
public final class MeanCopyNumberReport extends MoranReport {
    //
    // All mean fitness records indexed by trial and time step...
    //
    private final StepRecordCache<MeanCopyNumberRecord> cache = StepRecordCache.create();

    private MeanCopyNumberReport(MoranDriver driver) {
        super(driver);
    }

    /**
     * Name of the system property that specifies whether to run the
     * mean fitness report.
     */
    public static final String RUN_REPORT_PROPERTY = "moran.report.runMeanCopyNumberReport";

    /**
     * Creates a new report for a given driver application.
     *
     * @param driver the governing driver application.
     *
     * @return the new report object.
     */
    public static MeanCopyNumberReport create(MoranDriver driver) {
        return new MeanCopyNumberReport(driver);
    }

    /**
     * Determines whether the mean fitness report will be executed.
     *
     * @return {@code true} iff the mean fitness report should be executed.
     */
    public static boolean reportRequested() {
        return JamProperties.getOptionalBoolean(RUN_REPORT_PROPERTY, false);
    }

    @Override public void initializeSimulation() {
        if (!cache.isEmpty())
            throw new IllegalStateException("Expected an empty record cache.");
    }

    @Override public void initializeTrial() {
        Collection<MeanCopyNumberRecord> trialRecords =
            cache.lookupTrial(getTrialIndex());

        if (!trialRecords.isEmpty())
            throw new IllegalStateException("Expected an empty trial cache.");
    }

    @Override public void processStep() {
        cache.add(MeanCopyNumberRecord.create(getDriver()));
    }

    @Override public void finalizeTrial() {
        //
        // Nothing special needed at the end of a trial...
        //
    }

    @Override public void finalizeSimulation() {
        ReportWriter.write(getReportDir(), cache);
    }
}
