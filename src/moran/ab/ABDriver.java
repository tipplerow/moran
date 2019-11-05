
package moran.ab;

import java.io.File;

import moran.driver.MoranDriver;
import moran.space.Space;

/**
 * Simulates the evolution of the simple {@code A/B} Moran model.
 */
public final class ABDriver extends MoranDriver {
    private ABDriver(String... propertyFiles) {
        super(propertyFiles);
    }

    /**
     * Runs one simulation.
     *
     * @param propertyFiles the system files that define the model
     * properties.
     */
    public static void run(String... propertyFiles) {
        ABDriver driver = new ABDriver(propertyFiles);
        driver.runSimulation();
    }

    @Override protected Space createSpace() {
        return Space.global(ABFactory.A);
    }

    @Override protected ABPhenotype createPhenotype() {
        return ABPhenotype.INSTANCE;
    }

    @Override protected void recordSnapshot(File snapshotDir) {
        // No-op...
    }

    public static void main(String[] propertyFiles) {
        run(propertyFiles);
    }
}
