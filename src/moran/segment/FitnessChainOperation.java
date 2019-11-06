
package moran.segment;

/**
 * Defines the mathematical operations used to <em>cahin together</em>
 * the fitness effects from multiple copy number gains or losses.
 */
public enum FitnessChainOperation {
    /**
     * Let the wild-type have unit fitness and let {@code s} be the
     * <em>additive</em> change in fitness for a single copy number
     * event, then the fitness for copy number {@code N} is equal to
     * {@code 1.0 + Math.abs(N - 2) * s}.
     */
    ADD {
        @Override public double computeFitness(double s, int N) {
            return 1.0 + Math.abs(N - 2) * s;
        }
    },

    /**
     * Let the wild-type have unit fitness and let {@code (1 + s)} be
     * the <em>multiplicative</em> change in fitness for a single copy
     * number event, then the fitness for copy number {@code N} is
     * equal to {@code Math.pow(1 + s, Math.abs(N - 2))}.
     */
    MULTIPLY {
        @Override public double computeFitness(double s, int N) {
            return Math.pow(1.0 + s, Math.abs(N - 2));
        }
    },

    /**
     * Only the first copy number event changes fitness: Let the
     * wild-type have unit fitness and let {@code s} be the additive
     * change in fitness for a single copy number event (which is
     * equivalent to {@code (1 + s)} as the multiplicative change in
     * fitness for a single event), then the fitness for copy number
     * {@code N} is {@code N == 2 ? 1.0 : 1.0 + s}.
     */
    NONE  {
        @Override public double computeFitness(double s, int N) {
            return N == 2 ? 1.0 : 1.0 + s;
        }
    };

    /**
     * Computes the net fitness for a given copy number and
     * single-event fitness change.
     *
     * @param s the change in fitness that accompanies the first
     * change in copy number.
     *
     * @param N the copy number to evaluate.
     *
     * @return the net fitness for the specified copy number and
     * single-event fitness change.
     */
    public abstract double computeFitness(double s, int N);
}
