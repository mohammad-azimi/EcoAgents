package ecoagents;

import ecoagents.brain.NeuralBrain;
import ecoagents.model.Action;
import ecoagents.model.SensorData;

import java.util.Arrays;

public class NeuralBrainTest {

    public static void main(String[] args) {

        /*
         * Same sensor pattern used in
         * the previous SensorTest.
         *
         * Active inputs:
         *
         * CF = 1
         * HL = 1
         * PR = 1
         * PN = 1
         */
        SensorData sensors =
                new SensorData();

        sensors.addCarnivoreFront();
        sensors.addHerbivoreLeft();
        sensors.addPlantRight();
        sensors.addPlantNear();

        /*
         * Four outputs:
         *
         * 0 = TURN_LEFT
         * 1 = TURN_RIGHT
         * 2 = MOVE_FORWARD
         * 3 = EAT
         */
        double[][] weights =
                new double[
                        NeuralBrain.OUTPUT_COUNT
                ][
                        NeuralBrain.INPUT_COUNT
                ];

        double[] biases =
                new double[
                        NeuralBrain.OUTPUT_COUNT
                ];

        /*
         * TURN_LEFT:
         *
         * bias = 0.2
         * CF weight = 0.5
         *
         * result:
         * 0.2 + 0.5 = 0.7
         */
        biases[0] = 0.2;
        weights[0][1] = 0.5;

        /*
         * TURN_RIGHT:
         *
         * bias = -0.2
         * HL weight = 0.3
         *
         * result:
         * -0.2 + 0.3 = 0.1
         */
        biases[1] = -0.2;
        weights[1][3] = 0.3;

        /*
         * MOVE_FORWARD:
         *
         * PR weight = 1.5
         *
         * result:
         * 1.5
         */
        weights[2][8] = 1.5;

        /*
         * EAT:
         *
         * bias = 0.1
         * PN weight = 2.0
         *
         * result:
         * 0.1 + 2.0 = 2.1
         */
        biases[3] = 0.1;
        weights[3][11] = 2.0;

        NeuralBrain brain =
                new NeuralBrain(
                        weights,
                        biases
                );

        double[] outputs =
                brain.evaluate(
                        sensors
                );

        Action action =
                brain.chooseAction(
                        sensors
                );

        System.out.println(
                "Sensor inputs:"
        );

        System.out.println(
                Arrays.toString(
                        sensors.toInputVector()
                )
        );

        System.out.println();

        System.out.println(
                "Neural outputs:"
        );

        System.out.println(
                "TURN_LEFT    = "
                        + outputs[0]
        );

        System.out.println(
                "TURN_RIGHT   = "
                        + outputs[1]
        );

        System.out.println(
                "MOVE_FORWARD = "
                        + outputs[2]
        );

        System.out.println(
                "EAT          = "
                        + outputs[3]
        );

        System.out.println();

        System.out.println(
                "Winner:"
        );

        System.out.println(
                action
        );

        System.out.println();

        System.out.println(
                "Expected winner:"
        );

        System.out.println(
                "EAT"
        );
    }
}