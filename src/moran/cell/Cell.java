
package moran.cell;

/**
 * The fundamental agent in a Moran simulation.
 */
public abstract class Cell {
    private final Genotype genotype;

    /**
     * Creates a new cell with a fixed genotype.
     *
     * @param genotype the fixed genotype.
     */
    protected Cell(Genotype genotype) {
        validateGenotype(genotype);
        this.genotype = genotype;
    }

    private static void validateGenotype(Genotype genotype) {
        if (genotype == null)
            throw new NullPointerException("Null genotype.");
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
    public Genotype getGenotype() {
        return genotype;
    }

    /**
     * Returns the relative scalar fitness of this cell.
     *
     * @return the relative scalar fitness of this cell.
     */
    public double getFitness() {
        return genotype.getPhenotype().getFitness();
    }
}
