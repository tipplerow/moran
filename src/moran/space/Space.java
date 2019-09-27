
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
import moran.cell.Population;

/**
 * Represents the spatial arrangement of the fixed-size population of
 * cells in a Moran simulation.
 */
public abstract class Space extends Population implements SpaceView {
    /**
     * Creates a new space and populates it with a collection of
     * cells.
     *
     * @param cells the initial occupants of the space.
     *
     * @throws IllegalArgumentException unless all cells are unique.
     */
    protected Space(Collection<Cell> cells) {
        super(cells);
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
}
