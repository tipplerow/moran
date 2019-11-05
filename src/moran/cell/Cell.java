
package moran.cell;

import jam.bio.Propagator;
import jam.lang.OrdinalIndex;

/**
 * The fundamental agent in a Moran simulation.
 */
public abstract class Cell extends Propagator {
    private static final OrdinalIndex ordinalIndex = OrdinalIndex.create();

    /**
     * Creates a new founder cell.
     */
    protected Cell() {
        this(null);
    }

    /**
     * Creates a new daughter cell.
     *
     * @param parent the parent of the new cell.
     */
    protected Cell(Cell parent) {
        super(ordinalIndex.next(), parent);
    }

    /**
     * Creates a new (possibly mutated) daughter cell.
     *
     * @return a new (possibly mutated) daughter cell.
     */
    public abstract Cell divide();

    /**
     * Returns the underlying genotype for this cell.
     *
     * @return the underlying genotype for this cell.
     */
    public abstract Genotype getGenotype();
}
