
package moran.scalar;

import moran.cell.Genotype;
import moran.cell.Phenotype;

import jam.lang.JamException;
import jam.math.DoubleComparator;

/**
 * Implements a genotype and phenotype that are completely defined by
 * a single scalar fitness.
 */
public final class ScalarGenotype implements Genotype, Phenotype {
    private final double fitness;

    private ScalarGenotype(double fitness) {
        validateFitness(fitness);
        this.fitness = fitness;
    }

    private static void validateFitness(double fitness) {
        if (DoubleComparator.DEFAULT.isNegative(fitness))
            throw JamException.runtime("Negative fitness.");
    }

    /**
     * A globally sharable reference genotype with unit fitness.
     */
    public static final ScalarGenotype REFERENCE = new ScalarGenotype(1.0);

    /**
     * Returns a scalar genotype with a given fitness.
     *
     * @param fitness the fitness to assign.
     *
     * @return a scalar genotype with the given fitness.
     */
    public static ScalarGenotype instance(double fitness) {
        if (DoubleComparator.DEFAULT.isUnity(fitness))
            return REFERENCE;
        else
            return new ScalarGenotype(fitness);
    }

    @Override public Phenotype getPhenotype() {
        return this;
    }

    @Override public double getFitness() {
        return fitness;
    }
}
