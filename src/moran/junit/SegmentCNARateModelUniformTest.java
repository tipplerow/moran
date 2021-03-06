
package moran.junit;

import jam.junit.NumericTestBase;
import jam.math.DoubleUtil;
import jam.math.Probability;

import moran.segment.GenomeSegment;
import moran.segment.SegmentCNARateModel;
import moran.segment.SegmentCNGenotype;

import org.junit.*;
import static org.junit.Assert.*;

public class SegmentCNARateModelUniformTest extends NumericTestBase {
    static {
        System.setProperty(GenomeSegment.DEFINITION_FILE_PROPERTY, "data/test/test_segment2.txt");
        System.setProperty(SegmentCNGenotype.MAX_COPY_NUMBER_PROPERTY, "4");

        System.setProperty(SegmentCNARateModel.WGD_RATE_PROPERTY, "0.078");
        System.setProperty(SegmentCNARateModel.GAIN_RATE_PROPERTY, "0.123");
        System.setProperty(SegmentCNARateModel.LOSS_RATE_PROPERTY, "0.456");
    }

    private static final GenomeSegment P6 = GenomeSegment.instance("6p");
    private static final GenomeSegment Q9 = GenomeSegment.instance("9q");

    @Test public void testGlobal() {
        SegmentCNARateModel model = SegmentCNARateModel.global();

        assertRate(0.078, model.getWGDRate());

        assertRate(0.0,   model.getGainRate(P6, 0)); // Cannot gain from zero copy number
        assertRate(0.123, model.getGainRate(P6, 1));
        assertRate(0.123, model.getGainRate(P6, 2));
        assertRate(0.123, model.getGainRate(P6, 3));
        assertRate(0.0,   model.getGainRate(P6, 4)); // Cannot gain beyond the maximum copy number

        assertRate(0.0,   model.getLossRate(P6, 0)); // Cannot lose from zero copy number
        assertRate(0.456, model.getLossRate(P6, 1));
        assertRate(0.456, model.getLossRate(P6, 2));
        assertRate(0.456, model.getLossRate(P6, 3));
        assertRate(0.456, model.getLossRate(P6, 4));

        assertRate(0.0,   model.getGainRate(Q9, 0)); // Cannot gain from zero copy number
        assertRate(0.123, model.getGainRate(Q9, 1));
        assertRate(0.123, model.getGainRate(Q9, 2));
        assertRate(0.123, model.getGainRate(Q9, 3));
        assertRate(0.0,   model.getGainRate(Q9, 4)); // Cannot gain beyond the maximum copy number

        assertRate(0.0,   model.getLossRate(Q9, 0)); // Cannot lose from zero copy number
        assertRate(0.456, model.getLossRate(Q9, 1));
        assertRate(0.456, model.getLossRate(Q9, 2));
        assertRate(0.456, model.getLossRate(Q9, 3));
        assertRate(0.456, model.getLossRate(Q9, 4));
    }

    @Test public void testMutate() {
        int countWGD   = 0;
        int countGain  = 0;
        int countLoss  = 0;
        int countTrial = 100000;

        for (int trialIndex = 0; trialIndex < countTrial; ++trialIndex) {
            SegmentCNGenotype genotype =
                SegmentCNARateModel.global().mutate(SegmentCNGenotype.GERMLINE);

            if (isDoubled(genotype))
                ++countWGD;

            countGain += countGain(genotype);
            countLoss += countLoss(genotype);
        }

        double rateWGD = DoubleUtil.ratio(countWGD,  countTrial);

        // Halve the gain and loss rates because there are two
        // independent segments...
        double rateGain = 0.5 * DoubleUtil.ratio(countGain, countTrial);
        double rateLoss = 0.5 * DoubleUtil.ratio(countLoss, countTrial);

        assertEquals(0.078, rateWGD,  0.001);
        assertEquals(0.123, rateGain, 0.001);
        assertEquals(0.456, rateLoss, 0.001);
    }

    private boolean isDoubled(SegmentCNGenotype genotype) {
        return genotype.count(P6) == 4 && genotype.count(Q9) == 4;
    }

    private int countGain(SegmentCNGenotype genotype) {
        int count = 0;

        if (genotype.count(P6) == 3)
            ++count;

        if (genotype.count(Q9) == 3)
            ++count;

        return count;
    }

    private int countLoss(SegmentCNGenotype genotype) {
        int count = 0;

        if (genotype.count(P6) == 1)
            ++count;

        if (genotype.count(Q9) == 1)
            ++count;

        return count;
    }

    private void assertRate(double expected, Probability actual) {
        assertDouble(expected, actual.doubleValue());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.SegmentCNARateModelUniformTest");
    }
}
