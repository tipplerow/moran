
package moran.junit;

import moran.space.Coord;
import moran.space.CoordLine;

import org.junit.*;
import static org.junit.Assert.*;

public class CoordLineTest {
    private static final Coord c0 = new Coord(0);
    private static final Coord c1 = new Coord(1);
    private static final Coord c2 = new Coord(2);
    private static final Coord c3 = new Coord(3);
    private static final Coord c4 = new Coord(4);

    private static final CoordLine line4 = CoordLine.create(4);

    @Test public void testContains() {
        assertTrue(line4.contains(c0));
        assertTrue(line4.contains(c1));
        assertTrue(line4.contains(c2));
        assertTrue(line4.contains(c3));

        assertFalse(line4.contains(new Coord(-1)));
        assertFalse(line4.contains(c4));
    }

    @Test public void testIndexOf() {
        assertEquals(0, line4.indexOf(c0));
        assertEquals(1, line4.indexOf(c1));
        assertEquals(2, line4.indexOf(c2));
        assertEquals(3, line4.indexOf(c3));
    }

    @Test(expected = RuntimeException.class)
    public void testIndexOfInvalid() {
        line4.indexOf(c4);
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.CoordLineTest");
    }
}
