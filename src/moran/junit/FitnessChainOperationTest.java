
package moran.junit;

import jam.junit.NumericTestBase;

import moran.segment.FitnessChainOperation;

import org.junit.*;
import static org.junit.Assert.*;

public class FitnessChainOperationTest extends NumericTestBase {
    private static final double TOLERANCE = 1.0E-06;

    public FitnessChainOperationTest() {
        super(TOLERANCE);
    }

    @Test public void testAdd() {
        assertDouble(1.10, FitnessChainOperation.ADD.computeFitness(0.05, 0));
        assertDouble(1.05, FitnessChainOperation.ADD.computeFitness(0.05, 1));
        assertDouble(1.0,  FitnessChainOperation.ADD.computeFitness(0.05, 2));
        assertDouble(1.05, FitnessChainOperation.ADD.computeFitness(0.05, 3));
        assertDouble(1.10, FitnessChainOperation.ADD.computeFitness(0.05, 4));
        assertDouble(1.15, FitnessChainOperation.ADD.computeFitness(0.05, 5));
        assertDouble(1.20, FitnessChainOperation.ADD.computeFitness(0.05, 6));
    }

    @Test public void testMultiply() {
        assertDouble(1.1025,   FitnessChainOperation.MULTIPLY.computeFitness(0.05, 0));
        assertDouble(1.05,     FitnessChainOperation.MULTIPLY.computeFitness(0.05, 1));
        assertDouble(1.0,      FitnessChainOperation.MULTIPLY.computeFitness(0.05, 2));
        assertDouble(1.05,     FitnessChainOperation.MULTIPLY.computeFitness(0.05, 3));
        assertDouble(1.1025,   FitnessChainOperation.MULTIPLY.computeFitness(0.05, 4));
        assertDouble(1.157625, FitnessChainOperation.MULTIPLY.computeFitness(0.05, 5));
    }


    @Test public void testNone() {
        assertDouble(1.05, FitnessChainOperation.NONE.computeFitness(0.05, 0));
        assertDouble(1.05, FitnessChainOperation.NONE.computeFitness(0.05, 1));
        assertDouble(1.0,  FitnessChainOperation.NONE.computeFitness(0.05, 2));
        assertDouble(1.05, FitnessChainOperation.NONE.computeFitness(0.05, 3));
        assertDouble(1.05, FitnessChainOperation.NONE.computeFitness(0.05, 4));
        assertDouble(1.05, FitnessChainOperation.NONE.computeFitness(0.05, 5));
        assertDouble(1.05, FitnessChainOperation.NONE.computeFitness(0.05, 6));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.FitnessChainOperationTest");
    }
}
