
package moran.ab;

import moran.cell.Genotype;
import moran.cell.Phenotype;

/**
 * Defines the genotypes and phenotypes for the {@code A/B} Moran
 * model: both are completely defined by the scalar fitness of the
 * cell type.
 */
public abstract class ABGenotype implements Genotype, Phenotype {
    /**
     * The single genotype for type {@code A} cells.
     */
    public final static ABGenotype A = new TypeA();

    /**
     * The single genotype for type {@code B} cells.
     */
    public final static ABGenotype B = new TypeB();

    /**
     * Returns the enumerated cell type carrying this genotype.
     *
     * @return the enumerated cell type carrying this genotype.
     */
    public abstract ABType type();

    @Override public Phenotype getPhenotype() {
        return this;
    }

    private static final class TypeA extends ABGenotype {
        private TypeA() {}

        @Override public double getFitness() {
            return ABConfig.TYPE_A_FITNESS;
        }

        @Override public ABType type() {
            return ABType.A;
        }
    }

    private static final class TypeB extends ABGenotype {
        private TypeB() {}

        @Override public double getFitness() {
            return ABConfig.global().getFitnessRatio() * ABConfig.TYPE_A_FITNESS;
        }

        @Override public ABType type() {
            return ABType.B;
        }
    }
}

