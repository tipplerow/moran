
package moran.space;

import java.util.Arrays;

/**
 * Represents the spatial coordinate of a cell in a Moran simulation.
 */
public final class Coord {
    private final int[] coord;

    /**
     * Creates a new coordinate with fixed components.
     *
     * @param coord the fixed components.
     */
    public Coord(int... coord) {
        this.coord = Arrays.copyOf(coord, coord.length);
    }

    /**
     * The single zero-dimensional point coordinate.
     */
    public static final Coord POINT = new Coord();

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
        return Arrays.hashCode(coord);
    }
}
