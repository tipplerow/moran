
package moran.ab;

import jam.app.JamProperties;
import jam.math.DoubleRange;
import jam.math.Probability;

/**
 * Encapsulates the parameters that define a simple {@code A/B} Moran
 * model.
 *
 * <p>Cells of type {@code A} have unit fitness and mutate at a fixed
 * rate {@code mu} into cells of type {@code B} with relative fitness
 * {@code r}.  Cells of type {@code B} do not mutate further.
 */
public final class ABConfig {
    private final double fitnessRatio;
    private final Probability mutationRate;

    private static ABConfig GLOBAL = new ABConfig();

    private ABConfig() {
        this.fitnessRatio = resolveFitnessRatio();
        this.mutationRate = resolveMutationRate();
    }

    private static double resolveFitnessRatio() {
        return JamProperties.getRequiredDouble(FITNESS_RATIO_PROPERTY, DoubleRange.POSITIVE);
    }

    private static Probability resolveMutationRate() {
        return Probability.parse(JamProperties.getRequired(MUTATION_RATE_PROPERTY));
    }

    /**
     * Name of the system property that defines the relative fitness
     * of type {@code B} cells.
     */
    public static final String FITNESS_RATIO_PROPERTY = "moran.ab.fitnessRatio";

    /**
     * Name of the system property that defines the mutation rate of
     * type {@code A} cells.
     */
    public static final String MUTATION_RATE_PROPERTY = "moran.ab.mutationRate";

    /**
     * The fitness of all type {@code A} cells.
     */
    public static final double TYPE_A_FITNESS = 1.0;

    /**
     * Returns the global configuration defined by system properties.
     *
     * @return the global configuration defined by system properties.
     */
    public static ABConfig global() {
        return GLOBAL;
    }

    /**
     * Returns the relative fitness of type {@code B} cells.
     *
     * @return the relative fitness of type {@code B} cells.
     */
    public double getFitnessRatio() {
        return fitnessRatio;
    }

    /**
     * Returns the mutation rate of type {@code A} cells.
     *
     * @return the mutation rate of type {@code A} cells.
     */
    public Probability getMutationRate() {
        return mutationRate;
    }
}
