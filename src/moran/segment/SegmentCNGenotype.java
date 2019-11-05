
package moran.segment;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;

import jam.lang.JamException;

import moran.cell.Genotype;

/**
 * Implements a genotype that is completely defined by the copy
 * numbers of the global genome segments.
 *
 * <p><b>Immutability.</b> The genotype objects are immutable: the
 * {@code gain()}, {@code lose()}, and {@code doubleWG()} methods
 * return new objects with updated copy numbers while the original
 * object remains unchanged.
 */
public final class SegmentCNGenotype implements Genotype {
    private final Multiset<GenomeSegment> copyNum;

    private SegmentCNGenotype(Multiset<GenomeSegment> copyNum) {
        this.copyNum = copyNum;
    }

    /**
     * The wild-type genotype with {@code CN = 2} for all genome
     * segments.
     */
    public static final SegmentCNGenotype WILD_TYPE = createWildType();

    /**
     * Copy number for wild-type (unmutated) genomes.
     */
    public static final int WILD_TYPE_COPY_NUMBER = 2;

    private static SegmentCNGenotype createWildType() {
        Multiset<GenomeSegment> copyNum = LinkedHashMultiset.create();

        for (GenomeSegment segment : GenomeSegment.list())
            copyNum.setCount(segment, WILD_TYPE_COPY_NUMBER);

        return new SegmentCNGenotype(copyNum);
    }

    /**
     * Identifies genome segments in this genotype.
     *
     * @param segment the segment of interest.
     *
     * @return {@code true} iff this genotype contains the specified
     * genome segment with a positive copy number.
     */
    public boolean contains(GenomeSegment segment) {
        return copyNum.count(segment) > 0;
    }

    /**
     * Returns the copy number for a given genome segment.
     *
     * @param segment the segment of interest.
     *
     * @return the copy number for the specified genome segment
     * ({@code 0} if this genotype does not contain the segment).
     */
    public int count(GenomeSegment segment) {
        return copyNum.count(segment);
    }

    /**
     * Returns the copy number object for a given genome segment.
     *
     * @param segment the segment of interest.
     *
     * @return the copy number object for the specified genome
     * segment.
     */
    public SegmentCN number(GenomeSegment segment) {
        return SegmentCN.instance(segment, count(segment));
    }

    /**
     * Performs a whole-genome doubling on this genotype.
     *
     * @return a new genotype with all copy numbers doubled;
     * this genotype is unchanged.
     */
    public SegmentCNGenotype doubleWG() {
        Multiset<GenomeSegment> newCopyNum = newCopy();

        for (GenomeSegment segment : newCopyNum.elementSet())
            newCopyNum.setCount(segment, 2 * newCopyNum.count(segment));

        return new SegmentCNGenotype(newCopyNum);
    }

    private Multiset<GenomeSegment> newCopy() {
        return LinkedHashMultiset.create(copyNum);
    }

    /**
     * Returns a new genotype with one additional copy of a genome
     * segment; this genotype is unchanged.
     *
     * @param segment the segment to increase in copy number.
     *
     * @return a new genotype with one additional copy of the
     * specified genome segment.
     *
     * @throws RuntimeException unless this genotype already contains
     * the segment (with positive copy number).
     */
    public SegmentCNGenotype gain(GenomeSegment segment) {
        require(segment);

        Multiset<GenomeSegment> newCopyNum = newCopy();
        newCopyNum.add(segment);

        return new SegmentCNGenotype(newCopyNum);
    }

    private void require(GenomeSegment segment) {
        if (!contains(segment))
            throw JamException.runtime("Genotype does not contain segment [%s].", segment.getKey());
    }

    /**
     * Returns a new genotype with one additional copy of a genome
     * segment; this genotype is unchanged.
     *
     * @param segment the segment to increase in copy number.
     *
     * @return a new genotype with one additional copy of the
     * specified genome segment.
     *
     * @throws RuntimeException unless this genotype contains the
     * segment (with positive copy number).
     */
    public SegmentCNGenotype lose(GenomeSegment segment) {
        require(segment);

        Multiset<GenomeSegment> newCopyNum = newCopy();
        newCopyNum.remove(segment);

        return new SegmentCNGenotype(newCopyNum);
    }

    /**
     * Returns a read-only view of the genome segments contained in
     * this genotype (with positive copy number).
     *
     * @return a read-only view of the genome segments contained in
     * this genotype (with positive copy number).
     */
    public Collection<GenomeSegment> viewSegments() {
        return Collections.unmodifiableCollection(copyNum.elementSet());
    }
}
