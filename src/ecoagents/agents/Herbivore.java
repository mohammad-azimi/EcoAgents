package ecoagents.agents;

import ecoagents.brain.NeuralBrain;
import ecoagents.environment.Environment;
import ecoagents.model.Action;
import ecoagents.model.Direction;
import ecoagents.model.Position;
import ecoagents.model.SensorData;

public class Herbivore extends Animal {

    /*
     * Source-model food value.
     */
    private static final double PLANT_ENERGY = 1.0;

    private static final double MAX_ENERGY = 20.0;

    /*
     * Design parameter.
     *
     * Animals start at 80% energy.
     * Reproduction still requires 90%.
     */
    private static final double INITIAL_ENERGY = 16.0;

    public Herbivore(
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

    public Herbivore(
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

    public Herbivore(
            Position position,
            Direction direction,
            NeuralBrain brain,
            double initialEnergy,
            int generation
    ) {

        super(
                "Herbivore",
                'H',
                position,
                direction,
                brain,
                MAX_ENERGY,
                initialEnergy,
                generation
        );
    }

    /*
     * Herbivores eat Plants located
     * anywhere in the 8-cell nearness area.
     */
    @Override
    protected boolean eat(
            Environment environment
    ) {

        Plant plant =
                environment.findAdjacentLivingAgent(
                        getPosition(),
                        this,
                        Plant.class
                );

        if (
                plant == null
        ) {

            return false;
        }

        plant.markDead(
                "EATEN by Herbivore"
        );

        addEnergy(
                PLANT_ENERGY
        );

        setLastAction(
                String.format(
                        "EAT Plant at %s | energy = %.2f",
                        plant.getPosition(),
                        getEnergy()
                )
        );

        return true;
    }

    /*
     * Learning guidance:
     *
     * Plant Near  -> EAT
     * Plant Front -> MOVE_FORWARD
     * Plant Left  -> TURN_LEFT
     * Plant Right -> TURN_RIGHT
     */
    @Override
    protected double calculateBehaviorReward(
            SensorData sensors,
            Action action
    ) {

        double reward = 0.0;

        if (
                sensors.getPlantNear() > 0
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
                sensors.getPlantFront() > 0
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
                sensors.getPlantLeft() > 0
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
                sensors.getPlantRight() > 0
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

        return new Herbivore(
                position,
                direction,
                brain,
                initialEnergy,
                generation
        );
    }
}