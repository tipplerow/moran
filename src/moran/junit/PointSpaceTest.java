
package moran.junit;

import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jam.junit.NumericTestBase;
import jam.util.MultisetUtil;

import moran.cell.Cell;
import moran.scalar.ScalarCell;
import moran.space.Coord;
import moran.space.Space;

import org.junit.*;
import static org.junit.Assert.*;

public class PointSpaceTest extends NumericTestBase {
    private static final Cell cell0 = new ScalarCell(1.0);
    private static final Cell cell1 = new ScalarCell(1.1);
    private static final Cell cell2 = new ScalarCell(1.2);
    private static final Cell cell3 = new ScalarCell(1.3);
    private static final Cell cell4 = new ScalarCell(1.4);
    private static final Cell cell5 = new ScalarCell(1.5);

    private static final Space FIXED =
        Space.point(List.of(cell0, cell1, cell2, cell3));

    @Test public void testContains() {
        assertTrue(FIXED.contains(cell0));
        assertTrue(FIXED.contains(cell1));
        assertTrue(FIXED.contains(cell2));
        assertTrue(FIXED.contains(cell3));
        assertFalse(FIXED.contains(cell4));
        assertFalse(FIXED.contains(cell5));
    }

    @Test public void testNeighbors() {
        assertEquals(List.of(cell1, cell2, cell3), FIXED.getNeighbors(cell0));
        assertEquals(List.of(cell0, cell2, cell3), FIXED.getNeighbors(cell1));
        assertEquals(List.of(cell0, cell1, cell3), FIXED.getNeighbors(cell2));
        assertEquals(List.of(cell0, cell1, cell2), FIXED.getNeighbors(cell3));
    }

    @Test public void testList() {
        assertEquals(cell0, FIXED.list().get(0));
        assertEquals(cell1, FIXED.list().get(1));
        assertEquals(cell2, FIXED.list().get(2));
        assertEquals(cell3, FIXED.list().get(3));
    }

    @Test public void testLocate() {
        assertEquals(Coord.POINT, FIXED.locate(cell0));
        assertEquals(Coord.POINT, FIXED.locate(cell1));
        assertEquals(Coord.POINT, FIXED.locate(cell2));
        assertEquals(Coord.POINT, FIXED.locate(cell3));
        assertNull(FIXED.locate(cell4));
        assertNull(FIXED.locate(cell5));
    }

    @Test public void testReplace() {
        Space space = Space.point(List.of(cell0, cell1, cell2, cell3));

        assertEquals(4, space.size());
        
        assertTrue(space.contains(cell0));
        assertTrue(space.contains(cell1));
        assertTrue(space.contains(cell2));
        assertTrue(space.contains(cell3));
        assertFalse(space.contains(cell4));
        assertFalse(space.contains(cell5));

        space.replace(cell1, cell4);
        assertEquals(4, space.size());
        
        assertTrue(space.contains(cell0));
        assertFalse(space.contains(cell1));
        assertTrue(space.contains(cell2));
        assertTrue(space.contains(cell3));
        assertTrue(space.contains(cell4));
        assertFalse(space.contains(cell5));

        space.replace(cell3, cell5);
        assertEquals(4, space.size());
        
        assertTrue(space.contains(cell0));
        assertFalse(space.contains(cell1));
        assertTrue(space.contains(cell2));
        assertFalse(space.contains(cell3));
        assertTrue(space.contains(cell4));
        assertTrue(space.contains(cell5));
    }

    @Test public void testSelect() {
        Multiset<Cell> selections = HashMultiset.create();

        while (selections.size() < 1000000)
            selections.add(FIXED.select());

        assertEquals(0.25, MultisetUtil.frequency(selections, cell0), 0.001);
        assertEquals(0.25, MultisetUtil.frequency(selections, cell1), 0.001);
        assertEquals(0.25, MultisetUtil.frequency(selections, cell2), 0.001);
        assertEquals(0.25, MultisetUtil.frequency(selections, cell3), 0.001);
    }

    @Test public void testSize() {
        assertEquals(4, FIXED.size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.PointSpaceTest");
    }
}
