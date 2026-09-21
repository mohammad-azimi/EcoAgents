package ecoagents;

import ecoagents.agents.Agent;
import ecoagents.agents.Animal;
import ecoagents.simulation.Simulation;

public class EcosystemBalanceTest {

    public static void main(
            String[] args
    ) {

        Simulation simulation =
                new Simulation(
                        42L
                );

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "          ECOSYSTEM BALANCE TEST"
        );

        System.out.println(
                "=============================================="
        );

        printState(
                simulation
        );

        while (
                !simulation.isFinished()
        ) {

            simulation.step();

            if (
                    simulation.getIteration() % 25 == 0
                            ||
                    simulation.getBirthsThisIteration() > 0
            ) {

                System.out.println();

                printState(
                        simulation
                );
            }
        }

        System.out.println();

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "FINAL RESULT"
        );

        System.out.println(
                "=============================================="
        );

        printState(
                simulation
        );

        System.out.println();

        if (
                simulation.getTotalBirths() > 0
        ) {

            System.out.println(
                    "PASS: reproduction occurred in the ecosystem."
            );

        } else {

            System.out.println(
                    "NOTICE: no natural reproduction occurred."
            );
        }
    }

    private static void printState(
            Simulation simulation
    ) {

        System.out.printf(
                "Iteration %3d | H: %2d | C: %2d | P: %2d | "
                        + "Births: %2d | Max Generation: %d%n",

                simulation.getIteration(),

                simulation
                        .getLivingHerbivoreCount(),

                simulation
                        .getLivingCarnivoreCount(),

                simulation
                        .getLivingPlantCount(),

                simulation
                        .getTotalBirths(),

                findMaximumGeneration(
                        simulation
                )
        );
    }

    private static int findMaximumGeneration(
            Simulation simulation
    ) {

        int maximumGeneration = 0;

        for (
                Agent agent
                : simulation
                .getEnvironment()
                .getAgents()
        ) {

            if (
                    agent instanceof Animal animal
            ) {

                maximumGeneration =
                        Math.max(
                                maximumGeneration,
                                animal.getGeneration()
                        );
            }
        }

        return maximumGeneration;
    }
}