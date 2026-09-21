package ecoagents.simulation;

import ecoagents.agents.Agent;
import ecoagents.agents.Animal;
import ecoagents.agents.Carnivore;
import ecoagents.agents.Herbivore;
import ecoagents.agents.Plant;

import ecoagents.brain.NeuralBrain;
import ecoagents.environment.Environment;
import ecoagents.model.Direction;
import ecoagents.model.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Simulation {

    private final long seed;

    private final Random random;

    private final Environment environment;

    private int iteration = 0;

    private boolean finished = false;

    private int birthsThisIteration = 0;

    private int totalBirths = 0;

    public Simulation(
            long seed
    ) {

        this.seed =
                seed;

        random =
                new Random(
                        seed
                );

        environment =
                new Environment(
                        SimulationConfig.ROWS,
                        SimulationConfig.COLS,
                        seed
                );

        initializeWorld();
    }

    private void initializeWorld() {

        createPlants(
                SimulationConfig.INITIAL_PLANTS
        );

        createHerbivores(
                SimulationConfig.INITIAL_HERBIVORES
        );

        createCarnivores(
                SimulationConfig.INITIAL_CARNIVORES
        );
    }

    private void createPlants(
            int amount
    ) {

        for (
                int i = 0;
                i < amount;
                i++
        ) {

            Plant plant =
                    new Plant(
                            environment
                                    .randomEmptyPosition()
                    );

            environment.addAgent(
                    plant
            );
        }
    }

    private void createHerbivores(
            int amount
    ) {

        for (
                int i = 0;
                i < amount;
                i++
        ) {

            Position position =
                    environment.randomEmptyPosition();

            Herbivore herbivore =
                    new Herbivore(
                            position,
                            randomDirection(),
                            new NeuralBrain(
                                    random.nextLong()
                            )
                    );

            environment.addAgent(
                    herbivore
            );
        }
    }

    private void createCarnivores(
            int amount
    ) {

        for (
                int i = 0;
                i < amount;
                i++
        ) {

            Position position =
                    environment.randomEmptyPosition();

            Carnivore carnivore =
                    new Carnivore(
                            position,
                            randomDirection(),
                            new NeuralBrain(
                                    random.nextLong()
                            )
                    );

            environment.addAgent(
                    carnivore
            );
        }
    }

    public void step() {

        if (
                finished
        ) {

            return;
        }

        iteration++;

        birthsThisIteration = 0;

        List<Animal> activeAnimals =
                new ArrayList<>();

        for (
                Agent agent
                : environment.getAgents()
        ) {

            if (
                    agent instanceof Animal animal
                            &&
                    animal.isAlive()
            ) {

                activeAnimals.add(
                        animal
                );
            }
        }

        Collections.shuffle(
                activeAnimals,
                random
        );

        /*
         * Real simulation enables exploration.
         */
        for (
                Animal animal
                : activeAnimals
        ) {

            if (
                    animal.isAlive()
            ) {

                animal.takeTurn(
                        environment,
                        true
                );
            }
        }

        /*
         * Reproduction happens after
         * all actions are completed.
         */
        for (
                Animal parent
                : activeAnimals
        ) {

            if (
                    !parent.isAlive()
            ) {

                continue;
            }

            if (
                    !parent.canReproduce()
            ) {

                continue;
            }

            Animal child =
                    parent.reproduce(
                            environment,
                            random
                    );

            if (
                    child != null
            ) {

                birthsThisIteration++;
                totalBirths++;
            }
        }

        updateSimulationState();
    }

    private void updateSimulationState() {

        if (
                iteration
                        >=
                SimulationConfig.MAX_ITERATIONS
        ) {

            finished = true;

            return;
        }

        if (
                getLivingHerbivoreCount() == 0
                        &&
                getLivingCarnivoreCount() == 0
        ) {

            finished = true;
        }
    }

    private Direction randomDirection() {

        Direction[] directions =
                Direction.values();

        return directions[
                random.nextInt(
                        directions.length
                )
        ];
    }

    public int getLivingHerbivoreCount() {

        return countLiving(
                Herbivore.class
        );
    }

    public int getLivingCarnivoreCount() {

        return countLiving(
                Carnivore.class
        );
    }

    public int getLivingPlantCount() {

        return countLiving(
                Plant.class
        );
    }

    private int countLiving(
            Class<? extends Agent> type
    ) {

        int count = 0;

        for (
                Agent agent
                : environment.getAgents()
        ) {

            if (
                    type.isInstance(
                            agent
                    )
                            &&
                    agent.isAlive()
            ) {

                count++;
            }
        }

        return count;
    }

    public int getTotalLivingAnimals() {

        return getLivingHerbivoreCount()
                +
                getLivingCarnivoreCount();
    }

    public int getBirthsThisIteration() {

        return birthsThisIteration;
    }

    public int getTotalBirths() {

        return totalBirths;
    }

    public Environment getEnvironment() {

        return environment;
    }

    public int getIteration() {

        return iteration;
    }

    public boolean isFinished() {

        return finished;
    }

    public long getSeed() {

        return seed;
    }
}