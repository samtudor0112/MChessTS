import java.util.ArrayList;
import java.util.Arrays;

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

    // The first letter of each piece, used for string representations of moves
    public String firstLetter;

    public ArrayList<BoardPosition> relativeAttackSquares;

    static {
        PAWN.firstLetter = "";
        KNIGHT.firstLetter = "N";
        BISHOP.firstLetter = "B";
        ROOK.firstLetter = "R";
        QUEEN.firstLetter = "Q";
        KING.firstLetter = "K";
    }
}
