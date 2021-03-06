package chessboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Represents one piece, for one specific player, on the board.
 */
public class ColouredPiece {
    private Piece piece;
    private PlayerColour colour;

    private ArrayList<ArrayList<RelativeBoardPosition>> attackRoutes = new ArrayList<>();

    public ColouredPiece(Piece piece, PlayerColour colour) {
        this.piece = piece;
        this.colour = colour;
        generateAttackRoutes();
    }

    public Piece getPiece() {
        return piece;
    }

    public PlayerColour getColour() {
        return colour;
    }

    public ArrayList<ArrayList<RelativeBoardPosition>> getAttackRoutes() {
        // I'm really not sure what's going on here but hopefully the attack routes are unmodifiable. This entire class
        // is designed to be immutable.
        return new ArrayList<>(Collections.unmodifiableList(attackRoutes));
    }

    // Each attack route represents one type of attack a piece can make e.g. a diagonal for a bishop. Once one element
    // of an attack route is either invalid (off the board) or a capture, the rest of the attacks are no longer valid
    public void generateAttackRoutes() {
        try {
            ArrayList<RelativeBoardPosition> route1;
            ArrayList<RelativeBoardPosition> route2;
            ArrayList<RelativeBoardPosition> route3;
            ArrayList<RelativeBoardPosition> route4;
            ArrayList<RelativeBoardPosition> route5;
            ArrayList<RelativeBoardPosition> route6;
            ArrayList<RelativeBoardPosition> route7;
            ArrayList<RelativeBoardPosition> route8;
            switch (piece) {
                case PAWN:
                    if (colour == PlayerColour.WHITE) {
                        route1 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(1, 1)));
                        route2 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(-1, 1)));
                    } else {
                        // Black
                        route1 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(1, -1)));
                        route2 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(-1, -1)));
                    }
                    attackRoutes.add(route1);
                    attackRoutes.add(route2);
                    return;
                case KNIGHT:
                    route1 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(1, 2)));
                    route2 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(2, 1)));
                    route3 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(-1, 2)));
                    route4 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(-2, 1)));
                    route5 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(1, -2)));
                    route6 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(2, -1)));
                    route7 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(-1, -2)));
                    route8 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(-2, -1)));
                    attackRoutes.add(route1);
                    attackRoutes.add(route2);
                    attackRoutes.add(route3);
                    attackRoutes.add(route4);
                    attackRoutes.add(route5);
                    attackRoutes.add(route6);
                    attackRoutes.add(route7);
                    attackRoutes.add(route8);
                    return;
                case BISHOP:
                    route1 = new ArrayList<>();
                    route2 = new ArrayList<>();
                    route3 = new ArrayList<>();
                    route4 = new ArrayList<>();
                    for (int i = 1; i <= 7; i++) {
                        route1.add(new RelativeBoardPosition(i, i));
                        route2.add(new RelativeBoardPosition(i, -i));
                        route3.add(new RelativeBoardPosition(-i, i));
                        route4.add(new RelativeBoardPosition(-i, -i));
                    }
                    attackRoutes.add(route1);
                    attackRoutes.add(route2);
                    attackRoutes.add(route3);
                    attackRoutes.add(route4);
                    return;
                case ROOK:
                    route1 = new ArrayList<>();
                    route2 = new ArrayList<>();
                    route3 = new ArrayList<>();
                    route4 = new ArrayList<>();
                    for (int i = 1; i <= 7; i++) {
                        route1.add(new RelativeBoardPosition(i, 0));
                        route2.add(new RelativeBoardPosition(0, i));
                        route3.add(new RelativeBoardPosition(-i, 0));
                        route4.add(new RelativeBoardPosition(0, -i));
                    }
                    attackRoutes.add(route1);
                    attackRoutes.add(route2);
                    attackRoutes.add(route3);
                    attackRoutes.add(route4);
                    return;
                case QUEEN:
                    route1 = new ArrayList<>();
                    route2 = new ArrayList<>();
                    route3 = new ArrayList<>();
                    route4 = new ArrayList<>();
                    route5 = new ArrayList<>();
                    route6 = new ArrayList<>();
                    route7 = new ArrayList<>();
                    route8 = new ArrayList<>();
                    for (int i = 1; i <= 7; i++) {
                        route1.add(new RelativeBoardPosition(i, i));
                        route2.add(new RelativeBoardPosition(i, -i));
                        route3.add(new RelativeBoardPosition(-i, i));
                        route4.add(new RelativeBoardPosition(-i, -i));
                        route5.add(new RelativeBoardPosition(i, 0));
                        route6.add(new RelativeBoardPosition(0, i));
                        route7.add(new RelativeBoardPosition(-i, 0));
                        route8.add(new RelativeBoardPosition(0, -i));
                    }
                    attackRoutes.add(route1);
                    attackRoutes.add(route2);
                    attackRoutes.add(route3);
                    attackRoutes.add(route4);
                    attackRoutes.add(route5);
                    attackRoutes.add(route6);
                    attackRoutes.add(route7);
                    attackRoutes.add(route8);
                    return;
                case KING:
                    route1 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(1, 0)));
                    route2 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(1, 1)));
                    route3 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(0, 1)));
                    route4 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(-1, 1)));
                    route5 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(-1, 0)));
                    route6 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(-1, -1)));
                    route7 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(0, -1)));
                    route8 = new ArrayList<>(Arrays.asList(new RelativeBoardPosition(1, -1)));
                    attackRoutes.add(route1);
                    attackRoutes.add(route2);
                    attackRoutes.add(route3);
                    attackRoutes.add(route4);
                    attackRoutes.add(route5);
                    attackRoutes.add(route6);
                    attackRoutes.add(route7);
                    attackRoutes.add(route8);
                    return;
            }
        } catch (InvalidBoardPositionException e) {
            // This should never happen
            e.printStackTrace();
            System.out.println("Something's wrong!");
            return;
        }
    }
}
