package puzzle.model;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Represents the state of the puzzle.
 */
public class PuzzleState implements Cloneable {

    /**
     * The size of the board.
     */
    public static final int BOARD_SIZE = 3;

    /**
     * The index of the block.
     */
    public static final int BLOCK = 0;

    /**
     * The index of the red shoe.
     */
    public static final int RED_SHOE = 1;

    /**
     * The index of the blue shoe.
     */
    public static final int BLUE_SHOE = 2;

    /**
     * The index of the black shoe.
     */
    public static final int BLACK_SHOE = 3;

    private Position[] positions;

    /**
     * Creates a {@code PuzzleState} object that corresponds to the original
     * initial state of the puzzle.
     */
    public PuzzleState() {
        this(new Position(0, 0),
                new Position(2, 0),
                new Position(1, 1),
                new Position(0, 2)
        );
    }

    /**
     * Creates a {@code PuzzleState} object initializing the positions of the
     * pieces with the positions specified. The constructor expects an array of
     * four {@code Position} objects or four {@code Position} objects.
     *
     * @param positions the initial positions of the pieces
     */
    public PuzzleState(Position... positions) {
        checkPositions(positions);
        this.positions = positions.clone();
    }

    private void checkPositions(Position[] positions) {
        if (positions.length != 4) {
            throw new IllegalArgumentException();
        }
        for (var position : positions) {
            if (!isOnBoard(position)) {
                throw new IllegalArgumentException();
            }
        }
        if (positions[BLUE_SHOE].equals(positions[BLACK_SHOE])) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * {@return a copy of the position of the piece specified}
     *
     * @param index the number of a piece
     */
    public Position getPosition(int index) {
        return positions[index];
    }

    /**
     * {@return whether the puzzle is solved}
     */
    public boolean isGoal() {
        return haveSamePosition(RED_SHOE, BLUE_SHOE);
    }

    /**
     * {@return whether the block can be moved to the direction specified}
     *
     * @param direction a direction to which the block is intended to be moved
     */
    public boolean canMove(Direction direction) {
        return switch (direction) {
            case UP -> canMoveUp();
            case RIGHT -> canMoveRight();
            case DOWN -> canMoveDown();
            case LEFT -> canMoveLeft();
        };
    }

    private boolean canMoveUp() {
        return getPosition(BLOCK).row() > 0 && isEmpty(getPosition(BLOCK).moveUp());
    }

    private boolean canMoveRight() {
        if (getPosition(BLOCK).col() == BOARD_SIZE - 1) {
            return false;
        }
        var right = getPosition(BLOCK).moveRight();
        return isEmpty(right)
                || (getPosition(BLACK_SHOE).equals(right) && !haveSamePosition(BLOCK, BLUE_SHOE));
    }

    private boolean canMoveDown() {
        if (getPosition(BLOCK).row() == BOARD_SIZE - 1) {
            return false;
        }
        var down = getPosition(BLOCK).moveDown();
        if (isEmpty(down)) {
            return true;
        }
        if (haveSamePosition(BLACK_SHOE, BLOCK)) {
            return false;
        }
        return getPosition(BLUE_SHOE).equals(down)
                || (getPosition(RED_SHOE).equals(down) && !haveSamePosition(BLUE_SHOE, BLOCK));
    }

    private boolean canMoveLeft() {
        return getPosition(BLOCK).col() > 0 && isEmpty(getPosition(BLOCK).moveLeft());
    }

    /**
     * Moves the block to the direction specified.
     *
     * @param direction the direction to which the block is moved
     */
    public void move(Direction direction) {
        switch (direction) {
            case UP -> moveUp();
            case RIGHT -> moveRight();
            case DOWN -> moveDown();
            case LEFT -> moveLeft();
        }
    }

    private void moveUp() {
        if (haveSamePosition(BLACK_SHOE, BLOCK)) {
            if (haveSamePosition(RED_SHOE, BLOCK)) {
                movePiece(RED_SHOE, Direction.UP);
            }
            movePiece(BLACK_SHOE, Direction.UP);
        }
        movePiece(BLOCK, Direction.UP);
    }

    private void moveRight() {
        move(Direction.RIGHT, RED_SHOE, BLUE_SHOE, BLACK_SHOE);
    }

    private void moveDown() {
        move(Direction.DOWN, RED_SHOE, BLUE_SHOE, BLACK_SHOE);
    }

    private void moveLeft() {
        move(Direction.LEFT, RED_SHOE, BLUE_SHOE);
    }

    /**
     * Moves the block to the direction specified and also any of the shoes
     * specified that are at the same position with the block.
     *
     * @param direction the direction to which the block is moved
     * @param shoes the shoes that must be moved together with the block
     */
    private void move(Direction direction, int... shoes) {
        for (var i : shoes) {
            if (haveSamePosition(i, BLOCK)) {
                movePiece(i, direction);
            }
        }
        movePiece(BLOCK, direction);
    }

    private void movePiece(int index, Direction direction) {
        var newPosition = getPosition(index).move(direction);
        positions[index] = newPosition;
    }

    /**
     * {@return the set of directions to which the block can be moved}
     */
    public Set<Direction> getLegalMoves() {
        var legalMoves = EnumSet.noneOf(Direction.class);
        for (var direction : Direction.values()) {
            if (canMove(direction)) {
                legalMoves.add(direction);
            }
        }
        return legalMoves;
    }

    private boolean haveSamePosition(int i, int j) {
        return getPosition(i).equals(getPosition(j));
    }

    private boolean isOnBoard(Position position) {
        return position.row() >= 0 && position.row() < BOARD_SIZE &&
                position.col() >= 0 && position.col() < BOARD_SIZE;
    }

    private boolean isEmpty(Position position) {
        for (var p : positions) {
            if (p.equals(position)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return (o instanceof PuzzleState other) && Arrays.equals(positions, other.positions);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(positions);
    }

    @Override
    public PuzzleState clone() {
        PuzzleState copy = null;
        try {
            copy = (PuzzleState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        copy.positions = positions.clone();
        return copy;
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(",", "[", "]");
        for (var position : positions) {
            sj.add(position.toString());
        }
        return sj.toString();
    }

}
