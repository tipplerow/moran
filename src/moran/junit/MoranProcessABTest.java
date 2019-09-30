
package moran.junit;

import java.util.List;

import jam.bravais.Lattice;

import moran.ab.ABCell;
import moran.ab.ABConfig;
import moran.ab.ABType;
import moran.cell.Cell;
import moran.driver.MoranProcess;
import moran.space.Space;

import org.junit.*;
import static org.junit.Assert.*;

public class MoranProcessABTest {
    static {
        System.setProperty(ABConfig.FITNESS_RATIO_PROPERTY, "1.10");
        System.setProperty(ABConfig.MUTATION_RATE_PROPERTY, "0.01");
    }

    private Space createSpace() {
        Lattice<Cell> lattice = Lattice.parse("HEXAGONAL; 1.0; 100, 100");
        lattice.fill(ABType.A.objectFactory());

        return Space.lattice(lattice);
    }

    private MoranProcess createProcess() {
        return MoranProcess.initialize(createSpace());
    }

    @Test public void testRun() {
        MoranProcess process = createProcess();
        System.out.println(process.getMeanFitness());

        for (int step = 0; step < 100; ++step) {
            process.executeTimeStep();

            System.out.println(String.format("STEP: %5d; CLOCK: %10.4f; FITNESS: %6.4f",
                                             step, process.getTimeClock(), process.getMeanFitness()));
        }
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.MoranProcessABTest");
    }
}
