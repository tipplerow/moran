
package moran.ab;

import moran.cell.Genotype;

/**
 * Defines the genotype for the {@code A/B} Moran model: simply the
 * enumerated cell type.
 */
public final class ABGenotype implements Genotype {
    private final ABType type;

    private ABGenotype(ABType type) {
        this.type = type;
    }

    /**
     * The single genotype for type {@code A} cells.
     */
    public final static ABGenotype A = new ABGenotype(ABType.A);

    /**
     * The single genotype for type {@code B} cells.
     */
    public final static ABGenotype B = new ABGenotype(ABType.B);

    /**
     * Identifies genotypes of type {@code A}.
     *
     * @return {@code true} iff this is a genotype of type {@code A}.
     */
    public boolean isA() {
        return type.equals(ABType.A);
    }

    /**
     * Identifies genotypes of type {@code B}.
     *
     * @return {@code true} iff this is a genotype of type {@code B}.
     */
    public boolean isB() {
        return type.equals(ABType.B);
    }

    /**
     * Returns the enumerated cell type carrying this genotype.
     *
     * @return the enumerated cell type carrying this genotype.
     */
    public ABType type() {
        return type;
    }

    @Override public String format() {
        return type.name();
    }

    @Override public String header() {
        return "ABType";
    }
}
