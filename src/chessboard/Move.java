package chessboard;

/**
 * A single move
 */
public class Move {
    private ColouredPiece piece;
    private BoardPosition newPosition;
    private boolean taking;

    // Only set when there's ambiguity
    private String oldPositionCoordinate;

    // Only set for a special move: a castle, or a promotion
    private String specialMove;

    // Only set for a castle
    private ColouredPiece castlingPiece;
    private BoardPosition castlingPosition;

    // Only set for a promotion
    private ColouredPiece promotionTo;

    // Null if not taking, newPosition if not taking enpessant, different otherwise
    private BoardPosition takePosition;

    // Where the king castle to, depending on the colour
    private static BoardPosition kingsideCastlePosition;
    private static BoardPosition queensideCastlePosition;

    // A standard piece move
    public Move(ColouredPiece piece, BoardPosition newPosition, boolean taking) throws InvalidMoveException {
        if (piece.getPiece() == Piece.PAWN) {
            throw new InvalidMoveException("Invalid move!");
        }

        this.piece = piece;
        this.newPosition = newPosition;
        this.taking = taking;
        if (taking) {
            this.takePosition = newPosition;
        }
    }

    // Two pieces could move to the same square, or a pawn move
    public Move(ColouredPiece piece, BoardPosition newPosition, String oldPositionCoordinate, boolean taking) {
        this.piece = piece;
        this.newPosition = newPosition;
        this.taking = taking;
        this.oldPositionCoordinate = oldPositionCoordinate;
        if (taking) {
            this.takePosition = newPosition;
        }
    }

    // En passant
    public Move(String specialMove, ColouredPiece piece, BoardPosition newPosition, String oldPositionCoordinate, BoardPosition takePosition)
            throws InvalidMoveException {
        int takeDirection = piece.getColour() == PlayerColour.WHITE ? 1 : -1;
        try {
            if (!specialMove.equals("En passant") || piece.getPiece() != Piece.PAWN || !newPosition.equals(takePosition.createAddedPosition(0, takeDirection))) {
                throw new InvalidMoveException("Invalid move!");
            }
        } catch (InvalidBoardPositionException e) {
            throw new InvalidMoveException("Invalid move!");
        }

        this.specialMove = specialMove;
        this.piece = piece;
        this.newPosition = newPosition;
        this.oldPositionCoordinate = oldPositionCoordinate;
        this.taking = true;
        this.takePosition = takePosition;
    }

    // Castling
    public Move(String specialMove, ColouredPiece king, BoardPosition newKingPosition, ColouredPiece rook, BoardPosition newRookPosition) throws InvalidMoveException {
        BoardPosition kingsideCastleRookPosition;
        BoardPosition queensideCastleRookPosition;
        try {
            if (piece.getColour() == PlayerColour.WHITE) {
                kingsideCastlePosition = new BoardPosition(6, 0);
                queensideCastlePosition = new BoardPosition(2, 0);
                kingsideCastleRookPosition = new BoardPosition(5, 0);
                queensideCastleRookPosition = new BoardPosition(3, 0);
            } else {
                // Black
                kingsideCastlePosition = new BoardPosition(6, 7);
                queensideCastlePosition = new BoardPosition(2, 7);
                kingsideCastleRookPosition = new BoardPosition(5, 7);
                queensideCastleRookPosition = new BoardPosition(3, 7);
            }
        } catch (InvalidBoardPositionException e) {
            // This should never happen
            System.out.println("Something's wrong!");
            return;
        }
        if (!specialMove.equals("Castling") || king.getPiece() != Piece.KING
                || rook.getPiece()!= Piece.ROOK) {
            throw new InvalidMoveException("Invalid move!");
        }
        if (newKingPosition == kingsideCastlePosition) {
            if (newRookPosition != kingsideCastleRookPosition) {
                throw new InvalidMoveException("Invalid move!");
            }
        } else if (newKingPosition == queensideCastlePosition) {
            if (newRookPosition != queensideCastleRookPosition) {
                throw new InvalidMoveException("Invalid move!");
            }
        } else {
            throw new InvalidMoveException("Invalid move!");
        }
        this.specialMove = specialMove;
        this.piece = king;
        this.newPosition = newKingPosition;
        this.castlingPiece = rook;
        this.castlingPosition = newRookPosition;
        this.taking = false;
    }

    // Promotion
    public Move(String specialMove, ColouredPiece piece, BoardPosition newPosition, String oldPositionCoordinate,
                boolean taking, ColouredPiece promotionTo) throws InvalidMoveException {
        if (!specialMove.equals("Promoting") || piece.getPiece() != Piece.PAWN) {
            throw new InvalidMoveException("Invalid move!");
        }
        this.specialMove = specialMove;
        this.piece = piece;
        this.newPosition = newPosition;
        this.oldPositionCoordinate = oldPositionCoordinate;
        this.taking = taking;
        if (taking) {
            this.takePosition = newPosition;
        }
        this.promotionTo = promotionTo;
    }

    public String getString() {
        String outString;
        if (specialMove == null) {
            // Standard move
            outString = piece.getPiece().firstLetter;
            if (oldPositionCoordinate != null) {
                outString += oldPositionCoordinate;
            }
            if (taking) {
                outString += "x";
            }
            outString += newPosition;
        } else if (specialMove.equals("Castling")) {
            if (newPosition.equals(kingsideCastlePosition)) {
                outString = "O-O";
            } else {
                // Queenside
                outString = "O-O-O";
            }
        } else if (specialMove.equals("Promotion")){
            // Promoting
            outString = oldPositionCoordinate;
            if (taking) {
                outString += "x";
            }
            outString += newPosition;
            outString += "=";
            outString += promotionTo.getPiece().firstLetter;
        } else {
            // En passant
            outString = oldPositionCoordinate;
            outString += "x";
            outString += newPosition;
        }

        return outString;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            return getString().equals(((Move)obj).getString());
        }
        return false;
    }

    public ColouredPiece getPiece() {
        return piece;
    }

    public BoardPosition getNewPosition() {
        return newPosition;
    }

    public boolean isTaking() {
        return taking;
    }

    public BoardPosition getTakePosition() {
        return takePosition;
    }

    public String getSpecialMove() {
        return specialMove;
    }

    public ColouredPiece getCastlingPiece() {
        return castlingPiece;
    }

    public BoardPosition getCastlingPosition() {
        return castlingPosition;
    }

    public ColouredPiece getPromotionTo() {
        return promotionTo;
    }
}
