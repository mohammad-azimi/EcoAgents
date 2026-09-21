package ecoagents.brain;

import ecoagents.model.Action;
import ecoagents.model.SensorData;

import java.util.Arrays;
import java.util.Random;

public class NeuralBrain {

    public static final int INPUT_COUNT = 12;
    public static final int OUTPUT_COUNT = 4;

    /*
     * Learning configuration.
     */
    private static final double LEARNING_RATE = 0.05;

    /*
     * Neural parameter limits.
     */
    private static final double MIN_WEIGHT = -3.0;
    private static final double MAX_WEIGHT = 3.0;

    /*
     * Evolution configuration.
     *
     * Offspring inherit the parent's neural
     * network and receive small mutations.
     */
    private static final double MUTATION_PROBABILITY = 0.10;
    private static final double MUTATION_STRENGTH = 0.10;

    private final double[][] weights;
    private final double[] biases;

    private double[] lastOutputs;

    /*
     * Random neural brain.
     */
    public NeuralBrain(long seed) {

        weights =
                new double[
                        OUTPUT_COUNT
                ][
                        INPUT_COUNT
                ];

        biases =
                new double[
                        OUTPUT_COUNT
                ];

        lastOutputs =
                new double[
                        OUTPUT_COUNT
                ];

        Random random =
                new Random(seed);

        initializeRandomValues(
                random
        );
    }

    /*
     * Constructor from existing
     * weights and biases.
     */
    public NeuralBrain(
            double[][] weights,
            double[] biases
    ) {

        validateNetworkDimensions(
                weights,
                biases
        );

        this.weights =
                new double[
                        OUTPUT_COUNT
                ][
                        INPUT_COUNT
                ];

        for (
                int output = 0;
                output < OUTPUT_COUNT;
                output++
        ) {

            this.weights[output] =
                    Arrays.copyOf(
                            weights[output],
                            INPUT_COUNT
                    );
        }

        this.biases =
                Arrays.copyOf(
                        biases,
                        OUTPUT_COUNT
                );

        this.lastOutputs =
                new double[
                        OUTPUT_COUNT
                ];
    }

    /*
     * Neural equation:
     *
     * Out(i) =
     * bias(i)
     * +
     * sum(
     *     weight(i,j) * input(j)
     * )
     */
    public double[] evaluate(
            SensorData sensorData
    ) {

        return evaluate(
                sensorData.toInputVector()
        );
    }

    public double[] evaluate(
            double[] inputs
    ) {

        if (
                inputs.length
                        != INPUT_COUNT
        ) {

            throw new IllegalArgumentException(
                    "Expected "
                            + INPUT_COUNT
                            + " inputs, but received "
                            + inputs.length
            );
        }

        double[] outputs =
                new double[
                        OUTPUT_COUNT
                ];

        for (
                int output = 0;
                output < OUTPUT_COUNT;
                output++
        ) {

            double value =
                    biases[output];

            for (
                    int input = 0;
                    input < INPUT_COUNT;
                    input++
            ) {

                value +=
                        weights[output][input]
                                *
                                inputs[input];
            }

            outputs[output] =
                    value;
        }

        lastOutputs =
                Arrays.copyOf(
                        outputs,
                        outputs.length
                );

        return outputs;
    }

    /*
     * Winner-takes-all action selection.
     */
    public Action chooseAction(
            SensorData sensorData
    ) {

        double[] outputs =
                evaluate(
                        sensorData
                );

        int winnerIndex = 0;

        for (
                int i = 1;
                i < outputs.length;
                i++
        ) {

            if (
                    outputs[i]
                            >
                    outputs[winnerIndex]
            ) {

                winnerIndex = i;
            }
        }

        return actionFromIndex(
                winnerIndex
        );
    }

    /*
     * Simple reinforcement adaptation.
     */
    public void learn(
            SensorData sensorData,
            Action action,
            double reward
    ) {

        int outputIndex =
                indexFromAction(
                        action
                );

        double[] inputs =
                sensorData.toInputVector();

        for (
                int input = 0;
                input < INPUT_COUNT;
                input++
        ) {

            double change =
                    LEARNING_RATE
                            *
                            reward
                            *
                            inputs[input];

            weights[outputIndex][input] =
                    clamp(
                            weights[outputIndex][input]
                                    +
                                    change
                    );
        }

        biases[outputIndex] =
                clamp(
                        biases[outputIndex]
                                +
                                LEARNING_RATE
                                        *
                                        reward
                );
    }

    /*
     * Creates an inherited neural network
     * for a descendant.
     *
     * Step 1:
     * copy the parent's current learned
     * weights and biases.
     *
     * Step 2:
     * introduce small mutations.
     */
    public NeuralBrain createOffspringBrain(
            long mutationSeed
    ) {

        Random random =
                new Random(
                        mutationSeed
                );

        double[][] childWeights =
                getWeightsCopy();

        double[] childBiases =
                getBiasesCopy();

        int mutationCount = 0;

        for (
                int output = 0;
                output < OUTPUT_COUNT;
                output++
        ) {

            for (
                    int input = 0;
                    input < INPUT_COUNT;
                    input++
            ) {

                if (
                        random.nextDouble()
                                <
                        MUTATION_PROBABILITY
                ) {

                    double mutation =
                            randomMutation(
                                    random
                            );

                    childWeights[output][input] =
                            clamp(
                                    childWeights[output][input]
                                            +
                                            mutation
                            );

                    mutationCount++;
                }
            }

            if (
                    random.nextDouble()
                            <
                    MUTATION_PROBABILITY
            ) {

                childBiases[output] =
                        clamp(
                                childBiases[output]
                                        +
                                        randomMutation(
                                                random
                                        )
                        );

                mutationCount++;
            }
        }

        /*
         * Guarantee at least one small mutation
         * so the child is not an exact clone.
         */
        if (mutationCount == 0) {

            int output =
                    random.nextInt(
                            OUTPUT_COUNT
                    );

            int input =
                    random.nextInt(
                            INPUT_COUNT
                    );

            double mutation =
                    random.nextBoolean()
                            ?
                            MUTATION_STRENGTH
                            :
                            -MUTATION_STRENGTH;

            childWeights[output][input] =
                    clamp(
                            childWeights[output][input]
                                    +
                                    mutation
                    );
        }

        return new NeuralBrain(
                childWeights,
                childBiases
        );
    }

    private double randomMutation(
            Random random
    ) {

        return (
                random.nextDouble()
                        *
                        2.0
                        -
                        1.0
        )
                *
                MUTATION_STRENGTH;
    }

    private int indexFromAction(
            Action action
    ) {

        return switch (action) {

            case TURN_LEFT -> 0;

            case TURN_RIGHT -> 1;

            case MOVE_FORWARD -> 2;

            case EAT -> 3;
        };
    }

    private Action actionFromIndex(
            int index
    ) {

        return switch (index) {

            case 0 ->
                    Action.TURN_LEFT;

            case 1 ->
                    Action.TURN_RIGHT;

            case 2 ->
                    Action.MOVE_FORWARD;

            case 3 ->
                    Action.EAT;

            default ->
                    throw new IllegalArgumentException(
                            "Invalid neural output index: "
                                    + index
                    );
        };
    }

    private void initializeRandomValues(
            Random random
    ) {

        for (
                int output = 0;
                output < OUTPUT_COUNT;
                output++
        ) {

            biases[output] =
                    randomInitialValue(
                            random
                    );

            for (
                    int input = 0;
                    input < INPUT_COUNT;
                    input++
            ) {

                weights[output][input] =
                        randomInitialValue(
                                random
                        );
            }
        }
    }

    private double randomInitialValue(
            Random random
    ) {

        return random.nextDouble()
                *
                2.0
                -
                1.0;
    }

    private double clamp(
            double value
    ) {

        return Math.max(
                MIN_WEIGHT,
                Math.min(
                        MAX_WEIGHT,
                        value
                )
        );
    }

    private void validateNetworkDimensions(
            double[][] weights,
            double[] biases
    ) {

        if (
                weights.length
                        != OUTPUT_COUNT
        ) {

            throw new IllegalArgumentException(
                    "Neural network must have "
                            + OUTPUT_COUNT
                            + " output rows."
            );
        }

        for (
                double[] outputWeights
                : weights
        ) {

            if (
                    outputWeights.length
                            != INPUT_COUNT
            ) {

                throw new IllegalArgumentException(
                        "Each neural output must have "
                                + INPUT_COUNT
                                + " weights."
                );
            }
        }

        if (
                biases.length
                        != OUTPUT_COUNT
        ) {

            throw new IllegalArgumentException(
                    "Neural network must have "
                            + OUTPUT_COUNT
                            + " biases."
            );
        }
    }

    public double[] getLastOutputs() {

        return Arrays.copyOf(
                lastOutputs,
                lastOutputs.length
        );
    }

    public double[][] getWeightsCopy() {

        double[][] copy =
                new double[
                        OUTPUT_COUNT
                ][
                        INPUT_COUNT
                ];

        for (
                int output = 0;
                output < OUTPUT_COUNT;
                output++
        ) {

            copy[output] =
                    Arrays.copyOf(
                            weights[output],
                            INPUT_COUNT
                    );
        }

        return copy;
    }

    public double[] getBiasesCopy() {

        return Arrays.copyOf(
                biases,
                biases.length
        );
    }
}