
package moran.ab;

import jam.lang.ObjectFactory;

/**
 * Enumerates the two {@code A/B} cell types.
 */
public enum ABType {
    /**
     * Cells of type {@code A}.
     */
    A {
        @Override public ObjectFactory<ABCell> objectFactory() {
            return ABFactory.A;
        }
    },

    /**
     * Cells of type {@code B}.
     */
    B {
        @Override public ObjectFactory<ABCell> objectFactory() {
            return ABFactory.B;
        }
    };

    /**
     * Returns an object factory that creates new cells of this type.
     *
     * @return an object factory that creates new cells of this type.
     */
    public abstract ObjectFactory<ABCell> objectFactory();
}
