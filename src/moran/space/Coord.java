
package moran.space;

import java.util.Arrays;

public final class Coord {
    private final int[] coord;

    public Coord(int... coord) {
        this.coord = Arrays.copyOf(coord, coord.length);
    }

    /**
     * The single zero-dimensional point coordinate.
     */
    public static final Coord POINT = new Coord();

    public int coord(int dim) {
        return coord[dim];
    }

    public int dimensionality() {
        return coord.length;
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof Coord) && equalsCoord((Coord) obj);
    }

    private boolean equalsCoord(Coord that) {
        return Arrays.equals(this.coord, that.coord);
    }
}
