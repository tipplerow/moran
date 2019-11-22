
package moran.ab;

import moran.cell.Cell;
import moran.cell.Genotype;

/**
 * Defines the cell types in the {@code A/B} Moran model.
 */
public final class ABCell extends Cell {
    private final ABGenotype genotype;

    private ABCell(ABCell parent, ABGenotype genotype) {
        super(parent);
        this.genotype = genotype;
    }

    /**
     * Creates a new founder cell of a given type.
     *
     * @param type the enumerated cell type.
     *
     * @return a new founder cell of the given type.
     */
    public static ABCell create(ABType type) {
        switch (type) {
        case A:
            return newA();

        case B:
            return newB();

        default:
            throw new IllegalStateException("Unknown cell type.");
        }
    }
        
    /**
     * Creates a new founder cell of type {@code A}.
     *
     * @return a new founder cell of type {@code A}.
     */
    public static ABCell newA() {
        return new ABCell(null, ABGenotype.A);
    }

    /**
     * Creates a new founder cell of type {@code B}.
     *
     * @return a new founder cell of type {@code B}.
     */
    public static ABCell newB() {
        return new ABCell(null, ABGenotype.B);
    }

    /**
     * Identifies cells of type {@code A}.
     *
     * @return {@code true} iff this is a cell of type {@code A}.
     */
    public boolean isA() {
        return genotype.isA();
    }

    /**
     * Identifies cells of type {@code B}.
     *
     * @return {@code true} iff this is a cell of type {@code B}.
     */
    public boolean isB() {
        return genotype.isB();
    }

    /**
     * Returns the enumerated type of this cell.
     *
     * @return the enumerated type of this cell.
     */
    public ABType type() {
        return genotype.type();
    }

    @Override public ABCell divide() {
        return new ABCell(this, daughterGenotype());
    }

    private ABGenotype daughterGenotype() {
        if (isB()) {
            //
            // Cells of type B never mutate, cells of type A mutate...
            //
            return ABGenotype.B;
        }
        else if (ABConfig.global().getMutationRate().accept()) {
            //
            // Cell of type A mutates...
            //
            return ABGenotype.B;
        }
        else {
            //
            // Cell of type A divides without mutation...
            //
            return ABGenotype.A;
        }
    }

    @Override public ABGenotype getGenotype() {
        return genotype;
    }

    @Override public String toString() {
        return genotype.type().name() + "(" + getIndex() + ")";
    }
}
