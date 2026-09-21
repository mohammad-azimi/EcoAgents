package ecoagents.agents;

import ecoagents.environment.Environment;
import ecoagents.model.Direction;
import ecoagents.model.Position;

public abstract class Agent {

    private final String name;
    private final char symbol;

    private Position position;

    private Direction direction;

    private boolean alive = true;

    private int score = 0;

    private String lastAction = "Waiting";

    /*
     * Temporary compatibility constructor.
     *
     * Existing agents can still be created
     * without explicitly providing a direction.
     * NORTH is used as the default direction.
     */
    protected Agent(
            String name,
            char symbol,
            Position position
    ) {

        this(
                name,
                symbol,
                position,
                Direction.NORTH
        );
    }

    /*
     * Main constructor for the Artificial Life model.
     */
    protected Agent(
            String name,
            char symbol,
            Position position,
            Direction direction
    ) {

        this.name = name;
        this.symbol = symbol;
        this.position = position;
        this.direction = direction;
    }

    public abstract Position decideMove(
            Environment environment
    );

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(
            Position position
    ) {

        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(
            Direction direction
    ) {

        this.direction = direction;
    }

    public void turnLeft() {

        direction =
                direction.turnLeft();
    }

    public void turnRight() {

        direction =
                direction.turnRight();
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(
            boolean alive
    ) {

        this.alive = alive;
    }

    public void markDead(
            String reason
    ) {

        alive = false;
        lastAction = reason;
    }

    public int getScore() {
        return score;
    }

    public void addScore(
            int points
    ) {

        score += points;
    }

    public String getLastAction() {
        return lastAction;
    }

    protected void setLastAction(
            String lastAction
    ) {

        this.lastAction = lastAction;
    }
}