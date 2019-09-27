
package moran.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jam.math.Point;
import jam.math.Point1D;

import moran.cell.Cell;

final class PointSpace extends Space {
    private PointSpace(Collection<Cell> cells) {
        super(cells);
    }

    static PointSpace create(Collection<Cell> cells) {
        return new PointSpace(cells);
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

    @Override public Point locate(Cell cell) {
        if (contains(cell))
            return Point1D.ORIGIN;
        else
            return null;
    }
}
