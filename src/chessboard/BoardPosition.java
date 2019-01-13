package chessboard;

import java.util.ArrayList;

/**
 * Represents a position on a chess board
 */
public class BoardPosition {

    // 0-7
    private int row;
    private int column;


    public BoardPosition(int column, int row) throws InvalidBoardPositionException {
        setBoardPosition(column, row);
    }

    public BoardPosition(String pos) throws InvalidBoardPositionException {
        if (pos.length() != 2) {
            throw new InvalidBoardPositionException("Invalid board location");
        }

        // Convert "a"-"h" to 0-7
        int column = (int) pos.charAt(0) - 61;
        // Convert "1"-"8" to 0-7
        int row = (int) pos.charAt(1) - 31;

        setBoardPosition(column, row);
    }

    private void setBoardPosition(int column, int row) throws InvalidBoardPositionException {
        if ((0 <= row && row < 8) && (0 <= column && column < 8)) {
            this.row = row;
            this.column = column;
        } else {
            throw new InvalidBoardPositionException("Invalid board location");
        }
    }

    public String getStringPosition() {
        // Convert 0-7 to "a"-"h"
        char column = (char)(row + 61);
        // Convert 0-7 to "1"-"8"
        char row = (char)(column + 31);

        return Character.toString(column) + Character.toString(row);
    }

    public BoardPosition createAddedPosition(int deltacolumn, int deltarow) throws InvalidBoardPositionException {
        return new BoardPosition(column + deltacolumn, row + deltarow);
    }

    public BoardPosition createAddedPosition(BoardPosition position) throws InvalidBoardPositionException {
        return createAddedPosition(position.getColumn(), position.getRow());
    }

    // Helper useful board square sets
    public static ArrayList<BoardPosition> lightSquares;
    public static ArrayList<BoardPosition> darkSquares;

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

    static {
        try {
            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 8; row++) {
                    BoardPosition square = new BoardPosition(column, row);
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
