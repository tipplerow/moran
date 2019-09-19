
package moran.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import moran.cell.Cell;

final class PointSpace extends Space {
    PointSpace(Collection<Cell> cells) {
        super(cells);
    }

    @Override public List<Cell> getNeighbors(Cell target) {
        //
        // All cells except the target cell are neighbors in the
        // zero-dimensional point space...
        //
        List<Cell> neighbors = new ArrayList<Cell>(size() - 1);

        for (Cell neighbor : list())
            if (neighbor != target)
                neighbors.add(neighbor);

        return neighbors;
    }

    @Override public Coord locate(Cell cell) {
        if (contains(cell))
            return Coord.POINT;
        else
            return null;
    }

    @Override protected void place(Cell cell, Coord coord) {
        //
        // The POINT coordinates are not explicitly stored...
        //
        if (!coord.equals(Coord.POINT))
            throw new IllegalArgumentException("All cells must reside at the POINT coordinate.");
    }
}
