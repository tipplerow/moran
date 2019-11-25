
package moran.junit;

import jam.bravais.Lattice;

import moran.ab.ABConfig;
import moran.ab.ABPhenotype;
import moran.ab.ABType;
import moran.cell.Cell;
import moran.space.Space;

import org.junit.*;
import static org.junit.Assert.*;

public class MoranProcessABTest extends MoranProcessTestBase {
    static {
        System.setProperty(ABConfig.FITNESS_RATIO_PROPERTY, "1.25");
        System.setProperty(ABConfig.MUTATION_RATE_PROPERTY, "0.01");
    }

    private static final double TOLERANCE = 0.0001;

    public MoranProcessABTest() {
        super(TOLERANCE);
    }

    @Test public void testRun() {
        runTest(25, 23.5079, 1.1671, true);
    }

    @Override protected Space createSpace() {
        Lattice<Cell> lattice = Lattice.parse("HEXAGONAL; 1.0; 100, 100");
        lattice.fill(ABType.A.objectFactory());

        return Space.lattice(lattice);
    }

    @Override protected ABPhenotype createPhenotype() {
        return ABPhenotype.INSTANCE;
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.MoranProcessABTest");
    }
}
