
package moran.cna;

/**
 * Enumerates the two copy number event types.
 */
public enum CNEventType {
    GAIN, LOSS;

    /**
     * Returns the event type represented by a case-insensitive
     * string.
     *
     * @param s the string to parse.
     *
     * @return the event type represented by the input string (in
     * a case-insensitive manner).
     */
    public static CNEventType instance(String s) {
        return valueOf(s.toUpperCase());
    }
}

