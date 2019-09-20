
package moran.space;

import java.util.Arrays;
import java.util.Collection;

import jam.lang.JamException;
import jam.util.CollectionUtil;

/**
 * Represents the spatial coordinate of a cell in a Moran simulation.
 */
public final class Coord {
    private final int hash;
    private final int[] coord;

    /**
     * Creates a new coordinate with fixed components.
     *
     * @param coord the fixed components.
     */
    public Coord(int... coord) {
        this.hash  = Arrays.hashCode(coord);
        this.coord = Arrays.copyOf(coord, coord.length);
    }

    /**
     * The single zero-dimensional point coordinate.
     */
    public static final Coord POINT = new Coord();

    /**
     * Ensures that all coordinates in a collection have the same
     * dimensionality.
     *
     * @param coords the coordinates to validate.
     *
     * @throws RuntimeException unless all coordinates have the same
     * dimensionality.
     */
    public static void validateDimensionality(Collection<Coord> coords) {
        if (coords.isEmpty())
            return;

        int dim = CollectionUtil.peek(coords).dimensionality();

        for (Coord coord : coords)
            if (coord.dimensionality() != dim)
                throw JamException.runtime("Inconsistent dimensionality.");
    }

    /**
     * Returns the coordinate value along a given dimension.
     *
     * @param dim the (zero-based) index for the desired dimension.
     *
     * @return the coordinate value along the given dimension.
     *
     * @throws RuntimeException unless the dimension index is valid.
     */
    public int coord(int dim) {
        return coord[dim];
    }

    /**
     * Returns the number of components for this coordinate.
     *
     * @return the number of components for this coordinate.
     */
    public int dimensionality() {
        return coord.length;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof Coord) && equalsCoord((Coord) obj);
    }

    private boolean equalsCoord(Coord that) {
        return Arrays.equals(this.coord, that.coord);
    }

    @Override public int hashCode() {
        return hash;
    }
}
