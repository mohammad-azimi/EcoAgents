package ecoagents.agents;

import ecoagents.brain.NeuralBrain;
import ecoagents.environment.Environment;
import ecoagents.model.Action;
import ecoagents.model.Direction;
import ecoagents.model.Position;
import ecoagents.model.SensorData;

public class Carnivore extends Animal {

    /*
     * Source-model food value.
     */
    private static final double HERBIVORE_ENERGY = 2.0;

    private static final double MAX_ENERGY = 20.0;

    /*
     * Design parameter:
     * initial energy = 80% of maximum.
     */
    private static final double INITIAL_ENERGY = 16.0;

    public Carnivore(
            Position position,
            Direction direction,
            NeuralBrain brain
    ) {

        this(
                position,
                direction,
                brain,
                INITIAL_ENERGY,
                0
        );
    }

    public Carnivore(
            Position position,
            Direction direction,
            NeuralBrain brain,
            double initialEnergy
    ) {

        this(
                position,
                direction,
                brain,
                initialEnergy,
                0
        );
    }

    public Carnivore(
            Position position,
            Direction direction,
            NeuralBrain brain,
            double initialEnergy,
            int generation
    ) {

        super(
                "Carnivore",
                'C',
                position,
                direction,
                brain,
                MAX_ENERGY,
                initialEnergy,
                generation
        );
    }

    /*
     * Carnivores eat Herbivores located
     * anywhere inside the nearness area.
     */
    @Override
    protected boolean eat(
            Environment environment
    ) {

        Herbivore herbivore =
                environment.findAdjacentLivingAgent(
                        getPosition(),
                        this,
                        Herbivore.class
                );

        if (
                herbivore == null
        ) {

            return false;
        }

        herbivore.markDead(
                "EATEN by Carnivore"
        );

        addEnergy(
                HERBIVORE_ENERGY
        );

        setLastAction(
                String.format(
                        "EAT Herbivore at %s | energy = %.2f",
                        herbivore.getPosition(),
                        getEnergy()
                )
        );

        return true;
    }

    /*
     * Learning guidance:
     *
     * Herbivore Near  -> EAT
     * Herbivore Front -> MOVE_FORWARD
     * Herbivore Left  -> TURN_LEFT
     * Herbivore Right -> TURN_RIGHT
     */
    @Override
    protected double calculateBehaviorReward(
            SensorData sensors,
            Action action
    ) {

        double reward = 0.0;

        if (
                sensors.getHerbivoreNear() > 0
        ) {

            if (
                    action == Action.EAT
            ) {

                reward += 0.50;

            } else {

                reward -= 0.10;
            }

            return reward;
        }

        if (
                sensors.getHerbivoreFront() > 0
        ) {

            if (
                    action == Action.MOVE_FORWARD
            ) {

                reward += 0.25;

            } else {

                reward -= 0.03;
            }
        }

        if (
                sensors.getHerbivoreLeft() > 0
        ) {

            if (
                    action == Action.TURN_LEFT
            ) {

                reward += 0.25;

            } else {

                reward -= 0.03;
            }
        }

        if (
                sensors.getHerbivoreRight() > 0
        ) {

            if (
                    action == Action.TURN_RIGHT
            ) {

                reward += 0.25;

            } else {

                reward -= 0.03;
            }
        }

        return reward;
    }

    @Override
    protected Animal createOffspring(
            Position position,
            Direction direction,
            NeuralBrain brain,
            double initialEnergy,
            int generation
    ) {

        return new Carnivore(
                position,
                direction,
                brain,
                initialEnergy,
                generation
        );
    }
}