
package moran.driver;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import jam.app.JamLogger;
import jam.app.JamProperties;
import jam.dist.ExponentialDistribution;
import jam.math.IntRange;
import jam.math.JamRandom;
import jam.sim.DiscreteTimeSimulation;
import jam.vector.VectorUtil;

import moran.cell.Cell;
import moran.space.Space;
import moran.space.SpaceView;

/**
 * Provides a basic implementation of a spatial Moran simulation of an
 * evolving fixed-size cellular population.
 *
 * <p>In addition to providing bookkeeping and reporting functions,
 * this class implements the core process in all Moran simulations:
 * the cycle of cell death and division.  The cell cycle proceeds as
 * follows:
 *
 * <ol>
 *   <li>
 *      Select a cell {@code I} at random from the population, with
 *      equal probability for all cells; this cell dies and allows
 *      a neighbor to divide.
 *   </li>
 *   <li>
 *     Update the dimensionless time clock according to the fitness
 *     of the neighbors of cell {@code I}.
 *   </li>
 *   <li>
 *     Select another cell {@code J} at random from the neighbors of
 *     cell {@code I} with a probability proportional to the fitness
 *     of cell {@code J}.
 *   </li>
 *   <li>
 *     Replace cell {@code I} with a daughter of cell {@code J}.
 *   </li>
 * </ol>
 */
public abstract class MoranDriver extends DiscreteTimeSimulation {
    private final int trialTarget;
    private final int maxStepCount;
    private final int snapInterval;
    private final double fitnessThreshold;

    // The cell space for the active simulation trial...
    private Space space;

    // The average fitness of cells in the population...
    private double meanFitness;

    // The continuous (dimensionless) time elapsed in the active
    // simulation trial...
    private double timeClock;

    // The random number source...
    private final JamRandom random = JamRandom.global();

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

        this.fitnessThreshold = resolveFitnessThreshold();
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

    private static double resolveFitnessThreshold() {
        return JamProperties.getRequiredDouble(FITNESS_THRESHOLD_PROPERTY);
    }

    // =====================================================================
    // The following methods implement the core process in all spatial Moran
    // simulations: the cycle of cell death and division.
    // =====================================================================

    private void executeCellCycle() {
        //
        // (1) Select a cell "I" at random from the population, with
        // equal probability for all cells; this cell dies and allows
        // a neighbor to divide.
        //
        // (2) Update the dimensionless time clock according to the
        // fitness of the neighbors of cell "I".
        //
        // (3) Select another cell "J" at random from the neighbors of
        // cell "I", with a probability proportional to its fitness.
        //
        // (4) Replace cell "I" with a daughter of cell "J".
        //
        Cell deadCell = space.select();

        List<Cell> neighborCells = space.getNeighbors(deadCell);
        double[]   neighborFit   = getNeighborFitness(neighborCells);

        updateTimeClock(neighborFit);

        Cell neighbor = selectNeighbor(neighborCells, neighborFit);
        Cell daughter = neighbor.divide();

        space.replace(deadCell, daughter);
        updateMeanFitness(deadCell, daughter);
    }

    private static double[] getNeighborFitness(List<Cell> neighborCells) {
        double[] neighborFit = new double[neighborCells.size()];

        for (int k = 0; k < neighborCells.size(); ++k)
            neighborFit[k] = neighborCells.get(k).getFitness();

        return neighborFit;
    }

    private void updateTimeClock(double[] neighborFit) {
        timeClock += tick(neighborFit);
    }

    private double tick(double[] neighborFit) {
        //
        // Each division event is modeled as a Poisson process, so we
        // sample the elapased time from an exponential distribution
        // with rate parameter equal to the sum of the fitness...
        //
        double sum  = VectorUtil.sum(neighborFit);
        double time = ExponentialDistribution.sample(sum, random);

        // To make the elapsed time independent of the population size
        // and the coordination number of the lattice, we rescale the
        // elapsed time by both quantities.
        return time / (neighborFit.length * space.size());
    }

    private Cell selectNeighbor(List<Cell> neighborCells, double[] neighborFit) {
        double[] divisionProb = VectorUtil.copy(neighborFit);

        VectorUtil.normalize(divisionProb);
        int neighborIndex = random.selectPDF(divisionProb);

        return neighborCells.get(neighborIndex);
    }

    private void updateMeanFitness(Cell deadCell, Cell daughter) {
        meanFitness += (daughter.getFitness() - deadCell.getFitness()) / space.size();
    }

    // =====================================================================

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
     * Name of the system property that defines the fitness threshold:
     * simulation trials stop when the mean fitness of the population
     * exceeds this value.
     */
    public static final String FITNESS_THRESHOLD_PROPERTY = "moran.driver.fitnessThreshold";

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
     * Returns the fitness threshold: simulation trials stop when the
     * average fitness of the population exceeds this value.
     *
     * @return the fitness threshold.
     */
    public double getFitnessThreshold() {
        return fitnessThreshold;
    }

    /**
     * Returns the average fitness of the current cell population.
     *
     * @return the average fitness of the current cell population.
     */
    public double getMeanFitness() {
        return meanFitness;
    }

    /**
     * Returns a read-only view of the cellular space for the current
     * simulation trial.
     *
     * @return a read-only view of the cellular space for the current
     * simulation trial.
     */
    public SpaceView viewSpace() {
        return space;
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

        if (isSnapshotStep())
            recordSnapshot(getSnapshotDir());
    }

    /**
     * Logs a message to the console after every step.
     */
    protected void consoleLogStep() {
        int trialIndex = getTrialIndex();
        int timeStep   = getTimeStep();

        JamLogger.info("TRIAL: %4d; STEP: %5d; FITNESS: %.4f", getTrialIndex(), getTimeStep(), meanFitness);
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
    }

    private void writeRuntimeProperties() {
        PrintWriter writer = openWriter(PROPERTY_FILE_NAME);
        Map<String, String> properties = JamProperties.filter("jam.", "tumor.");

        for (Map.Entry<String, String> entry : properties.entrySet())
            writer.println(entry.getKey() + " = " + entry.getValue());

        writer.close();
    }

    @Override protected void finalizeSimulation() {
        autoClose();
    }

    @Override protected void initializeTrial() {
        space = createSpace();

        resetTimeClock();
        assignMeanFitness();
        //ReportManager.global().initializeTrial();
    }

    private void resetTimeClock() {
        timeClock = 0.0;
    }

    private void assignMeanFitness() {
        meanFitness = 0.0;

        for (Cell cell : space)
            meanFitness += cell.getFitness();

        meanFitness /= space.size();
    }

    @Override protected boolean continueTrial() {
        return (getTimeStep() < maxStepCount) && (meanFitness < fitnessThreshold);
    }

    @Override protected void advanceTrial() {
        //
        // For the simulation time to be independent of the sample
        // size, each cell in the population must have a chance (on
        // average) to die or divide in each discrete step, so we
        // execute the selection/death/division cycle once for each
        // cell in the population.
        //
        for (int cycleIndex = 0; cycleIndex < space.size(); ++cycleIndex)
            executeCellCycle();
    }

    @Override protected void finalizeTrial() {
        recordSnapshot(getReportDir());
        //ReportManager.global().finalizeTrial();
    }
}
