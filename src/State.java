import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores the current state of the match, including the board state, who's turn
 * it is, and whether the game has ended
 */
public class State {
    public static final int IN_PROGRESS = 0;
    public static final int WHITE_WIN = 1;
    public static final int BLACK_WIN = 2;
    public static final int STALEMATE = 3;

    private HashMap<ColouredPiece, BoardPosition> board;
    private PlayerColour turn;
    private int status;

    public State(HashMap<ColouredPiece, BoardPosition> board, PlayerColour turn, int status) {
        this.board = (HashMap<ColouredPiece, BoardPosition>) board.clone();
        this.turn = turn;
        this.status = status;
    }

    // Game start state
    public State() {
        this.board = null;// TODO
        this.turn = PlayerColour.WHITE;
        this.status = IN_PROGRESS;
    }

    public State executeMove(Move move) {
        State newState = this.clone();
        HashMap<ColouredPiece, BoardPosition> newBoard = newState.getBoard();
        if (move.getSpecialMove() == null) {
            if (newState.getPieceAtPosition(move.getNewPosition()) != null) {
                newBoard.remove(newState.getPieceAtPosition(move.getNewPosition()));
            }
            newBoard.put(move.getPiece(), move.getNewPosition());
        } else if (move.getSpecialMove().equals("Castling")) {
            newBoard.put(move.getPiece(), move.getNewPosition());
            // TODO move rook
        } else {
            // promotion
            if (newState.getPieceAtPosition(move.getNewPosition()) != null) {
                newBoard.remove(newState.getPieceAtPosition(move.getNewPosition()));
            }
            newBoard.remove(move.getPiece());
            newBoard.put(move.getPromotionTo(), move.getNewPosition());
        }
        newState.setTurn(newState.getTurn() == PlayerColour.WHITE ? PlayerColour.BLACK : PlayerColour.WHITE);
        newState.updateStatus();
        return newState;
    }

    public ArrayList<Move> getAllLegalMoves() {
        // TODO
        return null;
    }

    public void updateStatus() {
        // TODO
    }

    public ColouredPiece getPieceAtPosition(BoardPosition position) {
        for (Map.Entry<ColouredPiece, BoardPosition> entry : board.entrySet()) {
            if (entry.getValue().equals(position)) {
                return entry.getKey();
            }
        }
        // No piece at that position: return null
        return null;
    }

    public HashMap<ColouredPiece, BoardPosition> getBoard() {
        return board;
    }

    public PlayerColour getTurn() {
        return turn;
    }

    public void setTurn(PlayerColour colour) {
        turn = colour;
    }

    public int getStatus() {
        return status;
    }

    public State clone() {
        return new State(board, turn, status);
    }
}
