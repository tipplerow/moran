
package moran.cell;

import java.util.List;

/**
 * Provides a read-only view of the fixed-size population of cells in
 * a Moran simulation.
 */
public interface PopulationView {
    /**
     * Identifies members of this population.
     *
     * @param cell a cell to examine.
     *
     * @return {@code true} iff the specified cell is a member of this
     * population.
     */
    public abstract boolean contains(Cell cell);

    /**
     * Returns a read-only list view of the cells in this population.
     *
     * @return a read-only list view of the cells in this population.
     */
    public abstract List<Cell> list();

    /**
     * Returns the fixed number of cells in this population.
     *
     * @return the fixed number of cells in this population.
     */
    public int size();
}
