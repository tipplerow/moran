
package moran.segment;

import moran.cell.Cell;
import moran.cell.Genotype;

/**
 * Represents a cell with a genotype defined completely by the copy
 * numbers of the global genome segments.
 */
public final class SegmentCNCell extends Cell {
    private final SegmentCNGenotype genotype;

    private SegmentCNCell(SegmentCNCell parent, SegmentCNGenotype genotype) {
        super(parent);
        this.genotype = genotype;
    }

    /**
     * Creates a new germline founder cell.
     *
     * @return a new germline founder cell.
     */
    public static SegmentCNCell germline() {
        return new SegmentCNCell(null, SegmentCNGenotype.GERMLINE);
    }
        
    @Override public SegmentCNCell divide() {
        return new SegmentCNCell(this, SegmentCNARateModel.global().mutate(genotype));
    }

    @Override public SegmentCNGenotype getGenotype() {
        return genotype;
    }

    @Override public String toString() {
        return "Cell(" + genotype.toString() + ")";
    }
}
