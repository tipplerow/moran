
package moran.report;

import java.text.DecimalFormat;

import jam.report.LineBuilder;

import moran.driver.MoranDriver;

/**
 * Records the mean fitness of the cell population.
 */
public class MeanFitnessRecord extends MoranRecord {
    private final double meanFitness;

    private static final DecimalFormat MEAN_FITNESS_FORMAT = new DecimalFormat("#0.0#####");

    private MeanFitnessRecord(MoranDriver driver) {
        super(driver);
        this.meanFitness = driver.getMeanFitness();
    }

    /**
     * Base name for the fitness trajectory report.
     */
    public static final String MEAN_FITNESS_BASE_NAME = "mean-fitness.csv";

    /**
     * Creates a new fitness record with all data assigned from a
     * driver application.
     *
     * @param driver the active driver application.
     *
     * @return the fitness record for the current state of the driver
     * application.
     */
    public static MeanFitnessRecord create(MoranDriver driver) {
        return new MeanFitnessRecord(driver);
    }

    /**
     * Returns the mean fitness of the cell population when this
     * record was collected.
     *
     * @return the mean fitness of the cell population when this
     * record was collected.
     */
    public double getMeanFitness() {
        return meanFitness;
    }

    @Override public String formatLine() {
        LineBuilder builder = LineBuilder.csv();

        builder.append(super.formatLine());
        builder.append(getMeanFitness(), MEAN_FITNESS_FORMAT);

        return builder.toString();
    }

    @Override public String getBaseName() {
        return MEAN_FITNESS_BASE_NAME;
    }

    @Override public String getHeaderLine() {
        LineBuilder builder = LineBuilder.csv();

        builder.append(super.getHeaderLine());
        builder.append("meanFitness");

        return builder.toString();
    }
}
