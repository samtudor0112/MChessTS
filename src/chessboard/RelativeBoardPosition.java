package chessboard;

public class RelativeBoardPosition {

    // Can be -7 to 7
    protected int row;
    protected int column;

    public RelativeBoardPosition() {}

    public RelativeBoardPosition(int column, int row) throws InvalidBoardPositionException {
        setBoardPosition(column, row);
    }

    protected void setBoardPosition(int column, int row) throws InvalidBoardPositionException {
        if ((-8 < row && row < 8) && (-8 < column && column < 8)) {
            this.row = row;
            this.column = column;
        } else {
            throw new InvalidBoardPositionException("Invalid board location");
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
