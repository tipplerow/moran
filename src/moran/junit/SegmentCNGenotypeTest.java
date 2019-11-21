
package moran.junit;

import moran.segment.GenomeSegment;
import moran.segment.SegmentCNGenotype;

import org.junit.*;
import static org.junit.Assert.*;

public class SegmentCNGenotypeTest {
    static {
        System.setProperty(GenomeSegment.DEFINITION_FILE_PROPERTY, "data/test/test_segment.txt");
        System.setProperty(SegmentCNGenotype.MAX_COPY_NUMBER_PROPERTY, "5");
    }

    private static final GenomeSegment P6  = GenomeSegment.instance("6p");
    private static final GenomeSegment Q9  = GenomeSegment.instance("9q");
    private static final GenomeSegment P12 = GenomeSegment.instance("12p");

    @Test public void testFormat() {
        SegmentCNGenotype genotype = SegmentCNGenotype.GERMLINE;

        genotype = genotype.gain(P6);
        genotype = genotype.gain(P6);
        genotype = genotype.gain(Q9);

        assertEquals("4,3,2", genotype.format());
        assertEquals("6p,9q,12p", genotype.header());
    }

    @Test public void testGain() {
        SegmentCNGenotype genotype = SegmentCNGenotype.GERMLINE;

        genotype = genotype.gain(P6);
        genotype = genotype.gain(P6);
        genotype = genotype.gain(Q9);

        assertCN(genotype, 4, 3, 2);
        assertCN(SegmentCNGenotype.GERMLINE, 2, 2, 2);
    }

    private void assertCN(SegmentCNGenotype genotype, int cnP6, int cnQ9, int cnP12) {
        assertEquals(cnP6, genotype.count(P6));
        assertEquals(cnQ9, genotype.count(Q9));
        assertEquals(cnP12, genotype.count(P12));
    }

    @Test public void testLoss() {
        SegmentCNGenotype genotype = SegmentCNGenotype.GERMLINE;

        genotype = genotype.lose(P6);
        genotype = genotype.lose(P6);
        genotype = genotype.lose(Q9);

        assertCN(genotype, 0, 1, 2);
        assertCN(SegmentCNGenotype.GERMLINE, 2, 2, 2);
    }

    @Test public void testMaxCopyNumber() {
        SegmentCNGenotype genotype = SegmentCNGenotype.GERMLINE;

        assertEquals(5, SegmentCNGenotype.maxCopyNumber());

        genotype = genotype.gain(P6);
        genotype = genotype.gain(P6);
        genotype = genotype.gain(P6);

        assertCN(genotype, 5, 2, 2);

        genotype = genotype.gain(P6);
        genotype = genotype.gain(P6);
        genotype = genotype.gain(P6);

        assertCN(genotype, 5, 2, 2);
    }

    @Test(expected = RuntimeException.class)
    public void testZeroGain() {
        SegmentCNGenotype genotype = SegmentCNGenotype.GERMLINE;

        genotype = genotype.lose(P6);
        genotype = genotype.lose(P6);
        genotype = genotype.gain(P6);
    }

    @Test(expected = RuntimeException.class)
    public void testZeroLoss() {
        SegmentCNGenotype genotype = SegmentCNGenotype.GERMLINE;

        genotype = genotype.lose(P6);
        genotype = genotype.lose(P6);
        genotype = genotype.lose(P6);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.SegmentCNGenotypeTest");
    }
}
