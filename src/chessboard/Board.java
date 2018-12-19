package chessboard;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the board at any time, containing all the pieces of both colours
 */
public class Board {
    private HashMap<ColouredPiece, BoardPosition> boardMap;

    public Board(HashMap<ColouredPiece, BoardPosition> boardMap) {
        this.boardMap = (HashMap<ColouredPiece, BoardPosition>) boardMap.clone();
    }

    public Board() {
        HashMap<ColouredPiece, BoardPosition> boardMap = new HashMap<>();

        try {
            // Pawns
            for (int column = 0; column < 8; column++) {
                boardMap.put(new ColouredPiece(Piece.PAWN, PlayerColour.WHITE), new BoardPosition(column, 1));
                boardMap.put(new ColouredPiece(Piece.PAWN, PlayerColour.BLACK), new BoardPosition(column, 6));
            }

            // Kings
            boardMap.put(new ColouredPiece(Piece.KING, PlayerColour.WHITE), new BoardPosition(4, 0));
            boardMap.put(new ColouredPiece(Piece.KING, PlayerColour.BLACK), new BoardPosition(4, 7));

            // Queens
            boardMap.put(new ColouredPiece(Piece.QUEEN, PlayerColour.WHITE), new BoardPosition(3, 0));
            boardMap.put(new ColouredPiece(Piece.QUEEN, PlayerColour.BLACK), new BoardPosition(3, 7));

            // Bishops
            boardMap.put(new ColouredPiece(Piece.BISHOP, PlayerColour.WHITE), new BoardPosition(2, 0));
            boardMap.put(new ColouredPiece(Piece.BISHOP, PlayerColour.BLACK), new BoardPosition(2, 7));
            boardMap.put(new ColouredPiece(Piece.BISHOP, PlayerColour.WHITE), new BoardPosition(5, 0));
            boardMap.put(new ColouredPiece(Piece.BISHOP, PlayerColour.BLACK), new BoardPosition(5, 7));

            // Knights
            boardMap.put(new ColouredPiece(Piece.KNIGHT, PlayerColour.WHITE), new BoardPosition(1, 0));
            boardMap.put(new ColouredPiece(Piece.KNIGHT, PlayerColour.BLACK), new BoardPosition(1, 7));
            boardMap.put(new ColouredPiece(Piece.KNIGHT, PlayerColour.WHITE), new BoardPosition(6, 0));
            boardMap.put(new ColouredPiece(Piece.KNIGHT, PlayerColour.BLACK), new BoardPosition(6, 7));

            // Rooks
            boardMap.put(new ColouredPiece(Piece.ROOK, PlayerColour.WHITE), new BoardPosition(0, 0));
            boardMap.put(new ColouredPiece(Piece.ROOK, PlayerColour.BLACK), new BoardPosition(0, 7));
            boardMap.put(new ColouredPiece(Piece.ROOK, PlayerColour.WHITE), new BoardPosition(7, 0));
            boardMap.put(new ColouredPiece(Piece.ROOK, PlayerColour.BLACK), new BoardPosition(7, 7));
        } catch (InvalidBoardPositionException e) {
            // This should never happen
            System.out.println("Something's wrong!");
            return;
        }
    }

    // returns null if no piece at that position, otherwise the piece
    public ColouredPiece getPieceAtPosition(BoardPosition position) {
        for (Map.Entry<ColouredPiece, BoardPosition> entry : boardMap.entrySet()) {
            if (entry.getValue().equals(position)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public BoardPosition getPiecesPosition(ColouredPiece piece) {
        return boardMap.get(piece);
    }

    public BoardPosition moveAndTakePiece(ColouredPiece piece, BoardPosition newPosition) {
        ColouredPiece oldPiece = getPieceAtPosition(newPosition);
        if (oldPiece != null) {
            boardMap.remove(oldPiece);
        }
        return boardMap.put(piece, newPosition);
    }

    public BoardPosition replacePieceAtPosition(ColouredPiece piece, BoardPosition position) {
        boardMap.remove(getPieceAtPosition(position));
        return boardMap.put(piece, position);
    }

    public Board clone() {
        return new Board(boardMap);
    }
}
