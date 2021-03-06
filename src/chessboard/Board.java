package chessboard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the board at any time, containing all the pieces of both colours
 */
public class Board {
    private HashMap<BoardPosition, ColouredPiece> boardMap;

    public Board(HashMap<BoardPosition, ColouredPiece> boardMap) {
        this.boardMap = (HashMap<BoardPosition, ColouredPiece>) boardMap.clone();
    }

    public Board() {
        this.boardMap = new HashMap<>();

        try {
            // Pawns
            for (int column = 0; column < 8; column++) {
                boardMap.put(new BoardPosition(column, 1), new ColouredPiece(Piece.PAWN, PlayerColour.WHITE));
                boardMap.put(new BoardPosition(column, 6), new ColouredPiece(Piece.PAWN, PlayerColour.BLACK));
            }

            // Kings
            boardMap.put(new BoardPosition(4, 0), new ColouredPiece(Piece.KING, PlayerColour.WHITE));
            boardMap.put(new BoardPosition(4, 7), new ColouredPiece(Piece.KING, PlayerColour.BLACK));

            // Queens
            boardMap.put(new BoardPosition(3, 0), new ColouredPiece(Piece.QUEEN, PlayerColour.WHITE));
            boardMap.put(new BoardPosition(3, 7), new ColouredPiece(Piece.QUEEN, PlayerColour.BLACK));

            // Bishops
            boardMap.put(new BoardPosition(2, 0), new ColouredPiece(Piece.BISHOP, PlayerColour.WHITE));
            boardMap.put(new BoardPosition(2, 7), new ColouredPiece(Piece.BISHOP, PlayerColour.BLACK));
            boardMap.put(new BoardPosition(5, 0), new ColouredPiece(Piece.BISHOP, PlayerColour.WHITE));
            boardMap.put(new BoardPosition(5, 7), new ColouredPiece(Piece.BISHOP, PlayerColour.BLACK));

            // Knights
            boardMap.put(new BoardPosition(1, 0), new ColouredPiece(Piece.KNIGHT, PlayerColour.WHITE));
            boardMap.put(new BoardPosition(1, 7), new ColouredPiece(Piece.KNIGHT, PlayerColour.BLACK));
            boardMap.put(new BoardPosition(6, 0), new ColouredPiece(Piece.KNIGHT, PlayerColour.WHITE));
            boardMap.put(new BoardPosition(6, 7), new ColouredPiece(Piece.KNIGHT, PlayerColour.BLACK));

            // Rooks
            boardMap.put(new BoardPosition(0, 0), new ColouredPiece(Piece.ROOK, PlayerColour.WHITE));
            boardMap.put(new BoardPosition(0, 7), new ColouredPiece(Piece.ROOK, PlayerColour.BLACK));
            boardMap.put(new BoardPosition(7, 0), new ColouredPiece(Piece.ROOK, PlayerColour.WHITE));
            boardMap.put(new BoardPosition(7, 7), new ColouredPiece(Piece.ROOK, PlayerColour.BLACK));
        } catch (InvalidBoardPositionException e) {
            // This should never happen
            System.out.println("Something's wrong!");
            return;
        }
    }

    // returns null if there is no piece at that position, otherwise the piece
    public ColouredPiece getPieceAtPosition(BoardPosition position) {
        return boardMap.get(position);
    }

    // returns null if that piece is not on the board, otherwise the position
    public BoardPosition getPiecesPosition(ColouredPiece piece) {
        for (Map.Entry<BoardPosition, ColouredPiece> entry : boardMap.entrySet()) {
            if (entry.getValue().equals(piece)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void moveAndTakePiece(ColouredPiece piece, BoardPosition newPosition) {
        boardMap.remove(newPosition);
        boardMap.remove(getPiecesPosition(piece));
        boardMap.put(newPosition, piece);
    }

    public void replacePieceAtPosition(ColouredPiece piece, BoardPosition position) {
        boardMap.remove(position);
        boardMap.put(position, piece);
    }

    public Set<ColouredPiece> getPieces() {
        return new HashSet<>(boardMap.values());
    }

    // Returns all pieces of that colour
    public Set<ColouredPiece> getPieces(PlayerColour colour) {
        Set<ColouredPiece> pieces = getPieces().stream().filter(p -> p.getColour().equals(colour)).collect(Collectors
                .toSet());
        return pieces;
    }

    public Board clone() {
        return new Board(boardMap);
    }

    // Returns true if this board contains exactly the set of pieces pieces. Note by exactly I mean each piece is the
    // same according to comparePieces.
    public boolean compareBoardWithPieceList(Set<ColouredPiece> pieces) {
        Set<ColouredPiece> boardPieces = getPieces();
        Set<ColouredPiece> otherPieces = new HashSet<>(pieces);
        if (boardPieces.size() != pieces.size()) {
            return false;
        }
        for (ColouredPiece piece : boardPieces) {
            boolean pieceFound = false;
            // Check if there is a ColouredPiece with the same piece and colour
            // in otherPieces, then remove it if there is
            for (ColouredPiece otherPiece : otherPieces) {
                if (comparePieces(piece, otherPiece)) {
                    otherPieces.remove(otherPiece);
                    pieceFound = true;
                    break;
                }
            }
            if (!pieceFound) {
                return false;
            }
        }
        return true;
    }

    // Returns true if two pieces are the same colour and type (not the same instance). If either is null, returns false
    public static boolean comparePieces(ColouredPiece piece, ColouredPiece otherPiece) {
        if (piece == null || otherPiece == null) {
            return false;
        }
        return piece.getColour().equals(otherPiece.getColour()) && piece.getPiece().equals(otherPiece.getPiece());
    }

    // More or less an equals method. Used to determine threefold repetition. Doesn't compare pieces by .equals, uses
    // comparePieces. Unsure if this should be the actual equals method or if that will be problematic. This method is
    // really shit btw, not sure of any much better ways to do it
    public boolean sameBoard(Board otherBoard) {
        // Manually check through every square to make sure they're the same
        for (BoardPosition position: BoardPosition.allSquares) {
            if (!((getPieceAtPosition(position) == null && otherBoard.getPieceAtPosition(position) == null)
                    || (comparePieces(getPieceAtPosition(position), otherBoard.getPieceAtPosition(position))))) {
                return false;
            }
        }
        return true;
    }
}
