package chessboard;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private Board startState;
    private ArrayList<Move> moveList;
    private ArrayList<Move> allLegalMoves;

    private int whiteCastlingStatus;
    private int blackCastlingStatus;


    public State(Board board, PlayerColour turn, ArrayList<Move> moveList) {
        this.board = board.clone();
        this.startState = board.clone();
        this.turn = turn;
        this.moveList = (ArrayList<Move>) moveList.clone();
        updateCastlingStatuses();
        updateLegalMoves();
        updateGameStatus();
    }

    // Game start state
    public State() {
        board = new Board();
        startState = new Board();
        turn = PlayerColour.WHITE;
        moveList = new ArrayList<>();
        whiteCastlingStatus = EITHER_CASTLE;
        blackCastlingStatus = EITHER_CASTLE;
        updateLegalMoves();
        gameStatus = IN_PROGRESS;
    }

    // Called by clone method so executeMove doesn't take forever by having to call updateCastlingStatus(). No need to
    // evaluate status since we're cloning a legal state.
    private State(Board board, Board startState, PlayerColour turn, ArrayList<Move> moveList,
                 int whiteCastlingStatus, int blackCastlingStatus, int gameStatus, ArrayList<Move> allLegalMoves) {
        this.board = board.clone();
        this.startState = startState.clone();
        this.turn = turn;
        this.moveList = (ArrayList<Move>) moveList.clone();
        this.whiteCastlingStatus = whiteCastlingStatus;
        this.blackCastlingStatus = blackCastlingStatus;
        this.gameStatus = gameStatus;
        this.allLegalMoves = (ArrayList<Move>) allLegalMoves.clone();
    }

    // Execute a move on a board and returns the new board state. Doesn't modify the original board
    public Board executeMoveOnBoard(Board board, Move move) {
        Board newBoard = board.clone();
        if (move.getSpecialMove() == "") {
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
        return newBoard;
    }

    public State executeMove(Move move) {
        State newState = this.clone();
        newState.setBoard(executeMoveOnBoard(board, move));
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
            if (isKingInCheck(board, turn)) {
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

        // Threefold repetition
        // Generate two arrayLists: consisting of all the board states throughout the game, for each player's turn
        ArrayList<Board> allBoardStates1 = new ArrayList<>();
        ArrayList<Board> allBoardStates2 = new ArrayList<>();

        Board tempBoard = startState;
        allBoardStates1.add(tempBoard);
        for (int i = 0; i < moveList.size(); i ++) {
            tempBoard = executeMoveOnBoard(tempBoard, moveList.get(i));
            if (i % 2 == 0) {
                allBoardStates2.add(tempBoard);
            } else {
                allBoardStates1.add(tempBoard);
            }
        }

        HashMap<Board, Integer> allBoardCount1 = (HashMap<Board, Integer>) allBoardStates1.stream().collect(Collectors.toMap(Function.identity(), b -> 1));
        HashMap<Board, Integer> allBoardCount2 = (HashMap<Board, Integer>) allBoardStates2.stream().collect(Collectors.toMap(Function.identity(), b -> 1));

        for (Board board1: allBoardStates1) {
            for (Board board2: allBoardStates1) {
                if (board2 != board1 && board1.sameBoard(board2)) {
                    allBoardCount1.put(board1, allBoardCount1.get(board1) + 1);
                }
            }

            if (allBoardCount1.get(board1) >= 3) {
                gameStatus = DRAW;
            }
        }

        for (Board board1: allBoardStates2) {
            for (Board board2: allBoardStates2) {
                if (board2 != board1 && board1.sameBoard(board2)) {
                    allBoardCount2.put(board1, allBoardCount2.get(board1) + 1);
                }
            }

            if (allBoardCount2.get(board1) >= 3) {
                gameStatus = DRAW;
            }
        }

        // TODO 50 move rule

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

    // Generates pseudo legal moves then verifies if the king is in check. To be faster, could just generate legal
    // moves (though this problem is tricky)
    private void updateLegalMoves() {
        allLegalMoves = new ArrayList<>();

        // Regular moves
        for (ColouredPiece piece: board.getPieces(turn)) {
            BoardPosition oldPosition = board.getPiecesPosition(piece);
            String oldPositionCoordinate = oldPosition.getStringPosition();
            for (BoardPosition newPosition: getValidMovePositions(board, piece)) {
                boolean taking = board.getPieceAtPosition(newPosition) != null;
                allLegalMoves.add(new Move(piece, oldPosition, newPosition, oldPositionCoordinate, taking));
            }
        }

        // Castling
        // Can only castle while not in check
        if (!isKingInCheck(board, turn)) {
            // Determine all the squares attacked by opposing pieces to check if we can castle.
            ArrayList<BoardPosition> allAttackedSquares = new ArrayList<>();
            for (ColouredPiece piece : board.getPieces(PlayerColour.getOtherColour(turn))) {
                for (BoardPosition position: getAttackedSquares(board, piece)) {
                    if (!allAttackedSquares.contains(position)) {
                        allAttackedSquares.add(position);
                    }
                }
            }

            BoardPosition castlingSquareOne;
            BoardPosition castlingSquareTwo;
            BoardPosition castlingSquareThree;
            BoardPosition oldKingPosition;
            BoardPosition newKingPosition;
            BoardPosition oldRookPosition;
            BoardPosition newRookPosition;
            try {
                if (turn.equals(PlayerColour.WHITE)) {
                    // Kingside
                    if (whiteCastlingStatus == EITHER_CASTLE || whiteCastlingStatus == KINGSIDE_CASTLE) {
                        castlingSquareOne = new BoardPosition(5, 0);
                        castlingSquareTwo = new BoardPosition(6, 0);
                        oldKingPosition = new BoardPosition(4, 0);
                        newKingPosition = new BoardPosition(6, 0);
                        oldRookPosition = new BoardPosition(7, 0);
                        newRookPosition = new BoardPosition(5, 0);
                        Move castle = verifyAndMakeCastleMove(new ArrayList<>(Arrays.asList(castlingSquareOne,
                                castlingSquareTwo)), board, allAttackedSquares, oldKingPosition, newKingPosition,
                                oldRookPosition, newRookPosition);
                        if (castle != null) {
                            allLegalMoves.add(castle);
                        }
                    }
                    // Queenside
                    if (whiteCastlingStatus == EITHER_CASTLE || whiteCastlingStatus == QUEENSIDE_CASTLE) {
                        castlingSquareOne = new BoardPosition(1, 0);
                        castlingSquareTwo = new BoardPosition(2, 0);
                        castlingSquareThree = new BoardPosition(3, 0);
                        oldKingPosition = new BoardPosition(4, 0);
                        newKingPosition = new BoardPosition(2, 0);
                        oldRookPosition = new BoardPosition(0, 0);
                        newRookPosition = new BoardPosition(3, 0);
                        Move castle = verifyAndMakeCastleMove(new ArrayList<>(Arrays.asList(castlingSquareOne,
                                castlingSquareTwo, castlingSquareThree)), board, allAttackedSquares, oldKingPosition,
                                newKingPosition, oldRookPosition, newRookPosition);
                        if (castle != null) {
                            allLegalMoves.add(castle);
                        }
                    }
                } else {
                    // Black
                    // Kingside
                    if (blackCastlingStatus == EITHER_CASTLE || blackCastlingStatus == KINGSIDE_CASTLE) {
                        castlingSquareOne = new BoardPosition(5, 7);
                        castlingSquareTwo = new BoardPosition(6, 7);
                        oldKingPosition = new BoardPosition(4, 7);
                        newKingPosition = new BoardPosition(6, 7);
                        oldRookPosition = new BoardPosition(7, 7);
                        newRookPosition = new BoardPosition(5, 7);
                        Move castle = verifyAndMakeCastleMove(new ArrayList<>(Arrays.asList(castlingSquareOne,
                                castlingSquareTwo)), board, allAttackedSquares, oldKingPosition, newKingPosition,
                                oldRookPosition, newRookPosition);
                        if (castle != null) {
                            allLegalMoves.add(castle);
                        }
                    }
                    // Queenside
                    if (blackCastlingStatus == EITHER_CASTLE || blackCastlingStatus == QUEENSIDE_CASTLE) {
                        castlingSquareOne = new BoardPosition(1, 7);
                        castlingSquareTwo = new BoardPosition(2, 7);
                        castlingSquareThree = new BoardPosition(3, 7);
                        oldKingPosition = new BoardPosition(4, 7);
                        newKingPosition = new BoardPosition(2, 7);
                        oldRookPosition = new BoardPosition(0, 7);
                        newRookPosition = new BoardPosition(3, 7);
                        Move castle = verifyAndMakeCastleMove(new ArrayList<>(Arrays.asList(castlingSquareOne,
                                castlingSquareTwo, castlingSquareThree)), board, allAttackedSquares, oldKingPosition,
                                newKingPosition, oldRookPosition, newRookPosition);
                        if (castle != null) {
                            allLegalMoves.add(castle);
                        }
                    }
                }
            } catch (InvalidBoardPositionException e) {
                // This should never happen
                System.out.println("Something's wrong");
            }
        }

        // Promotion
        for (Move move: allLegalMoves) {
            if (move.getColouredPiece().getPiece().equals(Piece.PAWN)
                    && (move.getNewPosition().getRow() == 0 || move.getNewPosition().getRow() == 7)) {
                // Remove this invalid move from the list and replace it with promotions
                allLegalMoves.remove(move);
                ArrayList<Piece> validPromotes = new ArrayList<>(Arrays.asList(Piece.BISHOP, Piece.KNIGHT, Piece.QUEEN, Piece.ROOK));
                for (Piece newPiece: validPromotes) {
                    try {
                        allLegalMoves.add(new Move("Promoting", move.getColouredPiece(), move.getNewPosition(),
                                move.getOldPositionCoordinate(), move.isTaking(), new ColouredPiece(newPiece, turn)));
                    } catch (InvalidMoveException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // En passant
        if (moveList.size() > 0) {
            Move lastMove = moveList.get(moveList.size() - 1);
            if (turn.equals(PlayerColour.WHITE)) {
                if (lastMove.getColouredPiece().getPiece().equals(Piece.PAWN) && lastMove.getOldPosition().getRow() == 6
                        && lastMove.getNewPosition().getRow() == 4) {
                    // We can possibly en passant
                    ArrayList<BoardPosition> enPassantPositions = new ArrayList<>();
                    try {
                        enPassantPositions.add(new BoardPosition(lastMove.getOldPosition().getColumn() + 1, 4));
                    } catch (InvalidBoardPositionException e) {
                        // This is fine. It means the pawn to be en passant-ed is on the edge of the board
                    }
                    try {
                        enPassantPositions.add(new BoardPosition(lastMove.getOldPosition().getColumn() - 1, 4));
                    } catch (InvalidBoardPositionException e) {
                        // This is fine. It means the pawn to be en passant-ed is on the edge of the board
                    }
                    for (BoardPosition enPassantPosition : enPassantPositions) {
                        ColouredPiece enPassantPawn = board.getPieceAtPosition(enPassantPosition);
                        if (enPassantPawn != null && Board.comparePieces(enPassantPawn,
                                new ColouredPiece(Piece.PAWN, PlayerColour.WHITE))) {
                            try {
                                BoardPosition newPosition = new BoardPosition(lastMove.getOldPosition().getColumn(), 5);
                                String oldPositionCoordinate = enPassantPosition.getStringPosition();
                                allLegalMoves.add(new Move("En passant", enPassantPawn, newPosition,
                                        oldPositionCoordinate, lastMove.getNewPosition()));
                            } catch (InvalidBoardPositionException e) {
                                // This should never happen
                                System.out.println("Something's wrong");
                            } catch (InvalidMoveException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else {
                // Black
                if (lastMove.getColouredPiece().getPiece().equals(Piece.PAWN) && lastMove.getOldPosition().getRow() == 1
                        && lastMove.getNewPosition().getRow() == 3) {
                    // We can possibly en passant
                    ArrayList<BoardPosition> enPassantPositions = new ArrayList<>();
                    try {
                        enPassantPositions.add(new BoardPosition(lastMove.getOldPosition().getColumn() + 1, 3));
                    } catch (InvalidBoardPositionException e) {
                        // This is fine. It means the pawn to be en passant-ed is on the edge of the board
                    }
                    try {
                        enPassantPositions.add(new BoardPosition(lastMove.getOldPosition().getColumn() - 1, 3));
                    } catch (InvalidBoardPositionException e) {
                        // This is fine. It means the pawn to be en passant-ed is on the edge of the board
                    }
                    for (BoardPosition enPassantPosition : enPassantPositions) {
                        ColouredPiece enPassantPawn = board.getPieceAtPosition(enPassantPosition);
                        if (enPassantPawn != null && Board.comparePieces(enPassantPawn,
                                new ColouredPiece(Piece.PAWN, PlayerColour.BLACK))) {
                            try {
                                BoardPosition newPosition = new BoardPosition(lastMove.getOldPosition().getColumn(), 2);
                                String oldPositionCoordinate = enPassantPosition.getStringPosition();
                                allLegalMoves.add(new Move("En passant", enPassantPawn, newPosition,
                                        oldPositionCoordinate, lastMove.getNewPosition()));
                            } catch (InvalidBoardPositionException e) {
                                // This should never happen
                                System.out.println("Something's wrong");
                            } catch (InvalidMoveException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }


        // Verify each move is legal
        allLegalMoves = (ArrayList<Move>) allLegalMoves.stream().filter(m -> !isKingInCheck(executeMoveOnBoard(board, m), turn))
                .collect(Collectors.toList());
    }

    // Helper function to determine if the squares between the rook and king while castling are empty and not attacked.
    // If the castle is valid, create the corresponding move object. A complete array of the attacked squares has
    // already been generated for us
    private static Move verifyAndMakeCastleMove(ArrayList<BoardPosition> castlingSquares, Board board,
                                                ArrayList<BoardPosition> attackedSquares,
                                                BoardPosition oldKingPosition, BoardPosition newKingPosition,
                                                BoardPosition oldRookPosition, BoardPosition newRookPosition) {
        // Verify the castle is valid
        for (BoardPosition position: castlingSquares) {
            if (board.getPieceAtPosition(position) != null || attackedSquares.contains(position)) {
                return null;
            }
        }

        // Generate the move
        ColouredPiece king = board.getPieceAtPosition(oldKingPosition);
        ColouredPiece rook = board.getPieceAtPosition(oldRookPosition);
        try {
            return new Move("Castling", king, newKingPosition, rook, newRookPosition);
        } catch (InvalidMoveException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Will return if colour's king is in check
    private static boolean isKingInCheck(Board board, PlayerColour colour) {
        for (ColouredPiece piece: board.getPieces(PlayerColour.getOtherColour(colour))) {
            for (ColouredPiece attackedPiece: getAttackedPieces(board, piece)) {
                if (attackedPiece.getPiece().equals(Piece.KING)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Will return the list of pieces attacked by this piece. Doesn't include enpessant for pawns.
    private static ArrayList<ColouredPiece> getAttackedPieces(Board board, ColouredPiece piece) {
        ArrayList<ColouredPiece> attackedPieces = new ArrayList<>();
        for (ArrayList<RelativeBoardPosition> attackRoute : piece.getAttackRoutes()) {
            for (RelativeBoardPosition relativePosition: attackRoute) {
                try {
                    BoardPosition actualPosition = board.getPiecesPosition(piece).addRelativePosition(relativePosition);
                    ColouredPiece attackedPiece = board.getPieceAtPosition(actualPosition);
                    if (attackedPiece != null) {
                        // Once one position has a piece in it, the remainder of the positions in the attackroute are
                        // not attacked
                        if (!attackedPiece.getColour().equals(piece.getColour())) {
                            attackedPieces.add(attackedPiece);
                        }
                        break;
                    }
                } catch (InvalidBoardPositionException e) {
                    // The attack route is off the board. No need to check the rest
                    break;
                }
            }
        }
        return attackedPieces;
    }

    // Will return the list of squares attacked by this piece. Used to check castling and useful in the future for
    // non-pseudo move generation
    private static ArrayList<BoardPosition> getAttackedSquares(Board board, ColouredPiece piece) {
        ArrayList<BoardPosition> attackedSquares = new ArrayList<>();
        for (ArrayList<RelativeBoardPosition> attackRoute : piece.getAttackRoutes()) {
            for (RelativeBoardPosition relativePosition: attackRoute) {
                try {
                    BoardPosition actualPosition = board.getPiecesPosition(piece).addRelativePosition(relativePosition);
                    ColouredPiece attackedPiece = board.getPieceAtPosition(actualPosition);
                    if (attackedPiece != null) {
                        // Once one position has a piece in it, the remainder of the positions in the attackroute are
                        // not attacked
                        if (!attackedPiece.getColour().equals(piece.getColour())) {
                            attackedSquares.add(actualPosition);
                        }
                        break;
                    }

                    attackedSquares.add(actualPosition);

                } catch (InvalidBoardPositionException e) {
                    // The attack route is off the board. No need to check the rest
                    break;
                }
            }
        }
        return attackedSquares;
    }

    // Will return any squares a piece can move to, including takes. Doesn't consider whether this will place it's own
    // king in check. Doesn't include enpessant for pawns, but does include forward moves. Doesn't including castling.
    // Does including moving pawns to the last rank.
    private static ArrayList<BoardPosition> getValidMovePositions(Board board, ColouredPiece piece) {
        ArrayList<BoardPosition> movePositions = new ArrayList<>();
        // Need to manually do pawn movements cuz pawns are dumb
        if (piece.getPiece().equals(Piece.PAWN)) {
            BoardPosition forwardOne = null;
            BoardPosition forwardTwo = null;
            try {
                if (piece.getColour().equals(PlayerColour.WHITE)) {
                    forwardOne = board.getPiecesPosition(piece).addRelativePosition(new RelativeBoardPosition(0, 1));
                    if (board.getPiecesPosition(piece).getRow() == 1) {
                        forwardTwo = board.getPiecesPosition(piece).addRelativePosition(new RelativeBoardPosition(0, 2));
                    }
                } else {
                    // Black
                    forwardOne = board.getPiecesPosition(piece).addRelativePosition(new RelativeBoardPosition(0, -1));
                    if (board.getPiecesPosition(piece).getRow() == 6) {
                        forwardTwo = board.getPiecesPosition(piece).addRelativePosition(new RelativeBoardPosition(0, -2));
                    }
                }
            } catch (InvalidBoardPositionException e) {
                // This should never happen
                System.out.println("Something's wrong!");
                return null;
            }
            if (board.getPieceAtPosition(forwardOne) == null) {
                movePositions.add(forwardOne);
                if (forwardTwo != null && board.getPieceAtPosition(forwardTwo) == null) {
                    movePositions.add(forwardTwo);
                }
            }
        } else {
            // Valid move positions are the same as attacked squares except for pawns
            movePositions = getAttackedSquares(board, piece);
        }
        return movePositions;
    }

    private void changeTurn() {
        turn = PlayerColour.getOtherColour(turn);
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
        return new State(board, startState, turn, moveList, whiteCastlingStatus, blackCastlingStatus, gameStatus, allLegalMoves);
    }

    // Should only be used by executeMove
    private void setBoard(Board board) {
        this.board = board;
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
