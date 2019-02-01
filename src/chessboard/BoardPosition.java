package chessboard;

import java.util.ArrayList;

/**
 * Represents a position on a chess board
 */
public class BoardPosition extends RelativeBoardPosition {

    public BoardPosition(int column, int row) throws InvalidBoardPositionException {
        super(column, row);
    }

    public BoardPosition(String pos) throws InvalidBoardPositionException {
        if (pos.length() != 2) {
            throw new InvalidBoardPositionException("Invalid board location");
        }

        // Convert "a"-"h" to 0-7
        int column = (int) pos.charAt(0) - 97;
        // Convert "1"-"8" to 0-7
        int row = (int) pos.charAt(1) - 49;

        setBoardPosition(column, row);
    }

    @Override
    protected void setBoardPosition(int column, int row) throws InvalidBoardPositionException {
        // Row and column are 0 to 7 instead of -7 to 7
        if ((0 <= row && row < 8) && (0 <= column && column < 8)) {
            this.row = row;
            this.column = column;
        } else {
            throw new InvalidBoardPositionException("Invalid board location");
        }
    }

    public String toString() {
        // Convert 0-7 to "a"-"h"
        char columnChar = (char)(column + 97);
        // Convert 0-7 to "1"-"8"
        char rowChar = (char)(row + 49);

        return Character.toString(columnChar) + Character.toString(rowChar);
    }

    public BoardPosition addRelativePosition(RelativeBoardPosition position) throws InvalidBoardPositionException {
        return new BoardPosition(column + position.getColumn(), row + position.getRow());
    }

    // Helper useful board square sets
    public static ArrayList<BoardPosition> lightSquares = new ArrayList<>();
    public static ArrayList<BoardPosition> darkSquares = new ArrayList<>();
    public static ArrayList<BoardPosition> allSquares = new ArrayList<>();

    public int getCoordinatePosition() {
        return 10 * column + row;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BoardPosition) {
            BoardPosition pos = (BoardPosition) obj;
            return pos.getCoordinatePosition() == this.getCoordinatePosition();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getCoordinatePosition();
    }

    static {
        try {
            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 8; row++) {
                    BoardPosition square = new BoardPosition(column, row);
                    allSquares.add(square);
                    if (column % 2 == 0) {
                        // Even rows are dark squares
                        if (row % 2 == 0) {
                            darkSquares.add(square);
                        } else {
                            lightSquares.add(square);
                        }
                    } else {
                        // Even rows are light squares
                        if (row % 2 == 0) {
                            lightSquares.add(square);
                        } else {
                            darkSquares.add(square);
                        }
                    }
                }
            }
        } catch (InvalidBoardPositionException e) {
            // This will never happen
            System.out.println("Something's wrong!");
        }
    }
}
