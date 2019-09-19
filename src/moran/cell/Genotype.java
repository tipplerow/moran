
package moran.cell;

/**
 * Encapsulates the genetic information carried by a cell.
 */
public interface Genotype {
    /**
     * Returns the phenotype associated with this genotype.
     *
     * @return the phenotype associated with this genotype.
     */
    public abstract Phenotype getPhenotype();
}
