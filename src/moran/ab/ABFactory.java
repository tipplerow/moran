
package moran.ab;

import jam.lang.ObjectFactory;

public abstract class ABFactory implements ObjectFactory<ABCell> {
    /**
     * An object factory that creates type {@code A} cells.
     */
    public static final ABFactory A = new ABFactory() {
            @Override public ABCell newInstance() {
                return ABCell.newA();
            }
        };

    /**
     * An object factory that creates type {@code B} cells.
     */
    public static final ABFactory B = new ABFactory() {
            @Override public ABCell newInstance() {
                return ABCell.newB();
            }
        };
}
