/**
 * Represents one piece, for one specific player, on the board.
 */
public class ColouredPiece {
    private Piece piece;
    private PlayerColour colour;

    public ColouredPiece(Piece piece, PlayerColour colour) {
        this.piece = piece;
        this.colour = colour;
    }

    public Piece getPiece() {
        return piece;
    }

    public PlayerColour getColour() {
        return colour;
    }
}
