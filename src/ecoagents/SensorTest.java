package ecoagents;

import ecoagents.agents.Carnivore;
import ecoagents.agents.Herbivore;
import ecoagents.agents.Plant;

import ecoagents.brain.NeuralBrain;
import ecoagents.environment.Environment;

import ecoagents.model.Direction;
import ecoagents.model.Position;
import ecoagents.model.SensorData;

public class SensorTest {

    public static void main(
            String[] args
    ) {

        Environment environment =
                new Environment(
                        7,
                        12,
                        1L
                );

        Herbivore observer =
                new Herbivore(
                        new Position(
                                3,
                                3
                        ),
                        Direction.EAST,
                        new NeuralBrain(
                                10L
                        )
                );

        environment.addAgent(
                observer
        );

        /*
         * Plant directly EAST.
         *
         * Inside NEARNESS.
         */
        Plant nearPlant =
                new Plant(
                        new Position(
                                3,
                                4
                        )
                );

        environment.addAgent(
                nearPlant
        );

        /*
         * Carnivore two cells ahead.
         *
         * Observer faces EAST,
         * so this belongs to FRONT.
         */
        Carnivore frontCarnivore =
                new Carnivore(
                        new Position(
                                3,
                                5
                        ),
                        Direction.NORTH,
                        new NeuralBrain(
                                20L
                        )
                );

        environment.addAgent(
                frontCarnivore
        );

        /*
         * Herbivore directly NORTH.
         *
         * Inside NEARNESS.
         */
        Herbivore nearHerbivore =
                new Herbivore(
                        new Position(
                                2,
                                3
                        ),
                        Direction.SOUTH,
                        new NeuralBrain(
                                30L
                        )
                );

        environment.addAgent(
                nearHerbivore
        );

        /*
         * Second Plant directly SOUTH.
         *
         * Inside NEARNESS.
         */
        Plant secondNearPlant =
                new Plant(
                        new Position(
                                4,
                                3
                        )
                );

        environment.addAgent(
                secondNearPlant
        );

        SensorData sensors =
                environment.sense(
                        observer
                );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "              SENSOR TEST"
        );

        System.out.println(
                "========================================"
        );

        System.out.println();

        System.out.println(
                "Observer position : "
                        + observer.getPosition()
        );

        System.out.println(
                "Observer direction: "
                        + observer.getDirection()
        );

        System.out.println();

        System.out.println(
                "Input order:"
        );

        System.out.println(
                "HF CF PF HL CL PL HR CR PR HN CN PN"
        );

        System.out.println();

        System.out.println(
                "Sensor vector:"
        );

        System.out.println(
                sensors
        );

        System.out.println();

        double[] expected =
                new double[] {
                        0.0, 1.0, 0.0,
                        0.0, 0.0, 0.0,
                        0.0, 0.0, 0.0,
                        1.0, 0.0, 2.0
                };

        System.out.println(
                "Expected:"
        );

        System.out.println(
                "[0.0, 1.0, 0.0, "
                        + "0.0, 0.0, 0.0, "
                        + "0.0, 0.0, 0.0, "
                        + "1.0, 0.0, 2.0]"
        );

        System.out.println();

        double[] actual =
                sensors.toInputVector();

        boolean passed = true;

        for (
                int i = 0;
                i < expected.length;
                i++
        ) {

            if (
                    actual[i]
                            !=
                    expected[i]
            ) {

                passed = false;

                break;
            }
        }

        if (
                passed
        ) {

            System.out.println(
                    "PASS: sensor vector is correct."
            );

        } else {

            System.out.println(
                    "FAIL: sensor vector is incorrect."
            );
        }
    }
}