
package moran.cell;

/**
 * Encapsulates the genetic information carried by a cell.
 */
public interface Genotype {
    /**
     * Formats the complete genetic information in this genotype as a
     * string suitable for inclusion in report files.
     *
     * @return a string containing all genetic information contained
     * in this genotype.
     */
    public abstract String format();

    /**
     * Returns a string to be used in the header line of report files.
     *
     * @return a string to be used in the header line of report files.
     */
    public abstract String header();
}
