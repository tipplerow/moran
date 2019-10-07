
package moran.report;

import moran.driver.MoranDriver;
import moran.space.SpaceView;

/**
 * Provides a base class for simulation reports that may process data
 * after each completed time step and simulation trial.
 */
public abstract class MoranReport {
    private final MoranDriver driver;

    /**
     * Creates a new report for a fixed driver application.
     *
     * @param driver the governing driver application.
     */
    protected MoranReport(MoranDriver driver) {
        this.driver = driver;
    }

    /**
     * Initializes this report at the start of a new simulation.
     */
    public abstract void initializeSimulation();

    /**
     * Initializes this report at the start of a new simulation trial.
     */
    public abstract void initializeTrial();

    /**
     * Processes the results of the latest completed time step.
     */
    public abstract void processStep();

    /**
     * Reports the results of the latest completed simulation trial.
     */
    public abstract void finalizeTrial();

    /**
     * Reports the results of the completed simulation.
     */
    public abstract void finalizeSimulation();

    /**
     * Returns the global driver application.
     *
     * @return the global driver application.
     */
    public MoranDriver getDriver() {
        return driver;
    }

    /**
     * Returns the index of the latest completed time step.
     *
     * @return the index of the latest completed time step.
     */
    public int getTimeStep() {
        return driver.getTimeStep();
    }

    /**
     * Returns the index of the latest completed simulation trial.
     *
     * @return the index of the latest completed simulation trial.
     */
    public int getTrialIndex() {
        return driver.getTrialIndex();
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
        return driver.getTimeClock();
    }

    /**
     * Returns a read-only view of the cellular space for the current
     * simulation trial.
     *
     * @return a read-only view of the cellular space for the current
     * simulation trial.
     */
    public SpaceView viewSpace() {
        return driver.viewSpace();
    }

    /**
     * Identifies time steps when the system state must be sampled and
     * this report must be updated and/or recorded.
     *
     * @param sampleInterval the report-specific sampling (update)
     * interval (expressed as a number of time steps).
     *
     * @return {@code true} iff this report should be updated for the
     * latest completed time step.
     */
    public boolean isSampleStep(int sampleInterval) {
        return (sampleInterval > 0) && (getTimeStep() > 0) && (getTimeStep() % sampleInterval == 0);
    }
}
