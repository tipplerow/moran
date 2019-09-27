
package moran.space;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.bravais.Lattice;
import jam.util.ListUtil;

import moran.cell.Cell;

/**
 * Represents the spatial arrangement of fixed-size population of
 * cells in a Moran simulation.
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
     *
     * @throws IllegalArgumentException unless all cells are unique.
     */
    protected Space(Collection<Cell> cells) {
        this.cellList = new ArrayList<Cell>(cells);
        this.indexMap = new HashMap<Cell, Integer>(cells.size());

        mapCells();
        assert cellList.size() == indexMap.size();
    }

    private void mapCells() {
        for (int index = 0; index < cellList.size(); ++index) {
            Cell cell = cellList.get(index);
            
            if (indexMap.containsKey(cell))
                throw new IllegalArgumentException("Duplicate cell.");
            else
                indexMap.put(cell, index);
        }
    }

    /**
     * Creates a new zero-dimensional point space.
     *
     * @param cells the initial occupants of the space.
     *
     * @return the new space containing the specified cells.
     *
     * @throws IllegalArgumentException unless all cells are unique.
     */
    public static Space point(Collection<Cell> cells) {
        return PointSpace.create(cells);
    }

    /**
     * Creates a new space with cells distributed on a lattice.
     *
     * @param cells the initial occupants of the space.
     *
     * @param lattice a lattice completely filled with the initial
     * cells.
     *
     * @return the new space containing the specified cells at the
     * spatial locations on the specified lattice.
     *
     * @throws IllegalArgumentException unless all cells are unique
     * and the cells completely fill the lattice.
     */
    public static Space lattice(Collection<Cell> cells, Lattice<Cell> lattice) {
        return LatticeSpace.create(cells, lattice);
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
     * Identifies occupants of this space.
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
     * Returns a read-only list view of the cells in this space.
     *
     * @return a read-only list view of the cells in this space.
     */
    public List<Cell> list() {
        return Collections.unmodifiableList(cellList);
    }

    /**
     * Replaces one cell with another (at the same location).
     *
     * @param oldCell the old cell to remove.
     *
     * @param newCell the new cell to add.
     *
     * @throws IllegalArgumentException unless this space already
     * contains the old cell and does not already contain the new
     * cell.
     */
    public void replace(Cell oldCell, Cell newCell) {
        //
        // Place the new cell at the same location...
        //
        Integer index = indexMap.get(oldCell);

        if (index == null)
            throw new IllegalArgumentException("Cannot find the cell to replace.");

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
