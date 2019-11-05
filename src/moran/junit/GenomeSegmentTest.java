
package moran.junit;

import moran.segment.GenomeSegment;

import org.junit.*;
import static org.junit.Assert.*;

public class GenomeSegmentTest {
    static {
        System.setProperty(GenomeSegment.DEFINITION_FILE_PROPERTY, "data/test/test_segment.txt");
    }

    @Test public void testCount() {
        assertEquals(3, GenomeSegment.count());
    }

    @Test public void testInstanceIndex() {
        GenomeSegment p6  = GenomeSegment.instance("6p");
        GenomeSegment q9  = GenomeSegment.instance("9q");
        GenomeSegment p12 = GenomeSegment.instance("12p");

        assertEquals(0, p6.getIndex());
        assertEquals("6p", p6.getKey());
        assertEquals("Important segment", p6.getDesc());

        assertEquals(1, q9.getIndex());
        assertEquals("9q", q9.getKey());
        assertEquals("9q", q9.getDesc());

        assertEquals(2, p12.getIndex());
        assertEquals("12p", p12.getKey());
        assertEquals("third segment", p12.getDesc());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInstanceIndexNegative() {
        GenomeSegment.instance(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInstanceIndexTooLarge() {
        GenomeSegment.instance(100);
    }

    @Test public void testInstanceKey() {
        GenomeSegment p6  = GenomeSegment.instance("6p");
        GenomeSegment q9  = GenomeSegment.instance("9q");
        GenomeSegment p12 = GenomeSegment.instance("12p");

        assertEquals(0, p6.getIndex());
        assertEquals("6p", p6.getKey());
        assertEquals("Important segment", p6.getDesc());

        assertEquals(1, q9.getIndex());
        assertEquals("9q", q9.getKey());
        assertEquals("9q", q9.getDesc());

        assertEquals(2, p12.getIndex());
        assertEquals("12p", p12.getKey());
        assertEquals("third segment", p12.getDesc());

        assertNull(GenomeSegment.instance("foo"));
    }

    @Test public void testList() {
        assertEquals(3, GenomeSegment.list().size());

        assertEquals("6p", GenomeSegment.list().get(0).getKey());
        assertEquals("9q", GenomeSegment.list().get(1).getKey());
        assertEquals("12p", GenomeSegment.list().get(2).getKey());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testModifyList() {
        GenomeSegment.list().remove(0);
    }

    @Test public void testRequire() {
        GenomeSegment p6  = GenomeSegment.require("6p");

        assertEquals("6p", p6.getKey());
        assertEquals("Important segment", p6.getDesc());
    }

    @Test(expected = RuntimeException.class)
    public void testRequiredMissing() {
        GenomeSegment.require("foo");
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.GenomeSegmentTest");
    }
}
