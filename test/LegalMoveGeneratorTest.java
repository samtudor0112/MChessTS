import org.junit.*;

import static org.junit.Assert.*;

import chessboard.*;

import java.util.ArrayList;
import java.util.Arrays;

public class LegalMoveGeneratorTest {

    // Test that the legal move generator generates the correct amount of moves after depth 1-6
    @Test
    public void perftTestStartPosition() {
        int [] perftAtuals = {20, 400, 8902, 197281, 4865609, 119060324};

        ArrayList<State> states = new ArrayList<>(Arrays.asList(new State()));

        int totalNodes = 0;
        long startTime = System.currentTimeMillis();

        int lastOutNodes = 0;
        long lastOutTime = startTime;

        for (int i = 0; i < 6; i++) {
            ArrayList<State> tempStates = (ArrayList<State>) states.clone();
            states = new ArrayList<>();
            for (State state: tempStates) {
                if (state.getGameStatus() == State.WHITE_WIN || state.getGameStatus() == State.BLACK_WIN) {
                    // Can't do anything from this state
                    continue;
                }
                ArrayList<Move> legalMoves = state.getAllLegalMoves();
                for (Move move: legalMoves) {
                    states.add(state.executeMove(move));
                    totalNodes++;
                    if (System.currentTimeMillis() > lastOutTime + 5000) {
                        System.out.println("Total Nodes: " + totalNodes);
                        System.out.println("Total Elapsed Time: " + (System.currentTimeMillis() - startTime) + "ms");
                        System.out.println("Nps since last output: " + ((double)(totalNodes - lastOutNodes)/(double)(System.currentTimeMillis() - lastOutTime) * 1000));
                        System.out.println("Total Nps: " + ((double)totalNodes / (double)(System.currentTimeMillis() - startTime) * 1000));
                        lastOutTime = System.currentTimeMillis();
                        lastOutNodes = totalNodes;
                    }
                }
            }

            assertEquals(perftAtuals[i], states.size());
            System.out.println("Perft " + (i + 1) + " correct");
        }
    }
}
