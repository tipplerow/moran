
package moran.space;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jam.lang.JamException;

import moran.cell.Cell;

/**
 * Represents a space where cells are arranged on a line (with
 * optional periodic boundary conditions).
 */
public final class LinearSpace extends CoordSpace {
    private final boolean periodic;
    private final CoordLine coordLine;

    private LinearSpace(Map<Cell, Coord> coordMap, CoordLine coordLine, boolean periodic) {
        super(coordMap);
        this.periodic = periodic;
        this.coordLine = coordLine;
    }

    /**
     * Smallest number of cells allowed in a linear space.
     */
    public static final int MINIMUM_SIZE = 3;

    /**
     * Creates a new linear space with cells placed in the order
     * returned by the collection iterator.
     *
     * @param cells the initial occupants of the linear space.
     *
     * @param periodic whether to impose periodic boundary conditions
     * for the first and last locations.
     *
     * @return the new linear space.
     *
     * @throws RuntimeException unless three or more cells are
     * provided and all cells are unique.
     */
    public static LinearSpace create(Collection<Cell> cells, boolean periodic) {
        if (cells.size() < MINIMUM_SIZE)
            throw JamException.runtime("At least [%d] cells are required.", MINIMUM_SIZE);

        int coordIndex = 0;
        CoordLine coordLine = CoordLine.create(cells.size());
        Map<Cell, Coord> coordMap = new LinkedHashMap<Cell, Coord>(cells.size());

        for (Cell cell : cells) {
            coordMap.put(cell, coordLine.coordAt(coordIndex));
            ++coordIndex;
        }

        return new LinearSpace(coordMap, coordLine, periodic);
    }

    @Override protected List<Coord> getNeighbors(Coord target) {
        int lineLength = coordLine.length();
        int targetIndex = coordLine.indexOf(target);

        if (targetIndex == 0) {
            return getFirstNeighbors();
        }
        else if (1 <= targetIndex && targetIndex < lineLength - 1) {
            return getMiddleNeighbors(targetIndex);
        }
        else if (targetIndex == lineLength - 1) {
            return getLastNeighbors();
        }
        else
            throw JamException.runtime("Invalid target coordinate.");
    }

    private List<Coord> getFirstNeighbors() {
        if (periodic)
            return List.of(coordLine.coordAt(coordLine.length() - 1), coordLine.coordAt(1));
        else
            return List.of(coordLine.coordAt(1));
    }

    private List<Coord> getMiddleNeighbors(int targetIndex) {
        return List.of(coordLine.coordAt(targetIndex - 1),
                       coordLine.coordAt(targetIndex + 1));
    }

    private List<Coord> getLastNeighbors() {
        if (periodic)
            return List.of(coordLine.coordAt(coordLine.length() - 2), coordLine.coordAt(0));
        else
            return List.of(coordLine.coordAt(coordLine.length() - 2));
    }
}
