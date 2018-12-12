import java.util.HashMap;

/**
 * The current positions of all the pieces on the board.
 */
public class Board {
    private HashMap<ColouredPiece, BoardPosition> pieceMap;

    public Board() {
        // TODO Start configuration
    }

    public Board(HashMap<ColouredPiece, BoardPosition> currentPosition) {
        pieceMap = (HashMap<ColouredPiece, BoardPosition>) currentPosition.clone();
    }
}
