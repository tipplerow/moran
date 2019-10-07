
package moran.report;

import java.text.DecimalFormat;

import jam.report.LineBuilder;
import jam.report.ReportRecord;
import jam.sim.StepRecord;

import moran.driver.MoranDriver;

/**
 * Provides a base class for all report records with the trial index,
 * time step, and continuous time clock set automatically at the time
 * of collection.
 */
public abstract class MoranRecord extends StepRecord implements ReportRecord {
    private final double timeClock;

    private static final DecimalFormat TIME_CLOCK_FORMAT = new DecimalFormat("#0.0#####");

    /**
     * Creates a new Moran record with the trial index, time step, and
     * continuous time clock assigned from a driver application.
     *
     * @param driver the active driver application.
     */
    protected MoranRecord(MoranDriver driver) {
        super(driver.getTrialIndex(), driver.getTimeStep());
        this.timeClock = driver.getTimeClock();
    }

    /**
     * Returns the value of the continuous time clock when this record
     * was collected.
     *
     * @return the value of the continuous time clock when this record
     * was collected.
     */
    public double getTimeClock() {
        return timeClock;
    }

    @Override public String formatLine() {
        LineBuilder builder = LineBuilder.csv();

        builder.append(getTrialIndex());
        builder.append(getTimeStep());
        builder.append(getTimeClock(), TIME_CLOCK_FORMAT);

        return builder.toString();
    }

    @Override public String getHeaderLine() {
        LineBuilder builder = LineBuilder.csv();

        builder.append("trialIndex");
        builder.append("stepIndex");
        builder.append("timeClock");

        return builder.toString();
    }
}
