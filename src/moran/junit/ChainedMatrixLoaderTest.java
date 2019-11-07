
package moran.junit;

import jam.matrix.JamMatrix;

import moran.segment.GenomeSegment;
import moran.segment.SegmentCNGenotype;
import moran.segment.SegmentCNPhenotype;

import org.junit.*;
import static org.junit.Assert.*;

public class ChainedMatrixLoaderTest {
    static {
        System.setProperty(GenomeSegment.DEFINITION_FILE_PROPERTY, "data/test/test_segment.txt");
        System.setProperty(SegmentCNGenotype.MAX_COPY_NUMBER_PROPERTY, "5");
        System.setProperty(SegmentCNPhenotype.FITNESS_MATRIX_FILE_PROPERTY, "data/test/chained_phenotype.csv");
        System.setProperty(SegmentCNPhenotype.FITNESS_CHAIN_OPERATION_PROPERTY, "ADD");
    }

    private static final JamMatrix EXPECTED =
        JamMatrix.byrow(3, 6,
                        0.96, 0.98, 1.00, 1.05, 1.10, 1.15,
                        1.04, 1.02, 1.00, 0.95, 0.90, 0.85,
                        1.00, 1.00, 1.00, 1.03, 1.06, 1.09);

    @Test public void testLoader() {
        assertEquals(EXPECTED, SegmentCNPhenotype.global().viewFitnessMatrix());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.ChainedMatrixLoaderTest");
    }
}
