
package moran.ab;

import moran.cell.Cell;
import moran.cell.Genotype;

/**
 * Defines the cell types in the {@code A/B} Moran model.
 */
public abstract class ABCell implements Cell {
    /**
     * Creates a new cell of a given type.
     *
     * @param type the enumerated cell type.
     *
     * @return a new cell of the given type.
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
     * Creates a new cell of type {@code A}.
     *
     * @return a new cell of type {@code A}.
     */
    public static ABCell newA() {
        return new TypeA();
    }

    /**
     * Creates a new cell of type {@code B}.
     *
     * @return a new cell of type {@code B}.
     */
    public static ABCell newB() {
        return new TypeB();
    }

    /**
     * Returns the enumerated type of this cell.
     *
     * @return the enumerated type of this cell.
     */
    public abstract ABType type();

    @Override public String toString() {
        return "Cell(" + type() + ")";
    }

    private static final class TypeA extends ABCell {
        private TypeA() {}

        @Override public ABCell divide() {
            if (ABConfig.global().getMutationRate().accept())
                return new TypeB();
            else
                return new TypeA();
        }

        @Override public ABGenotype getGenotype() {
            return ABGenotype.A;
        }

        @Override public ABGenotype getPhenotype() {
            return ABGenotype.A;
        }

        @Override public double getFitness() {
            return ABConfig.global().getFitnessRatio();
        }

        @Override public ABType type() {
            return ABType.A;
        }
    }

    private static final class TypeB extends ABCell {
        private TypeB() {}

        @Override public ABCell divide() {
            return new TypeB();
        }

        @Override public ABGenotype getGenotype() {
            return ABGenotype.B;
        }

        @Override public ABGenotype getPhenotype() {
            return ABGenotype.B;
        }

        @Override public double getFitness() {
            return ABConfig.global().getFitnessRatio();
        }

        @Override public ABType type() {
            return ABType.B;
        }
    }
}
