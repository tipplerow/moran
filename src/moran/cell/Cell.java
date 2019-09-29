
package moran.cell;

/**
 * The fundamental agent in a Moran simulation.
 */
public interface Cell {
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

    /**
     * Returns the expressed phenotype for this cell.
     *
     * @return the expressed phenotype for this cell.
     */
    public default Phenotype getPhenotype() {
        return getGenotype().getPhenotype();
    }

    /**
     * Returns the relative scalar fitness of this cell.
     *
     * @return the relative scalar fitness of this cell.
     */
    public default double getFitness() {
        return getPhenotype().getFitness();
    }
}
