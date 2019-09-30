
package moran.driver;

import java.util.List;

import jam.dist.ExponentialDistribution;
import jam.math.JamRandom;
import jam.vector.VectorUtil;

import moran.cell.Cell;
import moran.space.Space;
import moran.space.SpaceView;

/**
 * Simulates the evolution of a fixed-size cellular population under
 * the rules of a spatial Moran model.
 *
 * <p>This class implements the core process in the Moran model, the
 * cycle of cell death and division.  The cycle proceeds as follows:
 *
 * <ol>
 *   <li>
 *      Select a cell {@code I} at random from the population, with
 *      equal probability for all cells; this cell dies and allows
 *      a neighbor to divide.
 *   </li>
 *   <li>
 *     Update the dimensionless time clock according to the fitness
 *     of the neighbors of cell {@code I}.
 *   </li>
 *   <li>
 *     Select another cell {@code J} at random from the neighbors of
 *     cell {@code I} with a probability proportional to the fitness
 *     of cell {@code J}.
 *   </li>
 *   <li>
 *     Replace cell {@code I} with a daughter of cell {@code J}.
 *   </li>
 * </ol>
 */
public final class MoranProcess {
    // The cell space for the active simulation trial...
    private Space space;

    // The continuous (dimensionless) time elapsed in the active
    // simulation trial...
    private double timeClock;

    // The average fitness of cells in the population...
    private double meanFitness;

    // The random number source...
    private final JamRandom random = JamRandom.global();

    private MoranProcess(Space space) {
        this.space = space;
        this.timeClock = 0.0;
        this.meanFitness = computeMeanFitness(space);
    }

    private static double computeMeanFitness(Space space) {
        double meanFitness = 0.0;

        for (Cell cell : space)
            meanFitness += cell.getFitness();

        meanFitness /= space.size();
        return meanFitness;
    }

    /**
     * Initializes a new spatial Moran process for a given cellular
     * population.
     *
     * @param space the spatial structure of the cellular population.
     *
     * @return the initialized Moran process.
     */
    public static MoranProcess initialize(Space space) {
        return new MoranProcess(space);
    }

    /**
     * Executes one cycle of cell death and division.
     */
    public void executeCellCycle() {
        //
        // (1) Select a cell "I" at random from the population, with
        // equal probability for all cells; this cell dies and allows
        // a neighbor to divide.
        //
        // (2) Update the dimensionless time clock according to the
        // fitness of the neighbors of cell "I".
        //
        // (3) Select another cell "J" at random from the neighbors of
        // cell "I", with a probability proportional to its fitness.
        //
        // (4) Replace cell "I" with a daughter of cell "J".
        //
        Cell deadCell = space.select();

        List<Cell> neighborCells = space.getNeighbors(deadCell);
        double[]   neighborFit   = getNeighborFitness(neighborCells);

        updateTimeClock(neighborFit);

        Cell neighbor = selectNeighbor(neighborCells, neighborFit);
        Cell daughter = neighbor.divide();

        space.replace(deadCell, daughter);
        updateMeanFitness(deadCell, daughter);
    }

    private static double[] getNeighborFitness(List<Cell> neighborCells) {
        double[] neighborFit = new double[neighborCells.size()];

        for (int k = 0; k < neighborCells.size(); ++k)
            neighborFit[k] = neighborCells.get(k).getFitness();

        return neighborFit;
    }

    private void updateTimeClock(double[] neighborFit) {
        timeClock += tick(neighborFit);
    }

    private double tick(double[] neighborFit) {
        //
        // Each division event is modeled as a Poisson process, so we
        // sample the elapased time from an exponential distribution
        // with rate parameter equal to the sum of the fitness...
        //
        double sum  = VectorUtil.sum(neighborFit);
        double time = ExponentialDistribution.sample(sum, random);

        // To make the elapsed time independent of the population size
        // and the coordination number of the lattice, we must rescale
        // the elapsed time by (C / N), where "C" is the coordination
        // number and "N" is the population size.
        return neighborFit.length * time / space.size();
    }

    private Cell selectNeighbor(List<Cell> neighborCells, double[] neighborFit) {
        double[] divisionProb = VectorUtil.copy(neighborFit);

        VectorUtil.normalize(divisionProb);
        int neighborIndex = random.selectPDF(divisionProb);

        return neighborCells.get(neighborIndex);
    }

    private void updateMeanFitness(Cell deadCell, Cell daughter) {
        meanFitness += (daughter.getFitness() - deadCell.getFitness()) / space.size();
    }

    /**
     * Executes one cycle of cell death and division for each member
     * of the cell population.
     */
    public void executeTimeStep() {
        for (int cycle = 0; cycle < space.size(); ++ cycle)
            executeCellCycle();
    }

    /**
     * Returns the average fitness of the current cell population.
     *
     * @return the average fitness of the current cell population.
     */
    public double getMeanFitness() {
        return meanFitness;
    }

    /**
     * Returns the continuous elapsed time in this Moran process
     * (normalized by the size of the cellular population and the
     * coordination number of the underlying space).
     *
     * @return the continuous elapsed time in this Moran process.
     */
    public double getTimeClock() {
        return timeClock;
    }

    /**
     * Returns a read-only view of the cellular space in this process.
     *
     * @return a read-only view of the cellular space in this process.
     */
    public SpaceView viewSpace() {
        return space;
    }
}
