
package moran.space;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.lang.JamException;
import jam.util.ListUtil;

import moran.cell.Cell;

/**
 * Represents the spatial arrangement of cells in a Moran simulation.
 */
public abstract class Space {
    //
    // The cells must be stored in a random-access list for efficient
    // random selection, and the list must be indexed by a hash table
    // for efficient replacement of dead cells...
    //
    private final List<Cell> cellList;
    private final Map<Cell, Integer> indexMap;

    /**
     * Creates a new space and populates it with a collection of
     * cells.
     *
     * @param cells the initial occupants of the space.
     */
    protected Space(Collection<Cell> cells) {
        this.cellList = new ArrayList<Cell>(cells);
        this.indexMap = new HashMap<Cell, Integer>(cells.size());
        mapCells();
    }

    private void mapCells() {
        for (int index = 0; index < cellList.size(); ++index)
            indexMap.put(cellList.get(index), index);
    }

    /**
     * Creates a new zero-dimensional point space.
     *
     * @param cells the initial occupants of the space.
     *
     * @return the new space containing the specified cells.
     */
    public static Space point(Collection<Cell> cells) {
        return new PointSpace(cells);
    }

    /**
     * Identifies cells that reside in this space.
     *
     * @param cell a cell to examine.
     *
     * @return {@code true} iff the specified cell resides in this
     * space.
     */
    public boolean contains(Cell cell) {
        return indexMap.containsKey(cell);
    }

    /**
     * Returns the neighbors of a given cell: the cells that may
     * divide after the specified cell dies.
     *
     * @param cell a cell in this space (usually one that has been
     * selected to die).
     *
     * @return the neighbors of the given cell.
     *
     * @throws RuntimeException unless the given cell resides in this
     * space.
     */
    public abstract List<Cell> getNeighbors(Cell cell);

    /**
     * Returns a read-only list view of the cells in this space.
     *
     * @return a read-only list view of the cells in this space.
     */
    public List<Cell> list() {
        return Collections.unmodifiableList(cellList);
    }

    /**
     * Returns the location of a given cell in this space.
     *
     * @param cell the cell to locate.
     *
     * @return the location of the given cell (or {@code null} if the
     * cell does not reside in this space).
     */
    public abstract Coord locate(Cell cell);

    /**
     * Places a cell at a specific location in this space.
     *
     * @param cell the cell to place.
     *
     * @param coord the location where the cell will be placed.
     */
    protected abstract void place(Cell cell, Coord coord);

    /**
     * Replaces one cell with another (at the same location).
     *
     * @param oldCell the old cell to remove.
     *
     * @param newCell the new cell to add.
     *
     * @throws RuntimeException unless the old cell resides in this
     * space.
     */
    public void replace(Cell oldCell, Cell newCell) {
        //
        // Place the new cell at the same spatial location and with
        // the same sequential list index...
        //
        Coord   coord = locate(oldCell);
        Integer index = indexMap.get(oldCell);

        if (coord == null)
            throw JamException.runtime("Missing cellular location.");

        if (index == null)
            throw JamException.runtime("Missing cellular index.");

        place(newCell, coord);

        cellList.set(index, newCell);
        indexMap.put(newCell, index);

        indexMap.remove(oldCell);
    }

    /**
     * Selects one cell from this space at random (with equal
     * likelihood for all).
     *
     * @return one cell from this space selected at random.
     */
    public Cell select() {
        return ListUtil.select(cellList);
    }

    /**
     * Returns the fixed number of cells in this space.
     *
     * @return the fixed number of cells in this space.
     */
    public int size() {
        return cellList.size();
    }
}
