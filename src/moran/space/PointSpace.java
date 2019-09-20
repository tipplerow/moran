
package moran.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import moran.cell.Cell;

/**
 * Represents a zero-dimensional space where all cells occupy a single
 * point.
 */
public final class PointSpace extends Space {
    /**
     * Creates a new zero-dimensional point space.
     *
     * @param cells the initial occupants of the space.
     *
     * @throws RuntimeException unless all cells are unique.
     */
    public PointSpace(Collection<Cell> cells) {
        super(cells);
    }

    @Override public boolean contains(Coord coord) {
        return coord.equals(Coord.POINT);
    }

    @Override public List<Cell> getNeighbors(Cell target) {
        if (!contains(target))
            throw new IllegalArgumentException("Target cell does not reside in this space.");

        // All cells except the target cell are neighbors in the
        // zero-dimensional point space...
        List<Cell> neighbors = new ArrayList<Cell>(size() - 1);

        for (Cell neighbor : list())
            if (neighbor != target)
                neighbors.add(neighbor);

        assert neighbors.size() == size() - 1;
        return neighbors;
    }

    @Override public Coord locate(Cell cell) {
        if (contains(cell))
            return Coord.POINT;
        else
            return null;
    }

    @Override protected void placeValid(Cell cell, Coord coord) {
        //
        // The POINT coordinates are not explicitly stored: this is a no-op...
        //
    }
}
