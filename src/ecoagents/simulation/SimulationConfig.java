package ecoagents.simulation;

public final class SimulationConfig {

    public static final int ROWS = 15;
    public static final int COLS = 20;

    public static final int INITIAL_HERBIVORES = 8;
    public static final int INITIAL_CARNIVORES = 3;

    /*
     * Plants occupy 50% of the total grid space.
     *
     * 15 × 20 = 300 cells
     * 300 / 2 = 150 plants
     */
    public static final int INITIAL_PLANTS =
            (ROWS * COLS) / 2;

    public static final int MAX_ITERATIONS = 400;

    private SimulationConfig() {
    }
}