package ecoagents.model;

import java.util.Objects;

public class Position {

    private final int row;
    private final int col;

    public Position(int row, int col) {

        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Position translate(
            int rowDelta,
            int colDelta
    ) {

        return new Position(
                row + rowDelta,
                col + colDelta
        );
    }

    public Position move(
            Direction direction
    ) {

        return translate(
                direction.getRowDelta(),
                direction.getColDelta()
        );
    }

    public int manhattanDistance(
            Position other
    ) {

        return Math.abs(
                row - other.row
        )
                +
                Math.abs(
                        col - other.col
                );
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Position)) {
            return false;
        }

        Position other =
                (Position) obj;

        return row == other.row
                &&
                col == other.col;
    }

    @Override
    public int hashCode() {

        return Objects.hash(
                row,
                col
        );
    }

    @Override
    public String toString() {

        return "("
                + row
                + ", "
                + col
                + ")";
    }
}