
package moran.junit;

import java.util.Iterator;
import java.util.List;

import moran.cell.Cell;
import moran.cell.Population;
import moran.scalar.ScalarCell;

import org.junit.*;
import static org.junit.Assert.*;

public class PopulationTest {
    private static final Cell cell0 = new ScalarCell(1.0);
    private static final Cell cell1 = new ScalarCell(1.1);
    private static final Cell cell2 = new ScalarCell(1.2);
    private static final Cell cell3 = new ScalarCell(1.3);
    private static final Cell cell4 = new ScalarCell(1.4);
    private static final Cell cell5 = new ScalarCell(1.5);

    private static final Population FIXED =
        new Population(List.of(cell0, cell1, cell2, cell3));

    @Test public void testContains() {
        assertTrue(FIXED.contains(cell0));
        assertTrue(FIXED.contains(cell1));
        assertTrue(FIXED.contains(cell2));
        assertTrue(FIXED.contains(cell3));
        assertFalse(FIXED.contains(cell4));
        assertFalse(FIXED.contains(cell5));
    }

    @Test public void testIterator() {
        Iterator<Cell> iterator = FIXED.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(cell0, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(cell1, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(cell2, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(cell3, iterator.next());

        assertFalse(iterator.hasNext());
    }

    @Test public void testList() {
        assertEquals(cell0, FIXED.list().get(0));
        assertEquals(cell1, FIXED.list().get(1));
        assertEquals(cell2, FIXED.list().get(2));
        assertEquals(cell3, FIXED.list().get(3));
    }

    @Test public void testSize() {
        assertEquals(4, FIXED.size());
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.PopulationTest");
    }
}
