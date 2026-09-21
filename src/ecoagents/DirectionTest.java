package ecoagents;

import ecoagents.model.Direction;

public class DirectionTest {

    public static void main(String[] args) {

        Direction direction =
                Direction.NORTH;

        System.out.println(
                "Start      : " + direction
        );

        direction =
                direction.turnRight();

        System.out.println(
                "Turn right : " + direction
        );

        direction =
                direction.turnRight();

        System.out.println(
                "Turn right : " + direction
        );

        direction =
                direction.turnLeft();

        System.out.println(
                "Turn left  : " + direction
        );

        direction =
                direction.turnLeft();

        System.out.println(
                "Turn left  : " + direction
        );
    }
}