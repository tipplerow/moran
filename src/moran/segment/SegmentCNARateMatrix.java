
package moran.segment;

import jam.math.Probability;

import moran.cna.CNEventType;

/**
 * Assembls the rates of copy number gains or losses into a matrix
 * data structure.
 */
public final class SegmentCNARateMatrix {
    //
    // CNA rates stored in matrix form, using the index of genome
    // segment as the row index and copy number as the column index.
    //
    private final CNEventType type;
    private final Probability[][] rates;

    private SegmentCNARateMatrix(CNEventType type) {
        this.type = type;
        this.rates = emptyMatrix();

        assignAbsorbingStates();
    }

    private static Probability[][] emptyMatrix() {
        int nrow = GenomeSegment.count();
        int ncol = 1 + SegmentCNGenotype.maxCopyNumber();

        return new Probability[nrow][ncol];
    }

    private void assignAbsorbingStates() {
        //
        // Assign zero to all absorbing states...
        //
        for (int irow = 0; irow < nrow(); ++irow)
            for (int jcol = 0; jcol < ncol(); ++jcol)
                if (isAbsorbing(jcol))
                    rates[irow][jcol] = Probability.ZERO;
    }

    /**
     * Creates a new rate matrix for a given event type ({@code GAIN}
     * or {@code LOSS}).
     *
     * <p>The event rates for zero copy number will be initialized to
     * zero, since zero copy number is an absorbing state.  The event
     * rates for the maximum copy number will be initialized to zero
     * if the matrix describes {@code GAIN} events.  All other matrix
     * elements will be {@code null}.
     *
     * @param type the event type to be described by the matrix.
     *
     * @return a new rate matrix for the specified event type.
     */
    public static SegmentCNARateMatrix create(CNEventType type) {
        return new SegmentCNARateMatrix(type);
    }

    /**
     * Creates a new uniform rate matrix for a given event type
     * ({@code GAIN} or {@code LOSS}).
     *
     * <p>The event rates for zero copy number will be initialized to
     * zero, since zero copy number is an absorbing state.  The event
     * rates for the maximum copy number will be initialized to zero
     * if the matrix describes {@code GAIN} events.  All other matrix
     * elements will be assigned the specified {@code rate} value.
     *
     * @param type the event type to be described by the matrix.
     *
     * @param rate the uniform rate to assign to all segments and
     * non-absorbing copy numbers.
     *
     * @return a new rate matrix for the specified event type.
     */
    public static SegmentCNARateMatrix uniform(CNEventType type, Probability rate) {
        SegmentCNARateMatrix matrix = create(type);

        for (int irow = 0; irow < matrix.nrow(); ++irow)
            for (int jcol = 0; jcol < matrix.ncol(); ++jcol)
                if (!matrix.isAbsorbing(jcol))
                    matrix.rates[irow][jcol] = rate;

        return matrix;
    }

    /**
     * Returns the event rate for a given segment and copy number.
     *
     * @param segment the segment of interest.
     *
     * @param copyNum the current copy number of the input segment.
     *
     * @return the event rate for the specified segment and copy
     *
     * @throws IllegalArgumentException unless the copy number is in
     * the valid range {@code [0, maxCN]}, where {@code maxCN} is the
     * maximum copy number defined by the {@code SegmentCNGenotype}
     * class.
     */
    public Probability getRate(GenomeSegment segment, int copyNum) {
        return rates[segment.indexOf()][copyNum];
    }

    /**
     * Returns the types of events ({@code GAIN} or {@code LOSS})
     * described by this matrix.
     *
     * @return the types of events described by this matrix.
     */
    public CNEventType getType() {
        return type;
    }

    /**
     * Identifies absorbing copy number states: zero copy number (for
     * both {@code GAIN} and {@code LOSS} event types) and the maximum
     * copy number for {@code GAIN} types.
     *
     * @param copyNum the copy number of interest.
     *
     * @return {@code true} iff the specified copy number refers to an
     * absorbing state.
     */
    public boolean isAbsorbing(int copyNum) {
        return copyNum == 0 || (type.equals(CNEventType.GAIN) && copyNum == SegmentCNGenotype.maxCopyNumber());
    }

    /**
     * Returns the number of rows in this rate matrix.
     *
     * @return the number of rows in this rate matrix.
     */
    public int nrow() {
        return rates.length;
    }

    /**
     * Returns the number of columns in this rate matrix.
     *
     * @return the number of columns in this rate matrix.
     */
    public int ncol() {
        return rates[0].length;
    }

    /**
     * Assigns the event rate for a given segment and copy number.
     *
     * @param segment the segment of interest.
     *
     * @param copyNum the current copy number of the input segment.
     *
     * @param rate the event rate for the specified segment and copy
     * number.
     *
     * @throws IllegalArgumentException if the copy number refers to
     * an absorbing state and the rate is non-zero or if the copy
     * number is outside the valid range {@code [0, maxCN]}, where
     * {@code maxCN} is the maximum copy number.
     */
    public void setRate(GenomeSegment segment, int copyNum, Probability rate) {
        if (isAbsorbing(copyNum) && !rate.equals(Probability.ZERO))
            throw new IllegalArgumentException("Cannot assign a non-zero rate to an absorbing state.");

        rates[segment.indexOf()][copyNum] = rate;
    }

    /**
     * Ensures that all rate elements have been assigned.
     *
     * @throws IllegalStateException unless all rate elements have
     * been assigned.
     */
    public void validate() {
        for (int irow = 0; irow < nrow(); ++irow)
            for (int jcol = 0; jcol < ncol(); ++jcol)
                if (rates[irow][jcol] == null)
                    throw new IllegalStateException("Unassigned rate element.");
    }
}
