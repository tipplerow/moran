
package moran.junit;

import jam.junit.NumericTestBase;

import moran.cell.Phenotype;
import moran.driver.MoranProcess;
import moran.space.Space;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class MoranProcessTestBase extends NumericTestBase {
    /**
     * Creates a new test with a given floating-point tolerance.
     *
     * @param tolerance the tolerance to allow in floating-point
     * equality tests.
     */
    protected MoranProcessTestBase(double tolerance) {
        super(tolerance);
    }

    /**
     * Creates the cellular space to simulate.
     *
     * @return the cellular space to simulate.
     */
    protected abstract Space createSpace();

    /**
     * Creates the governing cellular phenotype model.
     *
     * @return the governing cellular phenotype model.
     */
    protected abstract Phenotype createPhenotype();

    /**
     * Executes the Moran process.
     *
     * @param stepCount the number of steps to execute.
     *
     * @param verbose whether to log the intermediate steps of the
     * simulation.
     *
     * @return the executed Moran process.
     */
    protected MoranProcess runProcess(int stepCount, boolean verbose) {
        MoranProcess process =
            MoranProcess.initialize(createSpace(), createPhenotype());

        for (int stepIndex = 0; stepIndex < stepCount; ++stepIndex) {
            process.executeTimeStep();

            if (verbose)
                logProcess(process, stepIndex);
        }

        return process;
    }

    private void logProcess(MoranProcess process, int stepIndex) {
        System.out.println(String.format("STEP: %5d; CLOCK: %10.4f; FITNESS: %6.4f",
                                         stepIndex, process.getTimeClock(), process.getMeanFitness()));
    }

    /**
     * Executes the Moran process and tests the results.
     *
     * @param stepCount the number of steps to execute.
     *
     * @param timeClock the expected value of the continuous time
     * clock at the end of the simulation.
     *
     * @param meanFitness the expected value of the average cell
     * fitness at the end of the simulation.
     *
     * @param verbose whether to log the intermediate steps of the
     * simulation.
     */
    protected void runTest(int     stepCount,
                           double  timeClock,
                           double  meanFitness,
                           boolean verbose) {
        MoranProcess process =
            runProcess(stepCount, verbose);

        assertDouble(timeClock, process.getTimeClock());
        assertDouble(meanFitness, process.getMeanFitness());
    }
}
