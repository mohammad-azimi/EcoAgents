package ecoagents;

import ecoagents.agents.Herbivore;
import ecoagents.agents.Plant;
import ecoagents.brain.NeuralBrain;
import ecoagents.environment.Environment;
import ecoagents.model.Direction;
import ecoagents.model.Position;

public class NearnessEatTest {

    public static void main(String[] args) {

        Environment environment =
                new Environment(
                        7,
                        12,
                        5L
                );

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
                        5.0
                };

        Herbivore herbivore =
                new Herbivore(
                        new Position(
                                3,
                                3
                        ),
                        Direction.EAST,
                        new NeuralBrain(
                                weights,
                                biases
                        )
                );

        /*
         * Plant is NORTH of the Herbivore,
         * not directly in front.
         *
         * But it is still in NEARNESS.
         */
        Plant plant =
                new Plant(
                        new Position(
                                2,
                                3
                        )
                );

        environment.addAgent(
                herbivore
        );

        environment.addAgent(
                plant
        );

        System.out.println(
                "Herbivore position : "
                        + herbivore.getPosition()
        );

        System.out.println(
                "Herbivore direction: "
                        + herbivore.getDirection()
        );

        System.out.println(
                "Plant position      : "
                        + plant.getPosition()
        );

        System.out.println();

        System.out.println(
                "PN before action: "
                        +
                environment
                        .sense(
                                herbivore
                        )
                        .getPlantNear()
        );

        herbivore.takeTurn(
                environment
        );

        System.out.println();

        System.out.println(
                "Action:"
        );

        System.out.println(
                herbivore.getLastAction()
        );

        System.out.println();

        System.out.println(
                "Plant alive: "
                        + plant.isAlive()
        );

        System.out.println();

        if (
                !plant.isAlive()
        ) {

            System.out.println(
                    "PASS: Herbivore can eat inside the 8-cell nearness area."
            );

        } else {

            System.out.println(
                    "FAIL: Plant was not eaten."
            );
        }
    }
}