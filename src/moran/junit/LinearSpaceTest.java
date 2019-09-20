
package moran.junit;

import java.util.List;

import moran.cell.Cell;
import moran.scalar.ScalarCell;
import moran.space.Coord;
import moran.space.Space;

import org.junit.*;
import static org.junit.Assert.*;

public class LinearSpaceTest {
    private static final Cell cell0 = new ScalarCell(1.0);
    private static final Cell cell1 = new ScalarCell(1.1);
    private static final Cell cell2 = new ScalarCell(1.2);
    private static final Cell cell3 = new ScalarCell(1.3);
    private static final Cell cell4 = new ScalarCell(1.4);

    private static final Coord coord0 = new Coord(0);
    private static final Coord coord1 = new Coord(1);
    private static final Coord coord2 = new Coord(2);
    private static final Coord coord3 = new Coord(3);
    private static final Coord coord4 = new Coord(4);

    private static final Space hardwall = Space.linear(List.of(cell0, cell1, cell2, cell3), false);
    private static final Space periodic = Space.linear(List.of(cell0, cell1, cell2, cell3), true);

    @Test public void testContainsCell() {
        assertTrue(hardwall.contains(cell0));
        assertTrue(hardwall.contains(cell1));
        assertTrue(hardwall.contains(cell2));
        assertTrue(hardwall.contains(cell3));

        assertTrue(periodic.contains(cell0));
        assertTrue(periodic.contains(cell1));
        assertTrue(periodic.contains(cell2));
        assertTrue(periodic.contains(cell3));

        assertFalse(hardwall.contains(cell4));
        assertFalse(periodic.contains(cell4));
    }

    @Test public void testContainsCoord() {
        assertTrue(hardwall.contains(coord0));
        assertTrue(hardwall.contains(coord1));
        assertTrue(hardwall.contains(coord2));
        assertTrue(hardwall.contains(coord3));

        assertTrue(periodic.contains(coord0));
        assertTrue(periodic.contains(coord1));
        assertTrue(periodic.contains(coord2));
        assertTrue(periodic.contains(coord3));

        assertFalse(hardwall.contains(coord4));
        assertFalse(periodic.contains(coord4));
    }

    @Test public void testGetNeighbors() {
        assertEquals(List.of(cell1),        hardwall.getNeighbors(cell0));
        assertEquals(List.of(cell0, cell2), hardwall.getNeighbors(cell1));
        assertEquals(List.of(cell1, cell3), hardwall.getNeighbors(cell2));
        assertEquals(List.of(cell2),        hardwall.getNeighbors(cell3));

        assertEquals(List.of(cell3, cell1), periodic.getNeighbors(cell0));
        assertEquals(List.of(cell0, cell2), periodic.getNeighbors(cell1));
        assertEquals(List.of(cell1, cell3), periodic.getNeighbors(cell2));
        assertEquals(List.of(cell2, cell0), periodic.getNeighbors(cell3));
    }

    @Test public void testList() {
        assertEquals(cell0, hardwall.list().get(0));
        assertEquals(cell1, hardwall.list().get(1));
        assertEquals(cell2, hardwall.list().get(2));
        assertEquals(cell3, hardwall.list().get(3));

        assertEquals(cell0, periodic.list().get(0));
        assertEquals(cell1, periodic.list().get(1));
        assertEquals(cell2, periodic.list().get(2));
        assertEquals(cell3, periodic.list().get(3));
    }

    @Test public void testLocate() {
        assertEquals(coord0, hardwall.locate(cell0));
        assertEquals(coord1, hardwall.locate(cell1));
        assertEquals(coord2, hardwall.locate(cell2));
        assertEquals(coord3, hardwall.locate(cell3));

        assertEquals(coord0, periodic.locate(cell0));
        assertEquals(coord1, periodic.locate(cell1));
        assertEquals(coord2, periodic.locate(cell2));
        assertEquals(coord3, periodic.locate(cell3));

        assertNull(hardwall.locate(cell4));
        assertNull(periodic.locate(cell4));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.LinearSpaceTest");
    }
}
