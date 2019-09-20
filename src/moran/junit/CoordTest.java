
package moran.junit;

import java.util.List;

import moran.space.Coord;

import org.junit.*;
import static org.junit.Assert.*;

public class CoordTest {
    private static final Coord cp = new Coord();
    private static final Coord c1 = new Coord(1);
    private static final Coord c12 = new Coord(1, 2);
    private static final Coord c123 = new Coord(1, 2, 3);

    @Test public void testCoord() {
        assertEquals(1, c1.coord(0));
        assertEquals(1, c12.coord(0));
        assertEquals(1, c123.coord(0));

        assertEquals(2, c12.coord(1));
        assertEquals(2, c123.coord(1));

        assertEquals(3, c123.coord(2));
    }

    @Test public void testDimensionality() {
        assertEquals(0, cp.dimensionality());
        assertEquals(1, c1.dimensionality());
        assertEquals(2, c12.dimensionality());
        assertEquals(3, c123.dimensionality());
    }

    @Test public void testEquals() {
        assertTrue(cp.equals(new Coord()));
        assertFalse(cp.equals(c1));
        assertFalse(cp.equals(c12));
        assertFalse(cp.equals(c123));

        assertTrue(c1.equals(new Coord(1)));
        assertFalse(c1.equals(cp));
        assertFalse(c1.equals(c12));
        assertFalse(c1.equals(c123));

        assertTrue(c12.equals(new Coord(1, 2)));
        assertFalse(c12.equals(cp));
        assertFalse(c12.equals(c1));
        assertFalse(c12.equals(c123));

        assertTrue(c123.equals(new Coord(1, 2, 3)));
        assertFalse(c123.equals(cp));
        assertFalse(c123.equals(c1));
        assertFalse(c123.equals(c12));
    }

    @Test public void testValidateDimensionality1() {
        Coord.validateDimensionality(List.of(new Coord(1, 2), new Coord(2, 3), new Coord(4, 5)));
    }

    @Test(expected = RuntimeException.class)
    public void testValidateDimensionality2() {
        Coord.validateDimensionality(List.of(new Coord(1, 2), new Coord(2, 3), new Coord(4, 5, 6)));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.CoordTest");
    }
}
