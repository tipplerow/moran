
package moran.junit;

import jam.junit.NumericTestBase;
import jam.math.Probability;

import moran.segment.GenomeSegment;
import moran.segment.SegmentCNARateModel;
import moran.segment.SegmentCNGenotype;

import org.junit.*;
import static org.junit.Assert.*;

public class SegmentCNARateModelSegSpecTest extends NumericTestBase {
    static {
        System.setProperty(GenomeSegment.DEFINITION_FILE_PROPERTY, "data/test/test_segment2.txt");
        System.setProperty(SegmentCNGenotype.MAX_COPY_NUMBER_PROPERTY, "4");

        System.setProperty(SegmentCNARateModel.WGD_RATE_PROPERTY, "0.1111");
        System.setProperty(SegmentCNARateModel.RATE_FILE_PROPERTY, "data/test/cna_rate1.csv");
    }

    private static final GenomeSegment P6 = GenomeSegment.instance("6p");
    private static final GenomeSegment Q9 = GenomeSegment.instance("9q");

    @Test public void testGlobal() {
        SegmentCNARateModel model = SegmentCNARateModel.global();

        assertRate(0.1111, model.getWGDRate());

        assertRate(0.0,  model.getGainRate(P6, 0)); // Cannot gain from zero copy number
        assertRate(0.12, model.getGainRate(P6, 1));
        assertRate(0.12, model.getGainRate(P6, 2));
        assertRate(0.12, model.getGainRate(P6, 3));
        assertRate(0.0,  model.getGainRate(P6, 4)); // Cannot gain beyond the maximum copy number

        assertRate(0.0,  model.getLossRate(P6, 0)); // Cannot lose from zero copy number
        assertRate(0.34, model.getLossRate(P6, 1));
        assertRate(0.34, model.getLossRate(P6, 2));
        assertRate(0.34, model.getLossRate(P6, 3));
        assertRate(0.34, model.getLossRate(P6, 4));

        assertRate(0.0,  model.getGainRate(Q9, 0)); // Cannot gain from zero copy number
        assertRate(0.56, model.getGainRate(Q9, 1));
        assertRate(0.56, model.getGainRate(Q9, 2));
        assertRate(0.56, model.getGainRate(Q9, 3));
        assertRate(0.0,  model.getGainRate(Q9, 4)); // Cannot gain beyond the maximum copy number

        assertRate(0.0,  model.getLossRate(Q9, 0)); // Cannot lose from zero copy number
        assertRate(0.78, model.getLossRate(Q9, 1));
        assertRate(0.78, model.getLossRate(Q9, 2));
        assertRate(0.78, model.getLossRate(Q9, 3));
        assertRate(0.78, model.getLossRate(Q9, 4));
    }

    private void assertRate(double expected, Probability actual) {
        assertDouble(expected, actual.doubleValue());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.SegmentCNARateModelSegSpecTest");
    }
}
