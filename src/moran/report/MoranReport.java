
package moran.report;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import jam.math.Point;
import jam.report.LineBuilder;

import moran.cell.Cell;
import moran.driver.MoranDriver;
import moran.space.SpaceView;

/**
 * Provides a base class for simulation reports that may process data
 * after each completed time step and simulation trial.
 */
public abstract class MoranReport {
    private final MoranDriver driver;

    /**
     * Creates a new report for a fixed driver application.
     *
     * @param driver the governing driver application.
     */
    protected MoranReport(MoranDriver driver) {
        this.driver = driver;
    }

    /**
     * Format for spatial coordinates in reports.
     */
    public static final DecimalFormat COORD_FORMAT = new DecimalFormat("#0.0###");

    /**
     * Initializes this report at the start of a new simulation.
     */
    public abstract void initializeSimulation();

    /**
     * Initializes this report at the start of a new simulation trial.
     */
    public abstract void initializeTrial();

    /**
     * Processes the results of the latest completed time step.
     */
    public abstract void processStep();

    /**
     * Reports the results of the latest completed simulation trial.
     */
    public abstract void finalizeTrial();

    /**
     * Reports the results of the completed simulation.
     */
    public abstract void finalizeSimulation();

    /**
     * Returns the header text for cellular coordinates in report
     * files.
     *
     * @return the header text for cellular coordinates in report
     * files.
     */
    public String coordHeader() {
        //
        // Obtain the dimensionality from the coordinate of the first
        // cell...
        //
        Cell cell = listCells().get(0);
        Point point = locateCell(cell);

        switch (point.dimensionality()) {
        case 1:
            return "x";

        case 2:
            return "x,y";

        case 3:
            return "x,y,z";

        default:
            throw new IllegalStateException("Unknown dimensionality.");
        }
    }

    /**
     * Formats cellular coordinates for output in a report file.
     *
     * @param point the point to format.
     *
     * @return a formatted string containing the coordinates of the
     * specified point.
     */
    public static String formatCoord(Point point) {
        LineBuilder builder = LineBuilder.csv();

        for (int k = 0; k < point.dimensionality(); ++k)
            builder.append(point.coord(k), COORD_FORMAT);

        return builder.toString();
    }

    /**
     * Formats cellular coordinates for output in a report file.
     *
     * @param cell the cell of interest.
     *
     * @return a formatted string containing the coordinates of the
     * specified cell.
     */
    public String formatCoord(Cell cell) {
        return formatCoord(viewSpace().locate(cell));
    }

    /**
     * Returns the header text for cellular genotypes in report files.
     *
     * @return the header text for cellular genotypes in report files.
     */
    public String genotypeHeader() {
        return listCells().get(0).getGenotype().header();
    }

    /**
     * Returns the global driver application.
     *
     * @return the global driver application.
     */
    public MoranDriver getDriver() {
        return driver;
    }

    /**
     * Returns the directory where reports will be written.
     *
     * @return the directory where reports will be written.
     */
    public File getReportDir() {
        return driver.getReportDir();
    }

    /**
     * Returns a file located in the report directory.
     *
     * @param baseName the base name of the report file.
     *
     * @return a file with the specified base name located in the
     * report directory.
     */
    public File getReportFile(String baseName) {
        return driver.getReportFile(baseName);
    }

    /**
     * Returns the index of the latest completed time step.
     *
     * @return the index of the latest completed time step.
     */
    public int getTimeStep() {
        return driver.getTimeStep();
    }

    /**
     * Returns the index of the latest completed simulation trial.
     *
     * @return the index of the latest completed simulation trial.
     */
    public int getTrialIndex() {
        return driver.getTrialIndex();
    }

    /**
     * Returns the continuous elapsed time in the current simulation
     * trial (normalized by the size of the cellular population and
     * the coordination number of the underlying space).
     *
     * @return the continuous elapsed time in the current simulation
     * trial.
     */
    public double getTimeClock() {
        return driver.getTimeClock();
    }

    /**
     * Returns a read-only list view of the cells in the simulation.
     *
     * @return a read-only list view of the cells in the simulation.
     */
    public List<Cell> listCells() {
        return viewSpace().list();
    }

    /**
     * Returns the spatial location of cell in the simulation space.
     *
     * @param cell the cell to locate.
     *
     * @return the spatial location of the cell ({@code null} if the
     * cell is not present).
     */
    public Point locateCell(Cell cell) {
        return viewSpace().locate(cell);
    }

    /**
     * Returns a read-only view of the cellular space for the current
     * simulation trial.
     *
     * @return a read-only view of the cellular space for the current
     * simulation trial.
     */
    public SpaceView viewSpace() {
        return driver.viewSpace();
    }

    /**
     * Identifies time steps when the system state must be sampled and
     * this report must be updated and/or recorded.
     *
     * @param sampleInterval the report-specific sampling (update)
     * interval (expressed as a number of time steps).
     *
     * @return {@code true} iff this report should be updated for the
     * latest completed time step.
     */
    public boolean isSampleStep(int sampleInterval) {
        return (sampleInterval > 0) && (getTimeStep() > 0) && (getTimeStep() % sampleInterval == 0);
    }
}
