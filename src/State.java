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

    private HashMap<ColouredPiece, BoardPosition> board;
    private PlayerColour turn;
    private int gameStatus;
    private ArrayList<Move> moveList;
    private int whiteCastlingStatus;
    private int blackCastlingStatus;

    public State(HashMap<ColouredPiece, BoardPosition> board, PlayerColour turn, ArrayList<Move> moveList) {
        this.board = (HashMap<ColouredPiece, BoardPosition>) board.clone();
        this.turn = turn;
        this.moveList = moveList;
        updateCastlingStatuses();
        updateGameStatus();
    }

    // Game start state
    public State() {
        board = State.generateStartBoard();
        turn = PlayerColour.WHITE;
        moveList = new ArrayList<>();
        gameStatus = IN_PROGRESS;
        whiteCastlingStatus = EITHER_CASTLE;
        blackCastlingStatus = EITHER_CASTLE;
    }

    // Called by clone method so executeMove doesn't take forever by having to call updateCastlingStatus(). No need to
    // evaluate status since we're cloning a legal state.
    private State(HashMap<ColouredPiece, BoardPosition> board, PlayerColour turn, ArrayList<Move> moveList,
                 int whiteCastlingStatus, int blackCastlingStatus, int gameStatus) {
        this.board = (HashMap<ColouredPiece, BoardPosition>) board.clone();
        this.turn = turn;
        this.moveList = moveList;
        this.whiteCastlingStatus = whiteCastlingStatus;
        this.blackCastlingStatus = blackCastlingStatus;
        this.gameStatus = gameStatus;
    }

    public State executeMove(Move move) {
        State newState = this.clone();
        HashMap<ColouredPiece, BoardPosition> newBoard = newState.getBoard();
        if (move.getSpecialMove() == null) {
            if (newState.getPieceAtPosition(move.getNewPosition()) != null) {
                newBoard.remove(newState.getPieceAtPosition(move.getNewPosition()));
            }
            if (newState.getPieceAtPosition(move.getNewPosition()) == null && move.getTaking()) {
                // En passant
                int direction = move.getPiece().getColour() == PlayerColour.WHITE ? 1 : -1;
                BoardPosition enpassantPosition;
                try {
                    enpassantPosition = new BoardPosition(move.getNewPosition().getColumn(),
                            move.getNewPosition().getRow() + direction);
                } catch (InvalidBoardPositionException e) {
                    // This should never happen
                    System.out.println("Something's wrong!");
                    return null;
                }
                newBoard.remove(newState.getPieceAtPosition(enpassantPosition));
            }
            newBoard.put(move.getPiece(), move.getNewPosition());
        } else if (move.getSpecialMove().equals("Castling")) {
            newBoard.put(move.getPiece(), move.getNewPosition());
            BoardPosition castlingRookPosition;
            BoardPosition newCastlingRookPosition;
            try {
                int row = move.getPiece().getColour() == PlayerColour.WHITE ? 0 : 7;
                if (move.getNewPosition().equals(new BoardPosition(6, row))) {
                    // Kingside castle
                    castlingRookPosition = new BoardPosition(7, row);
                    newCastlingRookPosition = new BoardPosition(5, row);
                } else {
                    // Queenside castle
                    castlingRookPosition = new BoardPosition(0, row);
                    newCastlingRookPosition = new BoardPosition(3, row);
                }
            } catch (InvalidBoardPositionException e) {
                // This should never happen
                System.out.println("Something's wrong!");
                return null;
            }
            ColouredPiece castlingRook = newState.getPieceAtPosition(castlingRookPosition);
            newBoard.put(castlingRook, newCastlingRookPosition);
        } else {
            // promotion
            if (newState.getPieceAtPosition(move.getNewPosition()) != null) {
                newBoard.remove(newState.getPieceAtPosition(move.getNewPosition()));
            }
            newBoard.remove(move.getPiece());
            newBoard.put(move.getPromotionTo(), move.getNewPosition());
        }
        newState.getMoveList().add(move);
        newState.updateCastlingStatusesFromLastMove();
        newState.setTurn(newState.getTurn() == PlayerColour.WHITE ? PlayerColour.BLACK : PlayerColour.WHITE);
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

    public ColouredPiece getPieceAtPosition(BoardPosition position) {
        for (Map.Entry<ColouredPiece, BoardPosition> entry : board.entrySet()) {
            if (entry.getValue().equals(position)) {
                return entry.getKey();
            }
        }
        // No piece at that position: return null
        return null;
    }

    private static HashMap<ColouredPiece, BoardPosition> generateStartBoard() {
        HashMap<ColouredPiece, BoardPosition> board = new HashMap<>();

        try {
            // Pawns
            for (int column = 0; column < 8; column++) {
                board.put(new ColouredPiece(Piece.PAWN, PlayerColour.WHITE), new BoardPosition(column, 1));
                board.put(new ColouredPiece(Piece.PAWN, PlayerColour.BLACK), new BoardPosition(column, 6));
            }

            // Kings
            board.put(new ColouredPiece(Piece.KING, PlayerColour.WHITE), new BoardPosition(4, 0));
            board.put(new ColouredPiece(Piece.KING, PlayerColour.BLACK), new BoardPosition(4, 7));

            // Queens
            board.put(new ColouredPiece(Piece.QUEEN, PlayerColour.WHITE), new BoardPosition(3, 0));
            board.put(new ColouredPiece(Piece.QUEEN, PlayerColour.BLACK), new BoardPosition(3, 7));

            // Bishops
            board.put(new ColouredPiece(Piece.BISHOP, PlayerColour.WHITE), new BoardPosition(2, 0));
            board.put(new ColouredPiece(Piece.BISHOP, PlayerColour.BLACK), new BoardPosition(2, 7));
            board.put(new ColouredPiece(Piece.BISHOP, PlayerColour.WHITE), new BoardPosition(5, 0));
            board.put(new ColouredPiece(Piece.BISHOP, PlayerColour.BLACK), new BoardPosition(5, 7));

            // Knights
            board.put(new ColouredPiece(Piece.KNIGHT, PlayerColour.WHITE), new BoardPosition(1, 0));
            board.put(new ColouredPiece(Piece.KNIGHT, PlayerColour.BLACK), new BoardPosition(1, 7));
            board.put(new ColouredPiece(Piece.KNIGHT, PlayerColour.WHITE), new BoardPosition(6, 0));
            board.put(new ColouredPiece(Piece.KNIGHT, PlayerColour.BLACK), new BoardPosition(6, 7));

            // Rooks
            board.put(new ColouredPiece(Piece.ROOK, PlayerColour.WHITE), new BoardPosition(0, 0));
            board.put(new ColouredPiece(Piece.ROOK, PlayerColour.BLACK), new BoardPosition(0, 7));
            board.put(new ColouredPiece(Piece.ROOK, PlayerColour.WHITE), new BoardPosition(7, 0));
            board.put(new ColouredPiece(Piece.ROOK, PlayerColour.BLACK), new BoardPosition(7, 7));
        } catch (InvalidBoardPositionException e) {
            // This should never happen
            return null;
        }
        return board;
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
