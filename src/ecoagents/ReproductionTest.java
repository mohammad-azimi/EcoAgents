package ecoagents;

import ecoagents.agents.Animal;
import ecoagents.agents.Herbivore;
import ecoagents.brain.NeuralBrain;
import ecoagents.environment.Environment;
import ecoagents.model.Direction;
import ecoagents.model.Position;

import java.util.Random;

public class ReproductionTest {

    public static void main(
            String[] args
    ) {

        Environment environment =
                new Environment(
                        7,
                        12,
                        100L
                );

        /*
         * Controlled neural network.
         */
        double[][] weights =
                new double[
                        NeuralBrain.OUTPUT_COUNT
                ][
                        NeuralBrain.INPUT_COUNT
                ];

        double[] biases =
                new double[] {
                        0.25,
                        -0.10,
                        0.50,
                        0.75
                };

        /*
         * Give the parent exactly
         * 90% of maximum energy:
         *
         * 18 / 20 = 0.90
         */
        Herbivore parent =
                new Herbivore(
                        new Position(
                                3,
                                3
                        ),
                        Direction.NORTH,
                        new NeuralBrain(
                                weights,
                                biases
                        ),
                        18.0,
                        0
                );

        environment.addAgent(
                parent
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "          REPRODUCTION TEST"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.printf(
                "Parent energy before: %.2f / %.2f%n",
                parent.getEnergy(),
                parent.getMaxEnergy()
        );

        System.out.printf(
                "Parent energy ratio : %.2f%n",
                parent.getEnergyRatio()
        );

        System.out.println(
                "Can reproduce       : "
                        + parent.canReproduce()
        );

        System.out.println(
                "Parent generation   : "
                        + parent.getGeneration()
        );

        System.out.println();

        double[][] parentWeightsBefore =
                parent
                        .getBrain()
                        .getWeightsCopy();

        double[] parentBiasesBefore =
                parent
                        .getBrain()
                        .getBiasesCopy();

        Random random =
                new Random(
                        200L
                );

        Animal child =
                parent.reproduce(
                        environment,
                        random
                );

        if (
                child == null
        ) {

            System.out.println(
                    "FAIL: child was not created."
            );

            return;
        }

        System.out.println(
                "Child created successfully."
        );

        System.out.println();

        System.out.println(
                "Child type         : "
                        + child
                        .getClass()
                        .getSimpleName()
        );

        System.out.println(
                "Child position     : "
                        + child.getPosition()
        );

        System.out.println(
                "Child direction    : "
                        + child.getDirection()
        );

        System.out.println(
                "Child generation   : "
                        + child.getGeneration()
        );

        System.out.printf(
                "Child energy       : %.2f / %.2f%n",
                child.getEnergy(),
                child.getMaxEnergy()
        );

        System.out.printf(
                "Parent energy after: %.2f / %.2f%n",
                parent.getEnergy(),
                parent.getMaxEnergy()
        );

        System.out.println();

        /*
         * Compare parent neural network
         * with inherited child network.
         */
        double[][] childWeights =
                child
                        .getBrain()
                        .getWeightsCopy();

        double[] childBiases =
                child
                        .getBrain()
                        .getBiasesCopy();

        int changedParameters = 0;

        double maximumDifference = 0.0;

        for (
                int output = 0;
                output < NeuralBrain.OUTPUT_COUNT;
                output++
        ) {

            for (
                    int input = 0;
                    input < NeuralBrain.INPUT_COUNT;
                    input++
            ) {

                double difference =
                        Math.abs(
                                childWeights[output][input]
                                        -
                                parentWeightsBefore[output][input]
                        );

                if (
                        difference > 0.0000001
                ) {

                    changedParameters++;
                }

                maximumDifference =
                        Math.max(
                                maximumDifference,
                                difference
                        );
            }
        }

        for (
                int output = 0;
                output < NeuralBrain.OUTPUT_COUNT;
                output++
        ) {

            double difference =
                    Math.abs(
                            childBiases[output]
                                    -
                            parentBiasesBefore[output]
                    );

            if (
                    difference > 0.0000001
            ) {

                changedParameters++;
            }

            maximumDifference =
                    Math.max(
                            maximumDifference,
                            difference
                    );
        }

        System.out.println(
                "Neural inheritance:"
        );

        System.out.println(
                "Changed parameters : "
                        + changedParameters
        );

        System.out.printf(
                "Maximum mutation    : %.4f%n",
                maximumDifference
        );

        System.out.println();

        boolean reproductionPassed =
                child
                        instanceof Herbivore
                        &&
                child.getGeneration() == 1
                        &&
                Math.abs(
                        child.getEnergy()
                                -
                        8.0
                ) < 0.0001
                        &&
                Math.abs(
                        parent.getEnergy()
                                -
                        10.0
                ) < 0.0001
                        &&
                changedParameters > 0
                        &&
                maximumDifference <= 0.1001;

        if (
                reproductionPassed
        ) {

            System.out.println(
                    "PASS: reproduction, inheritance and mutation work."
            );

        } else {

            System.out.println(
                    "FAIL: reproduction test did not match expectations."
            );
        }
    }
}