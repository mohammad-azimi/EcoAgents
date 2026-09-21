package ecoagents.simulation;

public final class SimulationConfig {

    /*
     * Toroidal environment dimensions.
     */
    public static final int ROWS = 15;

    public static final int COLS = 20;

    /*
     * Initial ecosystem population.
     */
    public static final int INITIAL_HERBIVORES = 8;

    public static final int INITIAL_CARNIVORES = 3;

    public static final int INITIAL_PLANTS = 25;

    /*
     * Longer simulation so that learning,
     * reproduction and multiple generations
     * can be observed.
     */
    public static final int MAX_ITERATIONS = 400;

    private SimulationConfig() {

        /*
         * Utility class.
         */
    }
}