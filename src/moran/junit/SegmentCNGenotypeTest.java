
package moran.junit;

import moran.segment.GenomeSegment;
import moran.segment.SegmentCNGenotype;

import org.junit.*;
import static org.junit.Assert.*;

public class SegmentCNGenotypeTest {
    static {
        System.setProperty(GenomeSegment.DEFINITION_FILE_PROPERTY, "data/test/test_segment.txt");
    }

    private static final GenomeSegment P6  = GenomeSegment.instance("6p");
    private static final GenomeSegment Q9  = GenomeSegment.instance("9q");
    private static final GenomeSegment P12 = GenomeSegment.instance("12p");

    @Test public void testGain() {
        SegmentCNGenotype genotype = SegmentCNGenotype.WILD_TYPE;

        genotype = genotype.gain(P6);
        genotype = genotype.gain(P6);
        genotype = genotype.gain(Q9);

        assertCN(genotype, 4, 3, 2);
        assertCN(SegmentCNGenotype.WILD_TYPE, 2, 2, 2);
    }

    private void assertCN(SegmentCNGenotype genotype, int cnP6, int cnQ9, int cnP12) {
        assertEquals(cnP6, genotype.count(P6));
        assertEquals(cnQ9, genotype.count(Q9));
        assertEquals(cnP12, genotype.count(P12));
    }

    @Test public void testLoss() {
        SegmentCNGenotype genotype = SegmentCNGenotype.WILD_TYPE;

        genotype = genotype.lose(P6);
        genotype = genotype.lose(P6);
        genotype = genotype.lose(Q9);

        assertCN(genotype, 0, 1, 2);
        assertCN(SegmentCNGenotype.WILD_TYPE, 2, 2, 2);
    }

    @Test public void testViewSegments() {
        SegmentCNGenotype genotype = SegmentCNGenotype.WILD_TYPE;

        assertEquals(3, genotype.viewSegments().size());

        genotype = genotype.lose(P6);
        genotype = genotype.lose(P6);

        assertEquals(2, genotype.viewSegments().size());
        assertFalse(genotype.viewSegments().contains(P6));
    }

    @Test public void testWildType() {
        assertEquals(2, SegmentCNGenotype.WILD_TYPE.count(P6));
        assertEquals(2, SegmentCNGenotype.WILD_TYPE.count(Q9));
        assertEquals(2, SegmentCNGenotype.WILD_TYPE.count(P12));
    }

    @Test(expected = RuntimeException.class)
    public void testZeroGain() {
        SegmentCNGenotype genotype = SegmentCNGenotype.WILD_TYPE;

        genotype = genotype.lose(P6);
        genotype = genotype.lose(P6);
        genotype = genotype.gain(P6);
    }

    @Test(expected = RuntimeException.class)
    public void testZeroLoss() {
        SegmentCNGenotype genotype = SegmentCNGenotype.WILD_TYPE;

        genotype = genotype.lose(P6);
        genotype = genotype.lose(P6);
        genotype = genotype.lose(P6);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.SegmentCNGenotypeTest");
    }
}
