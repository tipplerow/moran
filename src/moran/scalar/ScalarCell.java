
package moran.scalar;

import moran.cell.Cell;

/**
 * Represents a cell whose genotype and phenotype are completely
 * defined by a single scalar fitness.
 */
public class ScalarCell extends Cell {
    private final ScalarGenotype genotype;

    private ScalarCell(ScalarCell parent, ScalarGenotype genotype) {
        super(parent);
        this.genotype = genotype;
    }

    /**
     * Creates a new founder scalar cell with a fixed fitness.
     *
     * @param fitness the fitness of the new cell.
     */
    public ScalarCell(double fitness) {
        this(null, ScalarGenotype.instance(fitness));
    }

    /**
     * Creates a new identical daughter cell.
     *
     * <p><b>Subclasses requiring mutation must override this method.</b>
     *
     * @return a new identical daughter cell.
     */
    @Override public ScalarCell divide() {
        return new ScalarCell(this, genotype);
    }

    @Override public ScalarGenotype getGenotype() {
        return genotype;
    }

    @Override public String toString() {
        return "ScalarCell(" + genotype.getFitness() + ")";
    }
}
