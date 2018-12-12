/**
 * Represents one piece on the chessboard
 */
public enum Piece {
    PAWN,
    KNIGHT,
    BISHOP,
    ROOK,
    QUEEN,
    KING;

    public String firstLetter;

    static {
        PAWN.firstLetter = "";
        KNIGHT.firstLetter = "N";
        BISHOP.firstLetter = "B";
        ROOK.firstLetter = "R";
        QUEEN.firstLetter = "Q";
        KING.firstLetter = "K";
    }
}
