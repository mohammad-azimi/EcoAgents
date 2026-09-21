package ecoagents;

import ecoagents.agents.Herbivore;
import ecoagents.agents.Plant;
import ecoagents.brain.NeuralBrain;
import ecoagents.environment.Environment;
import ecoagents.model.Direction;
import ecoagents.model.Position;
import ecoagents.model.SensorData;

public class LearningTest {

    public static void main(String[] args) {

        Environment environment =
                new Environment(
                        7,
                        12,
                        1L
                );

        /*
         * Create a controlled brain.
         *
         * EAT starts as the preferred action.
         */
        double[][] weights =
                new double[
                        NeuralBrain.OUTPUT_COUNT
                ][
                        NeuralBrain.INPUT_COUNT
                ];

        double[] biases =
                new double[] {
                        0.0,
                        0.0,
                        0.0,
                        1.0
                };

        NeuralBrain brain =
                new NeuralBrain(
                        weights,
                        biases
                );

        Herbivore herbivore =
                new Herbivore(
                        new Position(
                                3,
                                3
                        ),
                        Direction.EAST,
                        brain
                );

        Plant plant =
                new Plant(
                        new Position(
                                3,
                                4
                        )
                );

        environment.addAgent(
                herbivore
        );

        environment.addAgent(
                plant
        );

        /*
         * Sensor state before eating.
         */
        SensorData sensorsBefore =
                environment.sense(
                        herbivore
                );

        double[] outputsBefore =
                brain.evaluate(
                        sensorsBefore
                );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "           LEARNING TEST"
        );

        System.out.println(
                "======================================"
        );

        System.out.println();

        System.out.println(
                "Before successful action:"
        );

        printOutputs(
                outputsBefore
        );

        System.out.println();

        /*
         * Herbivore performs EAT.
         *
         * Successful action generates
         * positive reinforcement.
         */
        herbivore.takeTurn(
                environment
        );

        System.out.println(
                "Action:"
        );

        System.out.println(
                herbivore.getLastAction()
        );

        System.out.printf(
                "Learning reward: %.2f%n",
                herbivore
                        .getLastLearningReward()
        );

        System.out.println();

        /*
         * Evaluate the same original sensor
         * pattern after learning.
         *
         * The EAT output should now be
         * stronger than before.
         */
        double[] outputsAfter =
                brain.evaluate(
                        sensorsBefore
                );

        System.out.println(
                "After successful learning:"
        );

        printOutputs(
                outputsAfter
        );

        System.out.println();

        System.out.printf(
                "EAT output before: %.4f%n",
                outputsBefore[3]
        );

        System.out.printf(
                "EAT output after : %.4f%n",
                outputsAfter[3]
        );

        System.out.println();

        if (
                outputsAfter[3]
                        >
                outputsBefore[3]
        ) {

            System.out.println(
                    "PASS: successful EAT was reinforced."
            );

        } else {

            System.out.println(
                    "FAIL: EAT output did not increase."
            );
        }
    }

    private static void printOutputs(
            double[] outputs
    ) {

        System.out.printf(
                "TURN_LEFT    = %.4f%n",
                outputs[0]
        );

        System.out.printf(
                "TURN_RIGHT   = %.4f%n",
                outputs[1]
        );

        System.out.printf(
                "MOVE_FORWARD = %.4f%n",
                outputs[2]
        );

        System.out.printf(
                "EAT          = %.4f%n",
                outputs[3]
        );
    }
}