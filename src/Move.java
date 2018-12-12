/**
 * A single move
 */
public class Move {
    private ColouredPiece piece;
    private BoardPosition newPosition;

    // Not set when castling
    private boolean taking;

    // Only set when there's ambiguity
    private String oldPositionCoordinate;

    // Only set for a special move: a castle, or a promotion
    private String specialMove;

    // Only set for a promotion
    private ColouredPiece promotionTo;

    private static BoardPosition kingsideCastlePosition;
    private static BoardPosition queensideCastlePosition;

    // A standard piece move
    public Move(ColouredPiece piece, BoardPosition newPosition, boolean taking) throws InvalidMoveException {
        this.piece = piece;
        this.newPosition = newPosition;
        this.taking = taking;

        if (piece.getPiece() == Piece.PAWN) {
            throw new InvalidMoveException("Invalid move!");
        }
    }

    // Two pieces could move to the same square, or a pawn move
    public Move(ColouredPiece piece, BoardPosition newPosition, String oldPositionCoordinate, boolean taking) {
        this.piece = piece;
        this.newPosition = newPosition;
        this.taking = taking;
        this.oldPositionCoordinate = oldPositionCoordinate;
    }

    // Castling
    public Move(String specialMove, ColouredPiece piece, BoardPosition newPosition) throws InvalidMoveException {
        try {
            if (piece.getColour() == PlayerColour.WHITE) {
                kingsideCastlePosition = new BoardPosition(6, 0);
                queensideCastlePosition = new BoardPosition(2, 0);
            } else {
                // Black
                kingsideCastlePosition = new BoardPosition(6, 7);
                queensideCastlePosition = new BoardPosition(2, 7);
            }
        } catch (InvalidBoardPositionException e) {
            // This should never happen
            System.out.println("Something's wrong!");
            return;
        }
        if (!specialMove.equals("Castling") || piece.getPiece() != Piece.KING
                || !(newPosition.equals(kingsideCastlePosition) || newPosition.equals(queensideCastlePosition))) {
            throw new InvalidMoveException("Invalid move!");
        }
        this.specialMove = specialMove;
        this.piece = piece;
        this.newPosition = newPosition;
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
        } else {
            // Promoting
            outString = oldPositionCoordinate;
            if (taking) {
                outString += "x";
            }
            outString += newPosition;
            outString += "=";
            outString += promotionTo.getPiece().firstLetter;
        }

        return outString;
    }

    public ColouredPiece getPiece() {
        return piece;
    }

    public BoardPosition getNewPosition() {
        return newPosition;
    }

    public boolean getTaking() {
        return taking;
    }

    public String getOldPositionCoordinate() {
        return oldPositionCoordinate;
    }

    public String getSpecialMove() {
        return specialMove;
    }

    public ColouredPiece getPromotionTo() {
        return promotionTo;
    }
}
