
package moran.segment;

import java.util.Comparator;

/**
 * Encapsulates the copy number of a genome segment.
 *
 * <p><b>Immutability.</b> Copy number objects are immutable: the
 * {@code gain()}, {@code lose()}, and {@code doubleCN()} methods
 * return new objects with updated copy numbers while the original
 * object remains unchanged.
 *
 * <p><b>Natural ordering.</b> The natural ordering for this class
 * sorts by genome segments first, copy number second.
 */
public final class SegmentCN implements Comparable<SegmentCN> {
    private final GenomeSegment segment;
    private final int copyNum;

    private SegmentCN(GenomeSegment segment, int copyNum) {
        validateCN(copyNum);

        this.segment = segment;
        this.copyNum = copyNum;
    }

    private static void validateCN(int copyNum) {
        if (copyNum < 0)
            throw new IllegalArgumentException("Copy number must be non-negative.");
    }

    /**
     * Returns a comparator that sorts by genome segment first, copy
     * number second.
     */
    public static final Comparator<SegmentCN> SEGMENT_MAJOR_COMPARATOR =
        new Comparator<SegmentCN>() {
            @Override public int compare(SegmentCN obj1, SegmentCN obj2) {
                int segmentCmp = obj1.segment.compareTo(obj2.segment);

                if (segmentCmp != 0)
                    return segmentCmp;
                else
                    return Integer.compare(obj1.copyNum, obj2.copyNum);
            }
        };

    /**
     * Returns a comparator that sorts by copy number first, genome
     * segment second.
     */
    public static final Comparator<SegmentCN> NUMBER_MAJOR_COMPARATOR =
        new Comparator<SegmentCN>() {
            @Override public int compare(SegmentCN obj1, SegmentCN obj2) {
                int copyNumCmp = Integer.compare(obj1.copyNum, obj2.copyNum);

                if (copyNumCmp != 0)
                    return copyNumCmp;
                else
                    return obj1.segment.compareTo(obj2.segment);
            }
        };

    /**
     * Copy number for wild-type (unmutated) genomes.
     */
    public static final int GERMLINE_COPY_NUMBER = 2;

    /**
     * Returns the wild-type copy number object for a given genome
     * segment.
     *
     * @param segment the genome segment to be described.
     *
     * @return the wild-type copy number object for the specified
     * genome segment.
     */
    public static SegmentCN germline(GenomeSegment segment) {
        return instance(segment, GERMLINE_COPY_NUMBER);
    }

    /**
     * Returns the copy number object for a given genome segment and
     * copy number state.
     *
     * @param segment the genome segment to be described.
     *
     * @param copyNum the copy number state of the segment.
     *
     * @return the copy number object for the specified genome segment
     * and copy number state.
     *
     * @throws IllegalArgumentException if the copy number is negative.
     */
    public static SegmentCN instance(GenomeSegment segment, int copyNum) {
        //
        // Consider a "flyweight" pattern with a private instance cache...
        //
        return new SegmentCN(segment, copyNum);
    }

    /**
     * Implements whole genome doubling.  Returns a new object with
     * twice the copy number; this object is unchanged.
     *
     * @return a new copy number object with twice the copy number.
     */
    public SegmentCN doubleCN() {
        return instance(segment, 2 * copyNum);
    }

    /**
     * Implements copy number gain.  Returns a new object with one
     * additional segment copy; this object is unchanged.
     *
     * @return a new copy number object with one additional segment
     * copy.
     */
    public SegmentCN gain() {
        return instance(segment, copyNum + 1);
    }

    /**
     * Implements copy number loss.  Returns a new object with one
     * fewer segment copies; this object is unchanged.
     *
     * @return a new copy number object with one fewer segment copies.
     *
     * @throws IllegalStateException if this is a zero copy number.
     */
    public SegmentCN lose() {
        if (copyNum == 0)
            throw new IllegalStateException("No copy number loss from a zero copy-number state.");

        return instance(segment, copyNum - 1);
    }

    /**
     * Returns the genome segment described by this object.
     *
     * @return the genome segment described by this object.
     */
    public GenomeSegment getSegment() {
        return segment;
    }

    /**
     * Returns the copy number of the genome segment.
     *
     * @return the copy number of the genome segment.
     */
    public int getCopyNumber() {
        return copyNum;
    }

    @Override public int compareTo(SegmentCN that) {
        return SEGMENT_MAJOR_COMPARATOR.compare(this, that);
    }

    @Override public boolean equals(Object obj) {
        return (obj instanceof SegmentCN) && equalsSegmentCN((SegmentCN) obj);
    }

    private boolean equalsSegmentCN(SegmentCN that) {
        return this.copyNum == that.copyNum && this.segment.equals(that.segment);
    }

    @Override public int hashCode() {
        return copyNum + 37 * segment.hashCode();
    }

    @Override public String toString() {
        return segment + "(" + copyNum + ")";
    }
}
