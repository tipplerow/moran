
package moran.space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import jam.lang.JamException;

import moran.cell.Cell;

/**
 * Provides a base class for dimensional spaces where cells fill the
 * space with exactly one cell per coordinate.
 */
public abstract class CoordSpace extends Space {
    //
    // One-to-one mapping between cells and the locations they
    // occupy...
    //
    private final BiMap<Cell, Coord> coordMap;

    /**
     * Creates a new coordinate space and populates it with a
     * collection of cells.
     *
     * <p>The coordinates in the map completely define the space: no
     * additional coordinates may be added later.
     *
     * @param cells the initial occupants of the space and the
     * locations that they occupy.
     *
     * @throws RuntimeException unless all cells are unique and have
     * distinct coordinates.
     */
    protected CoordSpace(Map<Cell, Coord> cells) {
        super(cells.keySet());
        this.coordMap = HashBiMap.create(cells);
        Coord.validateDimensionality(cells.values());
    }

    /**
     * Returns the cell located at a given coordinate.
     *
     * @param coord the coordinate of interest.
     *
     * @return the cell located at the specified coordinate (or
     * {@code null} if there is no such cell).
     */
    public Cell cellAt(Coord coord) {
        return coordMap.inverse().get(coord);
    }

    /**
     * Returns the nearest neighbors to a given coordinate.
     *
     * @param coord a coordinate in this space.
     *
     * @return the nearest neighbors to the given coordinate.
     *
     * @throws RuntimeException unless the coordinate exists in this
     * space.
     */
    protected abstract List<Coord> getNeighbors(Coord coord);

    @Override public List<Cell> getNeighbors(Cell target) {
        Coord targetCoord = locate(target);

        if (targetCoord == null)
            throw JamException.runtime("Target cell does not reside in this space.");

        List<Coord> neighborCoords = getNeighbors(targetCoord);
        List<Cell>  neighborCells  = new ArrayList<Cell>(neighborCoords.size());

        for (Coord neighborCoord : neighborCoords) {
            Cell neighborCell = cellAt(neighborCoord);

            if (neighborCell != null)
                neighborCells.add(neighborCell);
            else
                throw JamException.runtime("No cell at neighbor coordinate.");
        }

        return neighborCells;
    }

    @Override public boolean contains(Coord coord) {
        return coordMap.inverse().containsKey(coord);
    }

    @Override public Coord locate(Cell cell) {
        return coordMap.get(cell);
    }

    @Override protected void placeValid(Cell cell, Coord coord) {
        coordMap.forcePut(cell, coord);
    }
}
