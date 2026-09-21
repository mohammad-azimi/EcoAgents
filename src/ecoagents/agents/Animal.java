package ecoagents.agents;

import ecoagents.brain.NeuralBrain;
import ecoagents.environment.Environment;
import ecoagents.model.Action;
import ecoagents.model.Direction;
import ecoagents.model.Position;
import ecoagents.model.SensorData;

import java.util.Arrays;
import java.util.Random;

public abstract class Animal extends Agent {

    /*
     * Design parameter:
     *
     * Every living animal spends a small
     * amount of energy during each iteration.
     */
    private static final double METABOLISM_COST = 0.02;

    /*
     * Exploration probability.
     *
     * 10% of decisions in the real simulation
     * are exploratory random actions.
     */
    private static final double EXPLORATION_RATE = 0.10;

    /*
     * Basic reinforcement rewards.
     */
    private static final double EAT_SUCCESS_REWARD = 1.00;
    private static final double EAT_FAILURE_REWARD = -0.10;

    private static final double MOVE_SUCCESS_REWARD = 0.02;
    private static final double MOVE_BLOCKED_REWARD = -0.05;

    private static final double TURN_REWARD = -0.01;

    /*
     * Reproduction starts when energy reaches
     * 90% of maximum energy.
     */
    private static final double REPRODUCTION_THRESHOLD = 0.90;

    /*
     * During reproduction, 40% of maximum
     * energy is transferred to the child.
     */
    private static final double OFFSPRING_ENERGY_FRACTION = 0.40;

    private final NeuralBrain brain;

    private final double maxEnergy;

    private double energy;

    private final int generation;

    private SensorData lastSensorData =
            new SensorData();

    private Action lastChosenAction =
            null;

    private double lastLearningReward =
            0.0;

    private boolean lastDecisionExploratory =
            false;

    protected Animal(
            String name,
            char symbol,
            Position position,
            Direction direction,
            NeuralBrain brain,
            double maxEnergy,
            double initialEnergy
    ) {

        this(
                name,
                symbol,
                position,
                direction,
                brain,
                maxEnergy,
                initialEnergy,
                0
        );
    }

    protected Animal(
            String name,
            char symbol,
            Position position,
            Direction direction,
            NeuralBrain brain,
            double maxEnergy,
            double initialEnergy,
            int generation
    ) {

        super(
                name,
                symbol,
                position,
                direction
        );

        if (
                brain == null
        ) {

            throw new IllegalArgumentException(
                    "Animal must have a neural brain."
            );
        }

        if (
                maxEnergy <= 0.0
                        ||
                initialEnergy < 0.0
                        ||
                initialEnergy > maxEnergy
        ) {

            throw new IllegalArgumentException(
                    "Invalid energy configuration."
            );
        }

        if (
                generation < 0
        ) {

            throw new IllegalArgumentException(
                    "Generation cannot be negative."
            );
        }

        this.brain =
                brain;

        this.maxEnergy =
                maxEnergy;

        this.energy =
                initialEnergy;

        this.generation =
                generation;
    }

    /*
     * Deterministic neural-network decision.
     *
     * Used primarily by unit tests.
     */
    public Action decideAction(
            Environment environment
    ) {

        return decideAction(
                environment,
                false
        );
    }

    /*
     * Neural decision with optional
     * exploration.
     */
    private Action decideAction(
            Environment environment,
            boolean allowExploration
    ) {

        lastSensorData =
                environment.sense(
                        this
                );

        lastDecisionExploratory =
                allowExploration
                        &&
                environment.chance(
                        EXPLORATION_RATE
                );

        if (
                lastDecisionExploratory
        ) {

            lastChosenAction =
                    environment.randomChoice(
                            Arrays.asList(
                                    Action.values()
                            )
                    );

        } else {

            lastChosenAction =
                    brain.chooseAction(
                            lastSensorData
                    );
        }

        return lastChosenAction;
    }

    /*
     * Deterministic turn.
     *
     * Keeps development tests repeatable.
     */
    public void takeTurn(
            Environment environment
    ) {

        takeTurn(
                environment,
                false
        );
    }

    /*
     * Real simulation turn.
     */
    public void takeTurn(
            Environment environment,
            boolean allowExploration
    ) {

        if (
                !isAlive()
        ) {

            return;
        }

        /*
         * Basic metabolism.
         */
        spendEnergy(
                METABOLISM_COST
        );

        if (
                !isAlive()
        ) {

            return;
        }

        /*
         * Sensors -> Brain -> Action
         */
        Action action =
                decideAction(
                        environment,
                        allowExploration
                );

        double reward;

        switch (action) {

            case TURN_LEFT ->

                    reward =
                            performTurnLeft();

            case TURN_RIGHT ->

                    reward =
                            performTurnRight();

            case MOVE_FORWARD ->

                    reward =
                            performMoveForward(
                                    environment
                            );

            case EAT ->

                    reward =
                            performEat(
                                    environment
                            );

            default ->

                    throw new IllegalStateException(
                            "Unsupported action: "
                                    + action
                    );
        }

        /*
         * Species-specific reward shaping.
         */
        reward +=
                calculateBehaviorReward(
                        lastSensorData,
                        action
                );

        /*
         * Adapt neural weights.
         */
        brain.learn(
                lastSensorData,
                action,
                reward
        );

        lastLearningReward =
                reward;
    }

    private double performTurnLeft() {

        turnLeft();

        setLastAction(
                "TURN_LEFT -> "
                        + getDirection()
        );

        return TURN_REWARD;
    }

    private double performTurnRight() {

        turnRight();

        setLastAction(
                "TURN_RIGHT -> "
                        + getDirection()
        );

        return TURN_REWARD;
    }

    private double performMoveForward(
            Environment environment
    ) {

        Position target =
                environment.moveForward(
                        getPosition(),
                        getDirection()
                );

        if (
                environment.isOccupied(
                        target
                )
        ) {

            setLastAction(
                    "MOVE_FORWARD blocked at "
                            + target
            );

            return MOVE_BLOCKED_REWARD;
        }

        setPosition(
                target
        );

        setLastAction(
                "MOVE_FORWARD -> "
                        + target
        );

        return MOVE_SUCCESS_REWARD;
    }

    private double performEat(
            Environment environment
    ) {

        boolean success =
                eat(
                        environment
                );

        if (
                success
        ) {

            return EAT_SUCCESS_REWARD;
        }

        setLastAction(
                "EAT -> nothing edible nearby"
        );

        return EAT_FAILURE_REWARD;
    }

    /*
     * Species-specific eating behavior.
     */
    protected abstract boolean eat(
            Environment environment
    );

    /*
     * Species-specific reinforcement signal.
     */
    protected double calculateBehaviorReward(
            SensorData sensors,
            Action action
    ) {

        return 0.0;
    }

    /*
     * Species-specific descendant creation.
     */
    protected abstract Animal createOffspring(
            Position position,
            Direction direction,
            NeuralBrain brain,
            double initialEnergy,
            int generation
    );

    /*
     * Reproduction condition.
     */
    public boolean canReproduce() {

        return isAlive()
                &&
                getEnergyRatio()
                        >=
                REPRODUCTION_THRESHOLD;
    }

    /*
     * Produce one descendant.
     */
    public Animal reproduce(
            Environment environment,
            Random random
    ) {

        if (
                !canReproduce()
        ) {

            return null;
        }

        Position childPosition =
                environment
                        .findEmptyAdjacentPosition(
                                getPosition()
                        );

        if (
                childPosition == null
        ) {

            return null;
        }

        double childEnergy =
                maxEnergy
                        *
                        OFFSPRING_ENERGY_FRACTION;

        /*
         * Energy is transferred from
         * parent to child.
         */
        energy -=
                childEnergy;

        /*
         * Child inherits the current learned
         * neural network with small mutation.
         */
        NeuralBrain childBrain =
                brain.createOffspringBrain(
                        random.nextLong()
                );

        Direction[] directions =
                Direction.values();

        Direction childDirection =
                directions[
                        random.nextInt(
                                directions.length
                        )
                ];

        Animal child =
                createOffspring(
                        childPosition,
                        childDirection,
                        childBrain,
                        childEnergy,
                        generation + 1
                );

        environment.addAgent(
                child
        );

        return child;
    }

    protected void addEnergy(
            double amount
    ) {

        energy =
                Math.min(
                        maxEnergy,
                        energy + amount
                );
    }

    protected void spendEnergy(
            double amount
    ) {

        energy -= amount;

        if (
                energy <= 0.0
        ) {

            energy = 0.0;

            markDead(
                    "DIED from energy depletion"
            );
        }
    }

    public double getEnergy() {

        return energy;
    }

    public double getMaxEnergy() {

        return maxEnergy;
    }

    public double getEnergyRatio() {

        return energy
                /
                maxEnergy;
    }

    public int getGeneration() {

        return generation;
    }

    public NeuralBrain getBrain() {

        return brain;
    }

    public SensorData getLastSensorData() {

        return lastSensorData;
    }

    public Action getLastChosenAction() {

        return lastChosenAction;
    }

    public double getLastLearningReward() {

        return lastLearningReward;
    }

    public boolean wasLastDecisionExploratory() {

        return lastDecisionExploratory;
    }

    @Override
    public Position decideMove(
            Environment environment
    ) {

        return getPosition();
    }
}