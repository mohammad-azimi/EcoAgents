package ecoagents;

import ecoagents.environment.Environment;
import ecoagents.model.Direction;
import ecoagents.model.Position;

public class ToroidalTest {

    public static void main(String[] args) {

        Environment environment =
                new Environment(
                        7,
                        12,
                        1L
                );

        Position rightEdge =
                new Position(
                        3,
                        11
                );

        Position wrappedRight =
                environment.moveForward(
                        rightEdge,
                        Direction.EAST
                );

        System.out.println("East edge:");
        System.out.println(
                rightEdge + " -> " + wrappedRight
        );

        Position topEdge =
                new Position(
                        0,
                        5
                );

        Position wrappedTop =
                environment.moveForward(
                        topEdge,
                        Direction.NORTH
                );

        System.out.println();
        System.out.println("North edge:");
        System.out.println(
                topEdge + " -> " + wrappedTop
        );

        Position leftEdge =
                new Position(
                        2,
                        0
                );

        Position wrappedLeft =
                environment.moveForward(
                        leftEdge,
                        Direction.WEST
                );

        System.out.println();
        System.out.println("West edge:");
        System.out.println(
                leftEdge + " -> " + wrappedLeft
        );

        Position bottomEdge =
                new Position(
                        6,
                        8
                );

        Position wrappedBottom =
                environment.moveForward(
                        bottomEdge,
                        Direction.SOUTH
                );

        System.out.println();
        System.out.println("South edge:");
        System.out.println(
                bottomEdge + " -> " + wrappedBottom
        );
    }
}