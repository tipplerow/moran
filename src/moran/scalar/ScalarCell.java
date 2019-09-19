
package moran.scalar;

import moran.cell.Cell;

/**
 * The fundamental agent in a Moran simulation.
 */
public class ScalarCell extends Cell {
    private ScalarCell(ScalarGenotype genotype) {
        super(genotype);
    }

    /**
     * Creates a new scalar cell with a fixed fitness.
     *
     * @param fitness the fitness of the new cell.
     */
    public ScalarCell(double fitness) {
        this(ScalarGenotype.instance(fitness));
    }

    /**
     * Creates a new identical daughter cell.
     *
     * <p><b>Subclasses requiring mutation must override this method.</b>
     *
     * @return a new identical daughter cell.
     */
    @Override public ScalarCell divide() {
        return new ScalarCell(getGenotype());
    }

    @Override public ScalarGenotype getGenotype() {
        return (ScalarGenotype) super.getGenotype();
    }
}
