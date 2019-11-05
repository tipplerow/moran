
package moran.driver;

import java.util.List;

import jam.dist.ExponentialDistribution;
import jam.math.JamRandom;
import jam.vector.VectorUtil;

import moran.cell.Cell;
import moran.cell.Phenotype;
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
    private final Space space;

    // The governing phenotype model...
    private final Phenotype phenotype;

    // The continuous (dimensionless) time elapsed in the active
    // simulation trial...
    private double timeClock;

    // The average fitness of cells in the population...
    private double meanFitness;

    // The random number source...
    private final JamRandom random = JamRandom.global();

    private MoranProcess(Space space, Phenotype phenotype) {
        this.space = space;
        this.phenotype = phenotype;

        this.timeClock = 0.0;
        this.meanFitness = computeMeanFitness();
    }

    private double computeMeanFitness() {
        double meanFitness = 0.0;

        for (Cell cell : space)
            meanFitness += getFitness(cell);

        meanFitness /= space.size();
        return meanFitness;
    }

    private double getFitness(Cell cell) {
        return phenotype.getFitness(cell);
    }

    /**
     * Initializes a new spatial Moran process for a given cellular
     * population.
     *
     * @param space the spatial structure of the cellular population.
     *
     * @param phenotype the cellular fitness (phenotype) model.
     *
     * @return the initialized Moran process.
     */
    public static MoranProcess initialize(Space space, Phenotype phenotype) {
        return new MoranProcess(space, phenotype);
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

    private double[] getNeighborFitness(List<Cell> neighborCells) {
        double[] neighborFit = new double[neighborCells.size()];

        for (int k = 0; k < neighborCells.size(); ++k)
            neighborFit[k] = getFitness(neighborCells.get(k));

        return neighborFit;
    }

    private void updateTimeClock(double[] neighborFit) {
        timeClock += tick(neighborFit);
    }

    private double tick(double[] neighborFit) {
        //
        // Each division event is modeled as a Poisson process, so we
        // sample the elapased time from an exponential distribution
        // with a rate parameter equal to the average fitness of the
        // neighbors that may divide.
        //
        double rate = VectorUtil.mean(neighborFit);
        double time = ExponentialDistribution.sample(rate, random);

        // At each discrete time step, we execute N cell cycles (where
        // N is the population size), so on average each cell will die
        // once during a time step.  To make the elapsed time over a
        // single discrete time step independent of the population
        // size, we must divide the sampled time the population size.
        return time / space.size();
    }

    private Cell selectNeighbor(List<Cell> neighborCells, double[] neighborFit) {
        double[] divisionProb = VectorUtil.copy(neighborFit);

        VectorUtil.normalize(divisionProb);
        int neighborIndex = random.selectPDF(divisionProb);

        return neighborCells.get(neighborIndex);
    }

    private void updateMeanFitness(Cell deadCell, Cell daughter) {
        meanFitness += (getFitness(daughter) - getFitness(deadCell)) / space.size();
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
