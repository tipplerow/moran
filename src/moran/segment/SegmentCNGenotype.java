
package moran.segment;

import java.util.Arrays;

import jam.app.JamProperties;
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
    //
    // Copy number of each genome segment indexed by the genome
    // segment ordinal index...
    //
    private final int[] copyNumbers;

    private static final int maxCopyNumber = resolveMaxCopyNumber();

    private static int resolveMaxCopyNumber() {
        int max = JamProperties.getOptionalInt(MAX_COPY_NUMBER_PROPERTY,
                                               MAX_COPY_NUMBER_DEFAULT);

        if (max < 2)
            throw new IllegalStateException("The maximum copy number must be at least two.");

        return max;
    }

    private SegmentCNGenotype(int[] copyNumbers) {
        this.copyNumbers = copyNumbers;
    }

    private int[] copyCopyNumbers() {
        return Arrays.copyOf(copyNumbers, copyNumbers.length);
    }

    /**
     * Name of the system property that defines the maximum allowed
     * copy number (must be at least two).
     */
    public static final String MAX_COPY_NUMBER_PROPERTY = "moran.segment.maxCopyNumber";

    /**
     * Default value for the maximum allowed copy number.
     */
    public static final int MAX_COPY_NUMBER_DEFAULT = 8;

    /**
     * The wild-type genotype with {@code CN = 2} for all genome
     * segments.
     */
    public static final SegmentCNGenotype WILD_TYPE = createWildType();

    private static SegmentCNGenotype createWildType() {
        int[] wild = new int[GenomeSegment.count()];
        Arrays.fill(wild, SegmentCN.WILD_TYPE_COPY_NUMBER);

        return new SegmentCNGenotype(wild);
    }

    /**
     * Returns the maximum allowed copy number: Copy number gains
     * for segments already at this copy number do not result in
     * any changes to the genotype.
     *
     * @return the maximum allowed copy number.
     */
    public static int maxCopyNumber() {
        return maxCopyNumber;
    }

    /**
     * Returns the copy number for a given genome segment.
     *
     * @param segment the segment of interest.
     *
     * @return the copy number for the specified genome segment.
     */
    public int count(GenomeSegment segment) {
        return copyNumbers[segment.indexOf()];
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
        int[] newCopyNumbers = copyCopyNumbers();

        for (int index = 0; index < newCopyNumbers.length; ++index)
            newCopyNumbers[index] *= 2;

        return new SegmentCNGenotype(newCopyNumbers);
    }

    /**
     * Returns a new genotype with one additional copy of a genome
     * segment; this genotype is unchanged.
     *
     * <p>If the specified segment already has a copy number equal to
     * the maximum allowed, its copy number will remain unchanged.
     *
     * @param segment the segment to increase in copy number.
     *
     * @return a new genotype with one additional copy of the
     * specified genome segment (unless the copy number for the
     * segment is already equal to the maximum allowed, in which
     * case the copy number will remain unchanged).
     *
     * @throws RuntimeException if the segment has copy number zero.
     */
    public SegmentCNGenotype gain(GenomeSegment segment) {
        if (count(segment) >= maxCopyNumber)
            return this;

        validatePositiveCopyNumber(segment);

        int[] newCopyNumbers = copyCopyNumbers();
        ++newCopyNumbers[segment.indexOf()];

        return new SegmentCNGenotype(newCopyNumbers);
    }

    private void validatePositiveCopyNumber(GenomeSegment segment) {
        if (count(segment) < 1)
            throw new IllegalStateException("Segment [" + segment + "] has copy number zero.");
    }

    /**
     * Returns a new genotype with one fewer copy of a genome segment;
     * this genotype is unchanged.
     *
     * @param segment the segment to decrease in copy number.
     *
     * @return a new genotype with one fewer copy of the specified
     * genome segment.
     *
     * @throws RuntimeException if the segment has copy number zero.
     */
    public SegmentCNGenotype lose(GenomeSegment segment) {
        validatePositiveCopyNumber(segment);

        int[] newCopyNumbers = copyCopyNumbers();
        --newCopyNumbers[segment.indexOf()];

        return new SegmentCNGenotype(newCopyNumbers);
    }

    /**
     * Ensures that a copy number lies within the valid closed range
     * {@code [0, maxCopyNumber()]}.
     *
     * @param copyNumber the copy number to validate.
     *
     * @throws IllegalArgumentException if the copy number is outside
     * of the valid range.
     */
    public static void validateCopyNumber(int copyNumber) {
        if (copyNumber < 0)
            throw new IllegalArgumentException("Negative copy number.");

        if (copyNumber > maxCopyNumber)
            throw new IllegalArgumentException("Copy number exceeds the maximum.");
    }
}
