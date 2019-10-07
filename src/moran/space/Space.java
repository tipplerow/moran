
package moran.space;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jam.bravais.Lattice;
import jam.lang.ObjectFactory;
import jam.util.ListUtil;

import moran.cell.Cell;
import moran.cell.Population;

/**
 * Represents the spatial arrangement of the fixed-size population of
 * cells in a Moran simulation.
 */
public abstract class Space extends Population implements SpaceView {
    /**
     * Name of the system property that defines the global spatial
     * structure.
     *
     * <p>The system property may take one of two forms.  To define a
     * zero-dimensional point space, use the format {@code POINT; N},
     * where {@code POINT} is the literal string and {@code N} is the
     * number of cells in the space.  For a lattice space, use the
     * format required by the {@code jam.bravais.Lattice} class.
     */
    public static final String STRUCTURE_PROPERTY = "moran.space.structure";

    /**
     * Creates the global space with a structure defined by system
     * properties and fills it with occupants created by a factory.
     *
     * @param factory the source of new occupants for the space.
     *
     * @return the global space wiith the structure defined by system
     * properties, filled with cells created by the given factory.
     *
     * @throws RuntimeException unless a valid structure property is
     * set.
     */
    public static Space global(ObjectFactory<? extends Cell> factory) {
        return GlobalSpace.create(factory);
    }

    /**
     * Creates a new space and populates it with a collection of
     * cells.
     *
     * @param cells the initial occupants of the space.
     *
     * @throws IllegalArgumentException unless all cells are unique.
     */
    protected Space(Collection<? extends Cell> cells) {
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
    public static Space point(Collection<? extends Cell> cells) {
        return PointSpace.create(cells);
    }

    /**
     * Creates a new zero-dimensional point space.
     *
     * @param factory the source of new occupants for the space.
     *
     * @param size the fixed cellular population size.
     *
     * @return the new space containing the specified cells.
     *
     * @throws IllegalArgumentException unless all cells are unique.
     */
    public static Space point(ObjectFactory<? extends Cell> factory, int size) {
        return point(factory.newInstances(size));
    }

    /**
     * Creates a new space with cells distributed on a lattice.
     *
     * @param lattice a lattice housing the cellular population.
     *
     * @return the new space containing the cellular population
     * distributed on the input lattice.
     *
     * @throws IllegalArgumentException unless all cells are unique
     * and the cells completely fill the lattice.
     */
    public static Space lattice(Lattice<Cell> lattice) {
        return LatticeSpace.create(lattice);
    }

    /**
     * Creates a new space with cells distributed on a lattice.
     *
     * @param lattice a lattice to house the cellular population (may
     * be empty; any previous contents will be removed).
     *
     * @param factory the source of new occupants for the space.
     *
     * @return the new space containing cells created by the object
     * factory distributed on the lattice.
     */
    public static Space lattice(Lattice<Cell> lattice, ObjectFactory<? extends Cell> factory) {
        lattice.fill(factory);
        return lattice(lattice);
    }
}
