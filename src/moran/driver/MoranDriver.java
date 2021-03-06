
package moran.driver;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.math.DoubleRange;
import jam.math.IntRange;
import jam.math.Point;
import jam.sim.DiscreteTimeSimulation;

import moran.cell.Cell;
import moran.cell.Phenotype;
import moran.report.GenotypeCoordReport;
import moran.report.MeanCopyNumberReport;
import moran.report.MeanFitnessReport;
import moran.report.MoranReport;
import moran.space.Space;
import moran.space.SpaceView;

/**
 * Provides a base class for applications running simulations of
 * spatial Moran models.
 */
public abstract class MoranDriver extends DiscreteTimeSimulation {
    private final int trialTarget;
    private final int maxStepCount;
    private final int snapInterval;

    private final DoubleRange fitnessRange;

    // All reports to run...
    private final List<MoranReport> reports = new ArrayList<MoranReport>();

    // The active Moran process for the current simulation trial...
    private MoranProcess process;

    /**
     * Creates a new simulation instance and reads system properties
     * from a set of property files.
     *
     * @param propertyFiles files containing system properties that
     * define the simulation parameters (may be empty if properties
     * are specified elsewhere).
     */
    protected MoranDriver(String... propertyFiles) {
        super(propertyFiles);

        this.trialTarget  = resolveTrialTarget();
        this.maxStepCount = resolveMaxStepCount();
        this.snapInterval = resolveSnapInterval();
        this.fitnessRange = resolveFitnessRange();

        registerReports();
    }

    private static int resolveTrialTarget() {
        return JamProperties.getRequiredInt(TRIAL_TARGET_PROPERTY, IntRange.POSITIVE);
    }

    private static int resolveMaxStepCount() {
        return JamProperties.getRequiredInt(MAX_STEP_COUNT_PROPERTY, IntRange.POSITIVE);
    }

    private static int resolveSnapInterval() {
        return JamProperties.getOptionalInt(SNAPSHOT_INTERVAL_PROPERTY, 0);
    }

    private static DoubleRange resolveFitnessRange() {
        if (JamProperties.isSet(FITNESS_RANGE_PROPERTY))
            return DoubleRange.parse(JamProperties.getRequired(FITNESS_RANGE_PROPERTY));
        else
            return DoubleRange.POSITIVE;
    }

    private void registerReports() {
        //
        // Register the reports common to most applications...
        //
        if (GenotypeCoordReport.reportRequested())
            registerReport(GenotypeCoordReport.create(this));

        if (MeanCopyNumberReport.reportRequested())
            registerReport(MeanCopyNumberReport.create(this));

        if (MeanFitnessReport.reportRequested())
            registerReport(MeanFitnessReport.create(this));
    }

    /**
     * Name of the system property that defines the number of
     * independent simulation trials to execute.
     */
    public static final String TRIAL_TARGET_PROPERTY = "moran.driver.trialTarget";

    /**
     * Name of the system property that defines the maximum number of
     * time steps to execute on each simulation trial.
     */
    public static final String MAX_STEP_COUNT_PROPERTY = "moran.driver.maxStepCount";

    /**
     * Name of the system property that defines the allowed fitness
     * range: simulation trials stop if the mean fitness of the cell
     * population moves outside of this range.
     */
    public static final String FITNESS_RANGE_PROPERTY = "moran.driver.fitnessRange";

    /**
     * Name of the system property that defines the number of time
     * steps between snapshot recording: any positive integer will
     * initiate snapshot recording.
     */
    public static final String SNAPSHOT_INTERVAL_PROPERTY = "moran.driver.snapshotInterval";

    /**
     * Name of the output file containing all relevant system
     * properties that were defined at the time of execution.
     */
    public static final String PROPERTY_FILE_NAME = "runtime.prop";

    /**
     * Prefix for subdirectories containing time-step snapshots.
     */
    public static final String SUBDIR_PREFIX = "T";

    /**
     * Creates the initial cellular space for the next simulation trial.
     *
     * @return the initial cellular space for the next simulation trial.
     */
    protected abstract Space createSpace();

    /**
     * Creates the governing cellular fitness (phenotype) model.
     *
     * @return the governing cellular fitness (phenotype) model.
     */
    protected abstract Phenotype createPhenotype();

    /**
     * Writes snapshot reports for the most recently executed time
     * step into a specified output directory.
     *
     * @param snapshotDir the destination for the snapshot reports.
     */
    protected abstract void recordSnapshot(File snapshotDir);

    /**
     * Returns the number of independent simulation trials to execute.
     *
     * @return the number of independent simulation trials to execute.
     */
    public int getTrialTarget() {
        return trialTarget;
    }

    /**
     * Returns the maximum number of time steps to execute on each
     * simulation trial.
     *
     * @return the maximum number of time steps to execute on each
     * simulation trial.
     */
    public int getMaxStepCount() {
        return maxStepCount;
    }

    /**
     * Returns the allowed fitness range: simulation trials stop when
     * the average fitness of the population moves out of this range.
     *
     * @return the allowed fitness range.
     */
    public DoubleRange getFitnessRange() {
        return fitnessRange;
    }

    /**
     * Returns the average fitness of the current cell population.
     *
     * @return the average fitness of the current cell population.
     */
    public double getMeanFitness() {
        return process.getMeanFitness();
    }

    /**
     * Returns the continuous elapsed time in the current simulation
     * trial (normalized by the size of the cellular population and
     * the coordination number of the underlying space).
     *
     * @return the continuous elapsed time in the current simulation
     * trial.
     */
    public double getTimeClock() {
        return process.getTimeClock();
    }

    /**
     * Returns a read-only list view of the cells in the simulation.
     *
     * @return a read-only list view of the cells in the simulation.
     */
    public List<Cell> listCells() {
        return viewSpace().list();
    }

    /**
     * Returns the spatial location of cell in the simulation space.
     *
     * @param cell the cell to locate.
     *
     * @return the spatial location of the cell ({@code null} if the
     * cell is not present).
     */
    public Point locateCell(Cell cell) {
        return viewSpace().locate(cell);
    }

    /**
     * Returns a read-only view of the cellular space for the current
     * simulation trial.
     *
     * @return a read-only view of the cellular space for the current
     * simulation trial.
     */
    public SpaceView viewSpace() {
        return process.viewSpace();
    }

    /**
     * Registers a report to run.
     *
     * <p>Subclasses should register reports in their constructor,
     * prior to the initialization of the simulation, to ensure that
     * the reports are properly initialized.
     *
     * @param report the report to run.
     */
    protected final void registerReport(MoranReport report) {
        reports.add(report);
    }

    /**
     * Records the new state of the simulation system after a time
     * step has been executed.
     *
     * <p>This base class writes a console log message and records a
     * snapshot if the time step is a snapshot step.
     */
    protected void recordStep() {
        consoleLogStep();

        for (MoranReport report : reports)
            report.processStep();

        if (isSnapshotStep())
            recordSnapshot(getSnapshotDir());
    }

    /**
     * Logs a message to the console after every step.
     */
    protected void consoleLogStep() {
        JamLogger.info("TRIAL: %4d; STEP: %5d; FITNESS: %.4f", getTrialIndex(), getTimeStep(), getMeanFitness());
    }

    private boolean isSnapshotStep() {
        return (snapInterval > 0) && (getTimeStep() % snapInterval == 0);
    }

    private File getSnapshotDir() {
        return getSnapshotDir(getTimeStep());
    }

    private File getSnapshotDir(int timeStep) {
        return new File(getReportDir(), formatSnapshotSubDir(timeStep));
    }

    public static String formatSnapshotSubDir(int timeStep) {
        return String.format("%s%05d", SUBDIR_PREFIX, timeStep);
    }

    @Override protected void initializeSimulation() {
        writeRuntimeProperties();

        for (MoranReport report : reports)
            report.initializeSimulation();
    }

    private void writeRuntimeProperties() {
        PrintWriter writer = openWriter(PROPERTY_FILE_NAME);
        Map<String, String> properties = JamProperties.filter("jam.", "moran.");

        for (Map.Entry<String, String> entry : properties.entrySet())
            writer.println(entry.getKey() + " = " + entry.getValue());

        writer.close();
    }

    @Override protected void finalizeSimulation() {
        for (MoranReport report : reports)
            report.finalizeSimulation();

        autoClose();
    }

    @Override protected void initializeTrial() {
        process = MoranProcess.initialize(createSpace(), createPhenotype());

        for (MoranReport report : reports)
            report.initializeTrial();
    }

    @Override protected boolean continueTrial() {
        return (getTimeStep() < maxStepCount) && fitnessRange.contains(getMeanFitness());
    }

    @Override protected void advanceTrial() {
        process.executeTimeStep();
        recordStep();
    }

    @Override protected void finalizeTrial() {
        recordSnapshot(getReportDir());

        for (MoranReport report : reports)
            report.finalizeTrial();
    }
}
