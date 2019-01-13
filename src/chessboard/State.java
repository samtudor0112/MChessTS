package chessboard;

import java.util.*;

/**
 * Stores the current state of the match, including the board state, who's turn
 * it is, and whether the game has ended
 */
public class State {
    public static final int IN_PROGRESS = 0;
    public static final int WHITE_WIN = 1;
    public static final int BLACK_WIN = 2;
    public static final int DRAW = 3;

    public static final int EITHER_CASTLE = 0;
    public static final int KINGSIDE_CASTLE = 1;
    public static final int QUEENSIDE_CASTLE = 2;
    public static final int NO_CASTLE = 3;

    private Board board;
    private PlayerColour turn;
    private int gameStatus;
    private ArrayList<Move> moveList;
    private ArrayList<Move> allLegalMoves;

    private int whiteCastlingStatus;
    private int blackCastlingStatus;


    public State(Board board, PlayerColour turn, ArrayList<Move> moveList) {
        this.board = board.clone();
        this.turn = turn;
        this.moveList = moveList;
        updateCastlingStatuses();
        updateLegalMoves();
        updateGameStatus();
    }

    // Game start state
    public State() {
        board = new Board();
        turn = PlayerColour.WHITE;
        moveList = new ArrayList<>();
        whiteCastlingStatus = EITHER_CASTLE;
        blackCastlingStatus = EITHER_CASTLE;
        updateLegalMoves();
        gameStatus = IN_PROGRESS;
    }

    // Called by clone method so executeMove doesn't take forever by having to call updateCastlingStatus(). No need to
    // evaluate status since we're cloning a legal state.
    private State(Board board, PlayerColour turn, ArrayList<Move> moveList,
                 int whiteCastlingStatus, int blackCastlingStatus, int gameStatus, ArrayList<Move> allLegalMoves) {
        this.board = board.clone();
        this.turn = turn;
        this.moveList = moveList;
        this.whiteCastlingStatus = whiteCastlingStatus;
        this.blackCastlingStatus = blackCastlingStatus;
        this.gameStatus = gameStatus;
        this.allLegalMoves = allLegalMoves;
    }

    public State executeMove(Move move) {
        State newState = this.clone();
        Board newBoard = newState.getBoard();
        if (move.getSpecialMove() == null) {
            newBoard.moveAndTakePiece(move.getColouredPiece(), move.getNewPosition());
        } else if (move.getSpecialMove().equals("En passant")) {
            newBoard.moveAndTakePiece(move.getColouredPiece(), move.getTakePosition());
            newBoard.moveAndTakePiece(move.getColouredPiece(), move.getNewPosition());
        } else if (move.getSpecialMove().equals("Castling")) {
            newBoard.moveAndTakePiece(move.getColouredPiece(), move.getNewPosition());
            newBoard.moveAndTakePiece(move.getCastlingPiece(), move.getCastlingPosition());
        } else {
            // promotion
            newBoard.moveAndTakePiece(move.getColouredPiece(), move.getNewPosition());
            newBoard.replacePieceAtPosition(move.getPromotionTo(), move.getNewPosition());
        }
        newState.getMoveList().add(move);
        newState.changeTurn();
        newState.updateCastlingStatusesFromLastMove();
        newState.updateLegalMoves();
        newState.updateGameStatus();
        return newState;
    }

    public ArrayList<Move> getAllLegalMoves() {
        return new ArrayList<>(Collections.unmodifiableList(allLegalMoves));
    }

    // Determines whether the player who's turn it is has lost or drawn the game. Note: you can never win as it becomes
    // your turn.
    private void updateGameStatus() {
        // Determine whether a checkmate or stalemate has occurred

        if (allLegalMoves.size() == 0) {
            // Determine whether the king is in check or not
            if (true/* TODO IS KING IN CHECK */) {
                if (turn == PlayerColour.WHITE) {
                    gameStatus = BLACK_WIN;
                } else {
                    // Black
                    gameStatus = WHITE_WIN;
                }
            } else {
                gameStatus = DRAW;
            }
        }

        // Determine if both sides have invalid material
        // King vs King
        HashSet<ColouredPiece> kingKing = new HashSet<>(Arrays.asList(
                new ColouredPiece(Piece.KING, PlayerColour.WHITE),
                new ColouredPiece(Piece.KING, PlayerColour.BLACK)));
        if (board.compareBoardWithPieceList(kingKing)) {
            gameStatus = DRAW;
        }

        for (int i = 0; i < 2; i++) {
            // 0 = white, 1 = black
            // King vs King Bishop
            HashSet<ColouredPiece> kingKingBishop = new HashSet<>(Arrays.asList(
                    new ColouredPiece(Piece.KING, PlayerColour.WHITE),
                    new ColouredPiece(Piece.KING, PlayerColour.BLACK),
                    new ColouredPiece(Piece.BISHOP, PlayerColour.values()[i])));
            if (board.compareBoardWithPieceList(kingKingBishop)) {
                gameStatus = DRAW;
            }

            // King vs King Knight
            HashSet<ColouredPiece> kingKingKnight = new HashSet<>(Arrays.asList(
                    new ColouredPiece(Piece.KING, PlayerColour.WHITE),
                    new ColouredPiece(Piece.KING, PlayerColour.BLACK),
                    new ColouredPiece(Piece.KNIGHT, PlayerColour.values()[i])));
            if (board.compareBoardWithPieceList(kingKingKnight)) {
                gameStatus = DRAW;
            }
        }

        // King Bishop vs King Bishop (with Bishops on the same colour)
        HashSet<ColouredPiece> kingKingBishopBishop = new HashSet<>(Arrays.asList(
                new ColouredPiece(Piece.KING, PlayerColour.WHITE),
                new ColouredPiece(Piece.KING, PlayerColour.BLACK),
                new ColouredPiece(Piece.BISHOP, PlayerColour.WHITE),
                new ColouredPiece(Piece.BISHOP, PlayerColour.BLACK)));
        if (board.compareBoardWithPieceList(kingKingBishopBishop)) {
            // Verify that both the bishops are on the same coloured squares
            Set<ColouredPiece> pieces = board.getPieces();
            ColouredPiece whiteBishop = null;
            ColouredPiece blackBishop = null;
            for (ColouredPiece piece : pieces) {
                if (piece.getPiece().equals(Piece.BISHOP)) {
                    if (piece.getColour().equals(PlayerColour.WHITE)) {
                        whiteBishop = piece;
                    } else {
                        blackBishop = piece;
                    }
                }
            }

            if (BoardPosition.lightSquares.contains(board.getPiecesPosition(whiteBishop))) {
                if (BoardPosition.lightSquares.contains(board.getPiecesPosition(blackBishop))) {
                    gameStatus = DRAW;
                }
            } else {
                // White bishop is on dark squares
                if (BoardPosition.darkSquares.contains(board.getPiecesPosition(blackBishop))) {
                    gameStatus = DRAW;
                }
            }
        }

        // TODO Threefold repetition and 50 move rule

    }

    // Since this method is called every time a move is executed, we only need to check the very last move to ensure
    // the castling statuses are correct.
    private void updateCastlingStatusesFromLastMove() {
        updateCastlingStatusFromSingleMove(moveList.get(moveList.size() - 1));

    }

    private void updateCastlingStatusFromSingleMove(Move move) {
        try {
            // The only way a player can no longer castle is by moving their rook, moving their king or by having their
            // rook taken.
            if (move.getColouredPiece().getColour() == PlayerColour.WHITE) {
                if (whiteCastlingStatus == NO_CASTLE) {
                    // Can't ever castle if you couldn't previously
                    return;
                }

                if (move.getSpecialMove().equals("Castling")) {
                    // Can't castle again once you've castled
                    whiteCastlingStatus = NO_CASTLE;
                    return;
                }

                if (move.getColouredPiece().getPiece() == Piece.KING) {
                    // Can't castle if you move the king
                    whiteCastlingStatus = NO_CASTLE;
                    return;
                }

                if (move.getColouredPiece().getPiece() == Piece.ROOK) {
                    // Can't kingside castle if you move the rook on a8
                    if (move.getOldPosition() == new BoardPosition(0, 7)) {
                        if (whiteCastlingStatus == KINGSIDE_CASTLE) {
                            whiteCastlingStatus = NO_CASTLE;
                            return;
                        } else {
                            whiteCastlingStatus = QUEENSIDE_CASTLE;
                            return;
                        }
                    }
                    // Can't queenside castle if you move the rook on a1
                    if (move.getOldPosition() == new BoardPosition(0, 0)) {
                        if (whiteCastlingStatus == QUEENSIDE_CASTLE) {
                            whiteCastlingStatus = NO_CASTLE;
                            return;
                        } else {
                            whiteCastlingStatus = KINGSIDE_CASTLE;
                            return;
                        }
                    }
                }

                // Opposition can't kingside castle if you take their rook on h8
                if (move.getTakePosition() == new BoardPosition(7, 7)) {
                    if (blackCastlingStatus == KINGSIDE_CASTLE) {
                        blackCastlingStatus = NO_CASTLE;
                        return;
                    } else {
                        blackCastlingStatus = QUEENSIDE_CASTLE;
                        return;
                    }
                }

                // Opposition can't queenside castle if you take their rook on h1
                if (move.getTakePosition() == new BoardPosition(7, 0)) {
                    if (blackCastlingStatus == QUEENSIDE_CASTLE) {
                        blackCastlingStatus = NO_CASTLE;
                        return;
                    } else {
                        blackCastlingStatus = KINGSIDE_CASTLE;
                        return;
                    }
                }

            } else {
                // black
                if (blackCastlingStatus == NO_CASTLE) {
                    // Can't ever castle if you couldn't previously
                    return;
                }

                if (move.getSpecialMove().equals("Castling")) {
                    // Can't castle again once you've castled
                    blackCastlingStatus = NO_CASTLE;
                    return;
                }

                if (move.getColouredPiece().getPiece() == Piece.KING) {
                    // Can't castle if you move the king
                    blackCastlingStatus = NO_CASTLE;
                    return;
                }

                if (move.getColouredPiece().getPiece() == Piece.ROOK) {
                    // Can't kingside castle if you move the rook on h8
                    if (move.getOldPosition() == new BoardPosition(7, 7)) {
                        if (blackCastlingStatus == KINGSIDE_CASTLE) {
                            blackCastlingStatus = NO_CASTLE;
                            return;
                        } else {
                            blackCastlingStatus = QUEENSIDE_CASTLE;
                            return;
                        }
                    }
                    // Can't queenside castle if you move the rook on h1
                    if (move.getOldPosition() == new BoardPosition(7, 0)) {
                        if (blackCastlingStatus == QUEENSIDE_CASTLE) {
                            blackCastlingStatus = NO_CASTLE;
                            return;
                        } else {
                            blackCastlingStatus = KINGSIDE_CASTLE;
                            return;
                        }
                    }
                }

                // Opposition can't kingside castle if you take their rook on a8
                if (move.getTakePosition() == new BoardPosition(0, 7)) {
                    if (whiteCastlingStatus == KINGSIDE_CASTLE) {
                        whiteCastlingStatus = NO_CASTLE;
                        return;
                    } else {
                        whiteCastlingStatus = QUEENSIDE_CASTLE;
                        return;
                    }
                }

                // Opposition can't queenside castle if you take their rook on a1
                if (move.getTakePosition() == new BoardPosition(0, 0)) {
                    if (whiteCastlingStatus == QUEENSIDE_CASTLE) {
                        whiteCastlingStatus = NO_CASTLE;
                        return;
                    } else {
                        whiteCastlingStatus = KINGSIDE_CASTLE;
                        return;
                    }
                }
            }
        } catch (InvalidBoardPositionException e) {
            // this should never happen
            System.out.println("Something's wrong!");
        }
    }

    // Checking from scratch is neccessary if a game is imported. No need for speed here.
    private void updateCastlingStatuses() {
        moveList.forEach(this::updateCastlingStatusFromSingleMove);
    }

    private void updateLegalMoves() {
        // TODO
    }

    private void changeTurn() {
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
        return new State(board, turn, moveList, whiteCastlingStatus, blackCastlingStatus, gameStatus, allLegalMoves);
    }

    public Move getMoveFromString(String stringMove) throws InvalidMoveException {
        Move move = null;
        if (turn == PlayerColour.WHITE) {
            // TODO
        } else {
            // Black
            // TODO
        }
        if (getAllLegalMoves().contains(move)) {
            return move;
        } else {
            throw new InvalidMoveException("Invalid move!");
        }
    }
}
