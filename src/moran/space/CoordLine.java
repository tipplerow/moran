
package moran.space;

import jam.lang.JamException;

/**
 * Provides the coordinates for a one-dimensional space.
 */
public final class CoordLine {
    private final Coord[] coords;

    private CoordLine(Coord[] coords) {
        this.coords = coords;
    }

    /**
     * Creates a new coordinate line with a fixed length.
     *
     * @param length the length of the coordinate line.
     *
     * @return a new coordinate line with the specified length.
     */
    public static CoordLine create(int length) {
        Coord[] coords = new Coord[length];

        for (int index = 0; index < length; ++index)
            coords[index] = new Coord(index);

        return new CoordLine(coords);
    }

    /**
     * Determines whether this line contains a given coordinate.
     *
     * @param coord the coordinate to examine.
     *
     * @return {@code true} iff this line contains the the given
     * coordinate.
     */
    public boolean contains(Coord coord) {
        return coord.dimensionality() == 1 && 0 <= coord.coord(0) && coord.coord(0) < length();
    }

    /**
     * Returns the coordinate object at a given position.
     *
     * @param index the coordinate index (position).
     *
     * @return the coordinate value along the given dimension.
     *
     * @throws RuntimeException unless the index is valid.
     */
    public Coord coordAt(int index) {
        return coords[index];
    }

    /**
     * Returns the index of a coordinate in this line.
     *
     * @param coord the coordinate of interest.
     *
     * @return the index of the given coordinate.
     *
     * @throws RuntimeException unless the coordinate is located on
     * this line.
     */
    public int indexOf(Coord coord) {
        if (contains(coord))
            return coord.coord(0);
        else
            throw JamException.runtime("Invalid coordinate.");
    }

    /**
     * Returns the length of this coordinate line.
     *
     * @return the length of this coordinate line.
     */
    public int length() {
        return coords.length;
    }
}
