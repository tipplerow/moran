
package moran.cell;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.util.ListUtil;

/**
 * Represents the fixed-size population of cells in a Moran
 * simulation.
 */
public class Population implements PopulationView {
    //
    // The cells must be stored in a random-access list for efficient
    // random selection, and the list must be indexed by a hash table
    // for efficient replacement of dead cells...
    //
    private final List<Cell> cellList;
    private final Map<Cell, Integer> indexMap;

    /**
     * Creates a new population and fills it with a collection of
     * cells.
     *
     * @param cells the initial members of the population.
     *
     * @throws IllegalArgumentException unless all cells are unique.
     */
    public Population(Collection<Cell> cells) {
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
     * Replaces one cell with another.
     *
     * @param oldCell the old cell to remove.
     *
     * @param newCell the new cell to add.
     *
     * @throws IllegalArgumentException unless this population already
     * contains the old cell and does not contain the new cell.
     */
    public void replace(Cell oldCell, Cell newCell) {
        //
        // Place the new cell in the same list element to avoid
        // adding or removing elements from the list...
        //
        Integer index = indexMap.get(oldCell);

        if (index == null)
            throw new IllegalArgumentException("Cannot find the cell to replace.");

        cellList.set(index, newCell);
        indexMap.put(newCell, index);
        indexMap.remove(oldCell);
    }

    /**
     * Selects one cell from this population at random (with equal
     * likelihood for all).
     *
     * @return one cell from this population selected at random.
     */
    public Cell select() {
        return ListUtil.select(cellList);
    }

    @Override public boolean contains(Cell cell) {
        return indexMap.containsKey(cell);
    }

    @Override public List<Cell> list() {
        return Collections.unmodifiableList(cellList);
    }

    @Override public int size() {
        return cellList.size();
    }
}
