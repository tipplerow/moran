
package moran.cell;

/**
 * Encapsulates the phenotypic characteristic of a cell.
 */
public interface Phenotype {
    /**
     * Returns the relative scalar fitness of this phenotype.
     *
     * @return the relative scalar fitness of this phenotype.
     */
    public abstract double getFitness();
}
