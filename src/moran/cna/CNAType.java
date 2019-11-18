
package moran.cna;

/**
 * Enumerates the copy number alteration (CNA) types.
 */
public enum CNAType {
    GAIN, LOSS, NONE;

    /**
     * Returns the event type represented by a case-insensitive
     * string.
     *
     * @param s the string to parse.
     *
     * @return the event type represented by the input string (in
     * a case-insensitive manner).
     */
    public static CNAType instance(String s) {
        return valueOf(s.toUpperCase());
    }
}

