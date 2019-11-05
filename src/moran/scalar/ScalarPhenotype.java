
package moran.scalar;

import moran.cell.Genotype;
import moran.cell.Phenotype;

/**
 * Implements a scalar fitness model: the scalar fitness of the
 * genotype maps directly to the scalar fitness of the cell.
 */
public final class ScalarPhenotype implements Phenotype {
    private ScalarPhenotype() {}

    /**
     * The single scalar phenotype model.
     */
    public static final ScalarPhenotype INSTANCE = new ScalarPhenotype();

    @Override public double getFitness(Genotype genotype) {
        return getScalarFitness((ScalarGenotype) genotype);
    }

    private static double getScalarFitness(ScalarGenotype genotype) {
        return genotype.getFitness();
    }
}
