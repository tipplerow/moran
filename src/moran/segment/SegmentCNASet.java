
package moran.segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jam.math.EventSet;
import jam.math.Probability;

import moran.cna.CNASet;
import moran.cna.CNAType;

/**
 * Assembles copy number event sets for each genome segment and copy
 * number state.
 *
 * <p><b>Exclusive events.</b> We consider whole-genome doubling to be
 * mutually exclusive to gain and loss events within individual genome
 * segments.  When whole-genome doubling occurs, no other copy number
 * events are permitted.  When whole-genome doubling does not occur,
 * copy number gains and losses within the same genome segment are
 * mutually exclusive, but gains and losses may occur independently
 * on different genome segments.
 */
public final class SegmentCNASet {
    //
    // CNA event sets stored in matrix form, using the index of genome
    // segment as the row index and copy number as the column index.
    //
    private final List<List<EventSet<CNAType>>> eventSets;

    private SegmentCNASet(List<List<EventSet<CNAType>>> eventSets) {
        this.eventSets = eventSets;
    }

    /**
     * Creates an event set from rates of gain, loss, and whole-genome
     * doubling.
     *
     * @param rateWGD the probability of whole-genome doubling, which
     * is considered mutually exclusive to all genome segment gain and
     * loss events.
     *
     * @param gainRates a rate matrix describing the probability of
     * copy number gain for each genome segment and copy number state.
     *
     * @param lossRates a rate matrix describing the probability of
     * copy number loss for each genome segment and copy number state.
     *
     * @return a new event set rate matrix for the specified event type.
     */
    public static SegmentCNASet create(double rateWGD,
                                       SegmentCNARateMatrix gainRates,
                                       SegmentCNARateMatrix lossRates) {
        return new SegmentCNASet(Builder.build(rateWGD, gainRates, lossRates));
    }

    /**
     * Returns the event set for a given genome segment and copy number.
     *
     * @param segment the genome segment of interest.
     *
     * @param copyNum the current copy number of the genome segment.
     *
     * @return the event set for the specified genome segment and copy
     * number.
     *
     * @throws IllegalArgumentException if the copy number is invalid.
     */
    public EventSet<CNAType> getEventSet(GenomeSegment segment, int copyNum) {
        return eventSets.get(segment.indexOf()).get(copyNum);
    }

    // -------------------------------------------------------------- //

    private static final class Builder {
        private final double rateWGD;
        private final SegmentCNARateMatrix gainRates;
        private final SegmentCNARateMatrix lossRates;

        private final List<List<EventSet<CNAType>>> setList =
            new ArrayList<List<EventSet<CNAType>>>();

        private Builder(double rateWGD,
                        SegmentCNARateMatrix gainRates,
                        SegmentCNARateMatrix lossRates) {
            this.rateWGD = rateWGD;
            this.gainRates = gainRates;
            this.lossRates = lossRates;
        }

        private static List<List<EventSet<CNAType>>> build(double rateWGD,
                                                           SegmentCNARateMatrix gainRates,
                                                           SegmentCNARateMatrix lossRates) {
            Builder builder = new Builder(rateWGD, gainRates, lossRates);
            return builder.build();
        }

        private List<List<EventSet<CNAType>>> build() {
            for (GenomeSegment segment : GenomeSegment.list())
                setList.add(createSegmentEventSets(segment));

            return Collections.unmodifiableList(setList);
        }

        private List<EventSet<CNAType>> createSegmentEventSets(GenomeSegment segment) {
            List<EventSet<CNAType>> segmentList = new ArrayList<EventSet<CNAType>>();

            for (int copyNum = 0; copyNum < SegmentCNGenotype.maxCopyNumber(); ++copyNum)
                segmentList.add(createSegmentEventSet(segment, copyNum));

            return Collections.unmodifiableList(segmentList);
        }

        private EventSet<CNAType> createSegmentEventSet(GenomeSegment segment, int copyNum) {
            Probability gainRate = adjustRate(gainRates.getRate(segment, copyNum));
            Probability lossRate = adjustRate(lossRates.getRate(segment, copyNum));

            return CNASet.create(gainRate, lossRate);
        }

        private Probability adjustRate(Probability rateCNA) {
            //
            // Whole-genome doubling and single-segment copy number
            // alterations are mutually exclusive.  Therefore, to
            // generate single-segment CNAs at a frequency equal to
            // "rateCNA", the probability in the underlying process
            // must be adjusted to [rateCNA / (1 - rateWGD)], where
            // "rateWGD" is the probability of whole-genome doubling.
            // This accounts for the fact that a single-segment CNA is
            // only attempted in the simulations when whole-genome
            // doubling does not occur (with probability 1 - WGD).
            //
            return rateCNA.times(1.0 / (1.0 - rateWGD));
        }
    }
}
