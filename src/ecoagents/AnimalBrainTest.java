package ecoagents;

import ecoagents.agents.Herbivore;
import ecoagents.agents.Plant;
import ecoagents.brain.NeuralBrain;
import ecoagents.environment.Environment;
import ecoagents.model.Direction;
import ecoagents.model.Position;

public class AnimalBrainTest {

    public static void main(String[] args) {

        Environment environment =
                new Environment(
                        7,
                        12,
                        1L
                );

        /*
         * Force EAT to be the neural winner.
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
                        5.0
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

        System.out.println(
                "Before turn:"
        );

        System.out.printf(
                "Herbivore energy: %.2f%n",
                herbivore.getEnergy()
        );

        System.out.println(
                "Plant alive: "
                        + plant.isAlive()
        );

        System.out.println();

        herbivore.takeTurn(
                environment
        );

        System.out.println(
                "After turn:"
        );

        System.out.println(
                "Herbivore action: "
                        + herbivore.getLastAction()
        );

        System.out.printf(
                "Herbivore energy: %.2f%n",
                herbivore.getEnergy()
        );

        System.out.println(
                "Plant alive: "
                        + plant.isAlive()
        );

        System.out.println();

        System.out.println(
                "Energy calculation:"
        );

        System.out.println(
                "16.00 - 0.02 metabolism + 1.00 food"
        );

        System.out.println();

        System.out.println(
                "Expected energy: 16.98"
        );

        System.out.println(
                "Expected plant alive: false"
        );
    }
}