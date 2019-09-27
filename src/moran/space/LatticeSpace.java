
package moran.space;

import java.util.Collection;
import java.util.List;

import jam.bravais.Lattice;
import jam.math.Point;

import moran.cell.Cell;

final class LatticeSpace extends Space {
    private final Lattice<Cell> lattice;

    private LatticeSpace(Collection<Cell> cells, Lattice<Cell> lattice) {
        super(cells);
        this.lattice = lattice;
    }

    static LatticeSpace create(Collection<Cell> cells, Lattice<Cell> lattice) {
        //
        // The lattice must be completely occupied by the cells...
        //
        if (!lattice.isFull())
            throw new IllegalArgumentException("The lattice must be completely full.");

        if (!lattice.containsAll(cells))
            throw new IllegalArgumentException("The lattice must contain all cells.");

        return new LatticeSpace(cells, lattice);
    }

    @Override public List<Cell> getNeighbors(Cell cell) {
        if (lattice.contains(cell))
            return lattice.neighborsOf(cell);
        else
            throw new IllegalArgumentException("Target cell does not reside on the lattice.");
    }

    @Override public Point locate(Cell cell) {
        return lattice.locate(cell);
    }

    @Override public void replace(Cell oldCell, Cell newCell) {
        super.replace(oldCell, newCell);
        lattice.replace(oldCell, newCell);
    }
}
