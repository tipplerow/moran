
package moran.segment;

import java.io.File;

import moran.driver.MoranDriver;
import moran.space.Space;

/**
 * Simulates the evolution of cells with genotypes defined by the copy
 * numbers of the global genome segments.
 */
public final class SegmentCNDriver extends MoranDriver {
    private SegmentCNDriver(String... propertyFiles) {
        super(propertyFiles);
    }

    /**
     * Runs one simulation.
     *
     * @param propertyFiles the system files that define the model
     * properties.
     */
    public static void run(String... propertyFiles) {
        SegmentCNDriver driver = new SegmentCNDriver(propertyFiles);
        driver.runSimulation();
    }

    @Override protected Space createSpace() {
        return Space.global(SegmentCNFactory.GERMLINE);
    }

    @Override protected SegmentCNPhenotype createPhenotype() {
        return SegmentCNPhenotype.global();
    }

    @Override protected void recordSnapshot(File snapshotDir) {
        // No-op...
    }

    public static void main(String[] propertyFiles) {
        run(propertyFiles);
    }
}
