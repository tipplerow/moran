
package moran.junit;

import moran.space.Coord;

import org.junit.*;
import static org.junit.Assert.*;

public class CoordTest {
    @Test public void testEquals() {
        Coord cp = new Coord();
        Coord c1 = new Coord(1);
        Coord c12 = new Coord(1, 2);
        Coord c123 = new Coord(1, 2, 3);

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

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.CoordTest");
    }
}
