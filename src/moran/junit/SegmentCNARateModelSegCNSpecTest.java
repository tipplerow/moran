
package moran.junit;

import jam.junit.NumericTestBase;
import jam.math.Probability;

import moran.cna.CNAType;
import moran.segment.GenomeSegment;
import moran.segment.SegmentCNARateModel;
import moran.segment.SegmentCNGenotype;

import org.junit.*;
import static org.junit.Assert.*;

public class SegmentCNARateModelSegCNSpecTest extends NumericTestBase {
    static {
        System.setProperty(GenomeSegment.DEFINITION_FILE_PROPERTY, "data/test/test_segment2.txt");
        System.setProperty(SegmentCNGenotype.MAX_COPY_NUMBER_PROPERTY, "4");

        System.setProperty(SegmentCNARateModel.WGD_RATE_PROPERTY, "0.0123");
        System.setProperty(SegmentCNARateModel.RATE_FILE_PROPERTY, "data/test/cna_rate2.csv");
    }

    private static final GenomeSegment P6 = GenomeSegment.instance("6p");
    private static final GenomeSegment Q9 = GenomeSegment.instance("9q");

    @Test public void testGlobal() {
        SegmentCNARateModel model = SegmentCNARateModel.global();

        assertRate(0.0123, model.getWGDRate());

        assertRate(0.0,   model.getGainRate(P6, 0)); // Cannot gain from zero copy number
        assertRate(0.011, model.getGainRate(P6, 1));
        assertRate(0.022, model.getGainRate(P6, 2));
        assertRate(0.033, model.getGainRate(P6, 3));
        assertRate(0.0,   model.getGainRate(P6, 4)); // Cannot gain beyond the maximum copy number

        assertRate(0.0,  model.getLossRate(P6, 0)); // Cannot lose from zero copy number
        assertRate(0.11, model.getLossRate(P6, 1));
        assertRate(0.22, model.getLossRate(P6, 2));
        assertRate(0.33, model.getLossRate(P6, 3));
        assertRate(0.44, model.getLossRate(P6, 4));

        assertRate(0.0,   model.getGainRate(Q9, 0)); // Cannot gain from zero copy number
        assertRate(0.055, model.getGainRate(Q9, 1));
        assertRate(0.066, model.getGainRate(Q9, 2));
        assertRate(0.077, model.getGainRate(Q9, 3));
        assertRate(0.0,   model.getGainRate(Q9, 4)); // Cannot gain beyond the maximum copy number

        assertRate(0.0,  model.getLossRate(Q9, 0)); // Cannot lose from zero copy number
        assertRate(0.55, model.getLossRate(Q9, 1));
        assertRate(0.66, model.getLossRate(Q9, 2));
        assertRate(0.77, model.getLossRate(Q9, 3));
        assertRate(0.88, model.getLossRate(Q9, 4));

        assertRate(0.0, model.getEventSet(P6, 0).getEventProbability(CNAType.GAIN));
        assertRate(0.0, model.getEventSet(P6, 0).getEventProbability(CNAType.LOSS));
        assertRate(1.0, model.getEventSet(P6, 0).getEventProbability(CNAType.NONE));

        assertRate(0.011 / (1.0 - 0.0123), model.getEventSet(P6, 1).getEventProbability(CNAType.GAIN));
        assertRate(0.11  / (1.0 - 0.0123), model.getEventSet(P6, 1).getEventProbability(CNAType.LOSS));

        assertRate(0.022 / (1.0 - 0.0123), model.getEventSet(P6, 2).getEventProbability(CNAType.GAIN));
        assertRate(0.22  / (1.0 - 0.0123), model.getEventSet(P6, 2).getEventProbability(CNAType.LOSS));

        assertRate(0.033 / (1.0 - 0.0123), model.getEventSet(P6, 3).getEventProbability(CNAType.GAIN));
        assertRate(0.33  / (1.0 - 0.0123), model.getEventSet(P6, 3).getEventProbability(CNAType.LOSS));

        assertRate(0.0, model.getEventSet(P6, 4).getEventProbability(CNAType.GAIN));
        assertRate(0.44 / (1.0 - 0.0123), model.getEventSet(P6, 4).getEventProbability(CNAType.LOSS));
    }

    private void assertRate(double expected, Probability actual) {
        assertDouble(expected, actual.doubleValue());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.SegmentCNARateModelSegCNSpecTest");
    }
}
