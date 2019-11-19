
package moran.segment;

import jam.lang.ObjectFactory;

public abstract class SegmentCNFactory implements ObjectFactory<SegmentCNCell> {
    /**
     * An object factory that creates germline cells.
     */
    public static final SegmentCNFactory GERMLINE = new SegmentCNFactory() {
            @Override public SegmentCNCell newInstance() {
                return SegmentCNCell.germline();
            }
        };
}
