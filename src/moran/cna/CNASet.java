
package moran.cna;

import java.util.EnumMap;
import java.util.Map;

import jam.math.EventSet;
import jam.math.Probability;

/**
 * Creates event sets that quantify the probabilities of mutually
 * exclusive copy number gain and loss events (e.g., for the same
 * genome segment).
 */
public final class CNASet {
    /**
     * Creates a new event set for mutually exclusive copy number
     * gains and losses.
     *
     * @param gainProb the probability of a copy number gain.
     *
     * @param lossProb the probability of a copy number loss.
     *
     * @return a new event set with the specified gain and loss
     * probabilities.
     *
     * @throws IllegalArgumentException if the sum of the gain and
     * loss probabilities exceeds one.
     */
    public static EventSet<CNAType> create(double gainProb, double lossProb) {
        return create(Probability.valueOf(gainProb), Probability.valueOf(lossProb));
    }

    /**
     * Creates a new event set for mutually exclusive copy number
     * gains and losses.
     *
     * @param gainProb the probability of a copy number gain.
     *
     * @param lossProb the probability of a copy number loss.
     *
     * @return a new event set with the specified gain and loss
     * probabilities.
     *
     * @throws IllegalArgumentException if the sum of the gain and
     * loss probabilities exceeds one.
     */
    public static EventSet<CNAType> create(Probability gainProb, Probability lossProb) {
        Map<CNAType, Probability> probMap =
            new EnumMap<CNAType, Probability>(CNAType.class);

        probMap.put(CNAType.GAIN, gainProb);
        probMap.put(CNAType.LOSS, lossProb);
        probMap.put(CNAType.NONE, Probability.not(gainProb.or(lossProb)));

        return new EventSet<CNAType>(probMap);
    }
}
