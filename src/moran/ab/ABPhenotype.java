
package moran.ab;

import moran.cell.Genotype;
import moran.cell.Phenotype;

import jam.lang.JamException;

/**
 * Implements the scalar {@code A/B} fitness model: Cells of type
 * {@code A} have unit fitness and cells of type {@code B} have a
 * fitness that differs by a constant multiple value defined by the
 * global configuration.
 */
public final class ABPhenotype implements Phenotype {
    private ABPhenotype() {}

    private static final double fitnessA = ABConfig.TYPE_A_FITNESS;
    private static final double fitnessB = fitnessA * ABConfig.global().getFitnessRatio();

    /**
     * The single scalar phenotype model.
     */
    public static final ABPhenotype INSTANCE = new ABPhenotype();

    @Override public double getFitness(Genotype genotype) {
        return getABFitness((ABGenotype) genotype);
    }

    private static double getABFitness(ABGenotype genotype) {
        switch (genotype.type()) {
        case A:
            return fitnessA;

        case B:
            return fitnessB;

        default:
            throw JamException.runtime("Unknown genotype [%s].", genotype);
        }
    }
}
