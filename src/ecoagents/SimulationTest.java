package ecoagents;

import ecoagents.agents.Agent;
import ecoagents.agents.Animal;
import ecoagents.simulation.Simulation;

public class SimulationTest {

    private static final int TEST_ITERATIONS = 15;

    public static void main(
            String[] args
    ) {

        long seed = 42L;

        Simulation simulation =
                new Simulation(
                        seed
                );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "       ECOAGENTS SIMULATION TEST"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Seed: "
                        + simulation.getSeed()
        );

        System.out.println();

        printPopulation(
                simulation
        );

        for (
                int i = 0;
                i < TEST_ITERATIONS;
                i++
        ) {

            if (
                    simulation.isFinished()
            ) {

                break;
            }

            simulation.step();

            System.out.println();

            System.out.println(
                    "---------------"
            );

            System.out.println(
                    "ITERATION "
                            + simulation.getIteration()
            );

            System.out.println(
                    "---------------"
            );

            printPopulation(
                    simulation
            );

            printAnimalStates(
                    simulation
            );
        }

        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "TEST FINISHED"
        );

        System.out.println(
                "========================================"
        );
    }

    private static void printPopulation(
            Simulation simulation
    ) {

        System.out.println(
                "Herbivores : "
                        + simulation
                        .getLivingHerbivoreCount()
        );

        System.out.println(
                "Carnivores : "
                        + simulation
                        .getLivingCarnivoreCount()
        );

        System.out.println(
                "Plants     : "
                        + simulation
                        .getLivingPlantCount()
        );
    }

    private static void printAnimalStates(
            Simulation simulation
    ) {

        System.out.println();

        for (
                Agent agent
                : simulation
                .getEnvironment()
                .getAgents()
        ) {

            if (
                    agent instanceof Animal animal
            ) {

                System.out.printf(
                        "%-10s | %-5s | Position: %-8s | "
                                + "Direction: %-5s | Energy: %5.2f/%5.2f | Action: %s%n",

                        animal.getName(),

                        animal.isAlive()
                                ? "ALIVE"
                                : "DEAD",

                        animal.getPosition(),

                        animal.getDirection(),

                        animal.getEnergy(),

                        animal.getMaxEnergy(),

                        animal.getLastAction()
                );
            }
        }
    }
}