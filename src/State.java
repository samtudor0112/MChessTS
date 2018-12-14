import java.awt.*;
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

    public static final int EITHER_CASTLE = 0;
    public static final int KINGSIDE_CASTLE = 1;
    public static final int QUEENSIDE_CASTLE = 2;
    public static final int NO_CASTLE = 3;

    private Board board;
    private PlayerColour turn;
    private int gameStatus;
    private ArrayList<Move> moveList;
    private int whiteCastlingStatus;
    private int blackCastlingStatus;

    public State(Board board, PlayerColour turn, ArrayList<Move> moveList) {
        this.board = board.clone();
        this.turn = turn;
        this.moveList = moveList;
        updateCastlingStatuses();
        updateGameStatus();
    }

    // Game start state
    public State() {
        board = new Board();
        turn = PlayerColour.WHITE;
        moveList = new ArrayList<>();
        gameStatus = IN_PROGRESS;
        whiteCastlingStatus = EITHER_CASTLE;
        blackCastlingStatus = EITHER_CASTLE;
    }

    // Called by clone method so executeMove doesn't take forever by having to call updateCastlingStatus(). No need to
    // evaluate status since we're cloning a legal state.
    private State(Board board, PlayerColour turn, ArrayList<Move> moveList,
                 int whiteCastlingStatus, int blackCastlingStatus, int gameStatus) {
        this.board = board.clone();
        this.turn = turn;
        this.moveList = moveList;
        this.whiteCastlingStatus = whiteCastlingStatus;
        this.blackCastlingStatus = blackCastlingStatus;
        this.gameStatus = gameStatus;
    }

    public State executeMove(Move move) {
        State newState = this.clone();
        Board newBoard = newState.getBoard();
        if (move.getSpecialMove() == null) {
            newBoard.moveAndTakePiece(move.getPiece(), move.getNewPosition());
        } else if (move.getSpecialMove().equals("En passant")) {
            newBoard.moveAndTakePiece(move.getPiece(), move.getTakePosition());
            newBoard.moveAndTakePiece(move.getPiece(), move.getNewPosition());
        } else if (move.getSpecialMove().equals("Castling")) {
            newBoard.moveAndTakePiece(move.getPiece(), move.getNewPosition());
            newBoard.moveAndTakePiece(move.getCastlingPiece(), move.getCastlingPosition());
        } else {
            // promotion
            newBoard.moveAndTakePiece(move.getPiece(), move.getNewPosition());
            newBoard.replacePieceAtPosition(move.getPromotionTo(), move.getNewPosition());
        }
        newState.getMoveList().add(move);
        newState.updateCastlingStatusesFromLastMove();
        newState.changeTurn();
        newState.updateGameStatus();
        return newState;
    }

    public ArrayList<Move> getAllLegalMoves() {
        // TODO
        return null;
    }

    public void updateGameStatus() {
        // TODO
    }

    // Since this method is called every time a move is executed, we only need to check the very last move to ensure
    // the castling statuses are correct.
    public void updateCastlingStatusesFromLastMove() {
        // TODO
    }

    // Checking from scratch is neccessary if a game is imported.
    public void updateCastlingStatuses() {
        // TODO
    }

    public void changeTurn() {
        turn = turn == PlayerColour.WHITE ? PlayerColour.BLACK : PlayerColour.WHITE;
    }

    public Board getBoard() {
        return board;
    }

    public PlayerColour getTurn() {
        return turn;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public ArrayList<Move> getMoveList() {
        return moveList;
    }

    public State clone() {
        return new State(board, turn, moveList, whiteCastlingStatus, blackCastlingStatus, gameStatus);
    }
}
