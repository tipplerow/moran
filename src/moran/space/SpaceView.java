
package moran.space;

import java.util.List;

import jam.math.Point;

import moran.cell.Cell;
import moran.cell.PopulationView;

/**
 * Provides a read-only view of the spatial arrangement of the
 * fixed-size population of cells in a Moran simulation.
 */
public interface SpaceView extends PopulationView {
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
     * Returns the spatial location of a cell in this space.
     *
     * @param cell a cell of interest.
     *
     * @return the spatial location of the specified cell (or 
     * {@code null} if the cell is not present).
     */
    public abstract Point locate(Cell cell);
}
