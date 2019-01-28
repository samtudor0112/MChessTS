package chessboard;

/**
 * The two colours a player can be, white or black.
 */
public enum PlayerColour {
    WHITE,
    BLACK;

    public static PlayerColour getOtherColour(PlayerColour colour) {
        return colour == WHITE ? BLACK : WHITE;
    }
}
