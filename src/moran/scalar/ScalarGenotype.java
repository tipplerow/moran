
package moran.scalar;

import moran.cell.Genotype;

import jam.lang.JamException;
import jam.math.DoubleComparator;

/**
 * Implements a genotype that is completely defined by a single scalar
 * fitness.
 */
public final class ScalarGenotype implements Genotype {
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

    /**
     * Returns the scalar fitness for this genotype.
     *
     * @return the scalar fitness for this genotype.
     */
    public double getFitness() {
        return fitness;
    }
}
