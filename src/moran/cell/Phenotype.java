
package moran.cell;

/**
 * Maps cellular genotypes to relative fitness and other observable
 * characteristics.
 */
public interface Phenotype {
    /**
     * Returns the relative scalar fitness for a given cell.
     *
     * @param cell the cell to evaluate.
     *
     * @return the relative scalar fitness for the specified cell.
     *
     * @throws ClassCastException unless the genotype of the cell
     * has a runtime type compatible with this phenotype.
     */
    public default double getFitness(Cell cell) {
        return getFitness(cell.getGenotype());
    }

    /**
     * Returns the relative scalar fitness for a given genotype.
     *
     * @param genotype the genotype to evaluate.
     *
     * @return the relative scalar fitness for the specified genotype.
     *
     * @throws ClassCastException unless the genotype has a runtime
     * type compatible with this phenotype.
     */
    public abstract double getFitness(Genotype genotype);
}
