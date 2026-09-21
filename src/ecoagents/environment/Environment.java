package ecoagents.environment;

import ecoagents.agents.Agent;
import ecoagents.agents.Carnivore;
import ecoagents.agents.Herbivore;
import ecoagents.agents.Plant;

import ecoagents.model.Direction;
import ecoagents.model.Position;
import ecoagents.model.SensorData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Environment {

    private static final int SENSOR_RANGE = 3;

    private final int rows;
    private final int cols;

    private final Random random;

    private final List<Agent> agents =
            new ArrayList<>();

    public Environment(
            int rows,
            int cols,
            long seed
    ) {

        if (
                rows <= 0
                        ||
                cols <= 0
        ) {

            throw new IllegalArgumentException(
                    "Environment size must be positive."
            );
        }

        this.rows = rows;
        this.cols = cols;

        this.random =
                new Random(
                        seed
                );
    }

    public void addAgent(
            Agent agent
    ) {

        agents.add(
                agent
        );
    }

    public List<Agent> getAgents() {

        return Collections.unmodifiableList(
                agents
        );
    }

    /*
     * Toroidal wrapping.
     */
    public Position wrap(
            Position position
    ) {

        int wrappedRow =
                Math.floorMod(
                        position.getRow(),
                        rows
                );

        int wrappedCol =
                Math.floorMod(
                        position.getCol(),
                        cols
                );

        return new Position(
                wrappedRow,
                wrappedCol
        );
    }

    /*
     * Move exactly one cell.
     */
    public Position moveForward(
            Position position,
            Direction direction
    ) {

        return wrap(
                position.move(
                        direction
                )
        );
    }

    /*
     * Move several cells while
     * respecting toroidal boundaries.
     */
    private Position moveSteps(
            Position start,
            Direction direction,
            int steps
    ) {

        Position result =
                start;

        for (
                int i = 0;
                i < steps;
                i++
        ) {

            result =
                    moveForward(
                            result,
                            direction
                    );
        }

        return result;
    }

    /*
     * Local perception system.
     *
     * NEARNESS:
     * all 8 directly neighboring cells.
     *
     * FRONT:
     * cells 2 and 3 ahead.
     *
     * LEFT:
     * cells 2 and 3 to the left.
     *
     * RIGHT:
     * cells 2 and 3 to the right.
     */
    public SensorData sense(
            Agent observer
    ) {

        SensorData data =
                new SensorData();

        Position origin =
                observer.getPosition();

        /*
         * 8-cell nearness.
         */
        senseNearness(
                observer,
                origin,
                data
        );

        Direction forward =
                observer.getDirection();

        Direction left =
                forward.turnLeft();

        Direction right =
                forward.turnRight();

        /*
         * FRONT
         */
        for (
                int step = 2;
                step <= SENSOR_RANGE;
                step++
        ) {

            observeCell(
                    observer,
                    moveSteps(
                            origin,
                            forward,
                            step
                    ),
                    SensorZone.FRONT,
                    data
            );
        }

        /*
         * LEFT
         */
        for (
                int step = 2;
                step <= SENSOR_RANGE;
                step++
        ) {

            observeCell(
                    observer,
                    moveSteps(
                            origin,
                            left,
                            step
                    ),
                    SensorZone.LEFT,
                    data
            );
        }

        /*
         * RIGHT
         */
        for (
                int step = 2;
                step <= SENSOR_RANGE;
                step++
        ) {

            observeCell(
                    observer,
                    moveSteps(
                            origin,
                            right,
                            step
                    ),
                    SensorZone.RIGHT,
                    data
            );
        }

        return data;
    }

    private void senseNearness(
            Agent observer,
            Position origin,
            SensorData data
    ) {

        for (
                int rowDelta = -1;
                rowDelta <= 1;
                rowDelta++
        ) {

            for (
                    int colDelta = -1;
                    colDelta <= 1;
                    colDelta++
            ) {

                if (
                        rowDelta == 0
                                &&
                        colDelta == 0
                ) {

                    continue;
                }

                Position nearby =
                        wrap(
                                origin.translate(
                                        rowDelta,
                                        colDelta
                                )
                        );

                observeCell(
                        observer,
                        nearby,
                        SensorZone.NEARNESS,
                        data
                );
            }
        }
    }

    private void observeCell(
            Agent observer,
            Position position,
            SensorZone zone,
            SensorData data
    ) {

        Position target =
                wrap(
                        position
                );

        for (
                Agent agent
                : agents
        ) {

            if (
                    agent == observer
            ) {

                continue;
            }

            if (
                    !agent.isAlive()
            ) {

                continue;
            }

            if (
                    !agent.getPosition()
                            .equals(
                                    target
                            )
            ) {

                continue;
            }

            classifyObservation(
                    agent,
                    zone,
                    data
            );
        }
    }

    /*
     * Final ecosystem categories.
     *
     * Only:
     *
     * Herbivore
     * Carnivore
     * Plant
     */
    private void classifyObservation(
            Agent detectedAgent,
            SensorZone zone,
            SensorData data
    ) {

        if (
                detectedAgent
                        instanceof Herbivore
        ) {

            addHerbivore(
                    zone,
                    data
            );

        } else if (
                detectedAgent
                        instanceof Carnivore
        ) {

            addCarnivore(
                    zone,
                    data
            );

        } else if (
                detectedAgent
                        instanceof Plant
        ) {

            addPlant(
                    zone,
                    data
            );
        }
    }

    private void addHerbivore(
            SensorZone zone,
            SensorData data
    ) {

        switch (zone) {

            case FRONT ->
                    data.addHerbivoreFront();

            case LEFT ->
                    data.addHerbivoreLeft();

            case RIGHT ->
                    data.addHerbivoreRight();

            case NEARNESS ->
                    data.addHerbivoreNear();
        }
    }

    private void addCarnivore(
            SensorZone zone,
            SensorData data
    ) {

        switch (zone) {

            case FRONT ->
                    data.addCarnivoreFront();

            case LEFT ->
                    data.addCarnivoreLeft();

            case RIGHT ->
                    data.addCarnivoreRight();

            case NEARNESS ->
                    data.addCarnivoreNear();
        }
    }

    private void addPlant(
            SensorZone zone,
            SensorData data
    ) {

        switch (zone) {

            case FRONT ->
                    data.addPlantFront();

            case LEFT ->
                    data.addPlantLeft();

            case RIGHT ->
                    data.addPlantRight();

            case NEARNESS ->
                    data.addPlantNear();
        }
    }

    /*
     * Find one living agent in a cell.
     */
    public Agent getLivingAgentAt(
            Position position,
            Agent excludedAgent
    ) {

        Position target =
                wrap(
                        position
                );

        for (
                Agent agent
                : agents
        ) {

            if (
                    agent == excludedAgent
            ) {

                continue;
            }

            if (
                    !agent.isAlive()
            ) {

                continue;
            }

            if (
                    agent.getPosition()
                            .equals(
                                    target
                            )
            ) {

                return agent;
            }
        }

        return null;
    }

    /*
     * Find a living neighboring entity
     * of a requested type.
     */
    public <T extends Agent> T findAdjacentLivingAgent(
            Position origin,
            Agent excludedAgent,
            Class<T> type
    ) {

        for (
                int rowDelta = -1;
                rowDelta <= 1;
                rowDelta++
        ) {

            for (
                    int colDelta = -1;
                    colDelta <= 1;
                    colDelta++
            ) {

                if (
                        rowDelta == 0
                                &&
                        colDelta == 0
                ) {

                    continue;
                }

                Position candidate =
                        wrap(
                                origin.translate(
                                        rowDelta,
                                        colDelta
                                )
                        );

                Agent found =
                        getLivingAgentAt(
                                candidate,
                                excludedAgent
                        );

                if (
                        found != null
                                &&
                        type.isInstance(
                                found
                        )
                ) {

                    return type.cast(
                            found
                    );
                }
            }
        }

        return null;
    }

    /*
     * Find an empty cardinal neighbor.
     *
     * Used for reproduction.
     */
    public Position findEmptyAdjacentPosition(
            Position origin
    ) {

        List<Direction> directions =
                new ArrayList<>();

        Collections.addAll(
                directions,
                Direction.values()
        );

        Collections.shuffle(
                directions,
                random
        );

        for (
                Direction direction
                : directions
        ) {

            Position candidate =
                    moveForward(
                            origin,
                            direction
                    );

            if (
                    !isOccupied(
                            candidate
                    )
            ) {

                return candidate;
            }
        }

        return null;
    }

    public boolean isInside(
            Position position
    ) {

        return position.getRow() >= 0
                &&
                position.getRow() < rows
                &&
                position.getCol() >= 0
                &&
                position.getCol() < cols;
    }

    public List<Position> getPossibleMoves(
            Position current,
            boolean allowStay
    ) {

        List<Position> moves =
                new ArrayList<>();

        moves.add(
                moveForward(
                        current,
                        Direction.NORTH
                )
        );

        moves.add(
                moveForward(
                        current,
                        Direction.SOUTH
                )
        );

        moves.add(
                moveForward(
                        current,
                        Direction.WEST
                )
        );

        moves.add(
                moveForward(
                        current,
                        Direction.EAST
                )
        );

        if (
                allowStay
        ) {

            moves.add(
                    current
            );
        }

        return moves;
    }

    public Position randomEmptyPosition() {

        if (
                getLivingAgentCount()
                        >=
                rows * cols
        ) {

            throw new IllegalStateException(
                    "Environment has no empty cells."
            );
        }

        Position candidate;

        do {

            candidate =
                    new Position(
                            random.nextInt(
                                    rows
                            ),
                            random.nextInt(
                                    cols
                            )
                    );

        } while (
                isOccupied(
                        candidate
                )
        );

        return candidate;
    }

    public boolean isOccupied(
            Position position
    ) {

        Position target =
                wrap(
                        position
                );

        return agents.stream()
                .filter(
                        Agent::isAlive
                )
                .anyMatch(
                        agent ->
                                agent
                                        .getPosition()
                                        .equals(
                                                target
                                        )
                );
    }

    private int getLivingAgentCount() {

        int count = 0;

        for (
                Agent agent
                : agents
        ) {

            if (
                    agent.isAlive()
            ) {

                count++;
            }
        }

        return count;
    }

    public <T> T randomChoice(
            List<T> items
    ) {

        if (
                items.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Cannot choose from an empty list."
            );
        }

        return items.get(
                random.nextInt(
                        items.size()
                )
        );
    }

    public boolean chance(
            double probability
    ) {

        if (
                probability < 0.0
                        ||
                probability > 1.0
        ) {

            throw new IllegalArgumentException(
                    "Probability must be between 0 and 1."
            );
        }

        return random.nextDouble()
                <
                probability;
    }

    public int getRows() {

        return rows;
    }

    public int getCols() {

        return cols;
    }

    private enum SensorZone {

        FRONT,
        LEFT,
        RIGHT,
        NEARNESS
    }
}