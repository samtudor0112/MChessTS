package chessboard;

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
}
