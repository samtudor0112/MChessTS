package search;

import chessboard.Move;
import chessboard.PlayerColour;
import chessboard.State;

import java.util.*;

public class MCTS {

    private Node root;

    private int timeLimit;

    private PlayerColour ourColour;

    public MCTS(State startState, int timeLimit, PlayerColour ourColour) {
        root = new Node(startState);
        this.timeLimit = timeLimit;
        this.ourColour = ourColour;
    }

    /**
     * Executes the MCTS search. Takes slightly longer than timeLimit. Will return the approximately
     * best Move object to perform.
     *
     * @return the best Move object from the startState.
     */
    public Move getBestMove() {
        long startTime = System.currentTimeMillis();

        while(System.currentTimeMillis() < startTime + timeLimit) {
            Node newNode = selectAndExpandNewNode();
            double playoutResult = simulatePlayout(newNode);
            backPropagateResult(newNode, playoutResult);
        }

        return getBestMoveFromFinishedTree();
    }

    private Node selectAndExpandNewNode() {
        // Start at the root
        Node node = root;

        // Traverse the tree, selecting the best UCT score each time, until we have a leaf node
        while (node.getChildNodes().size() != 0) {
            int parentVisits = node.getVisits();
            node = Collections.max(node.getChildNodes(), Comparator.comparing(c -> UCTValue(c, parentVisits)));
        }
        // Expand node if it's still in progress
        if (node.getState().getGameStatus() == State.IN_PROGRESS) {
            expandNode(node);
            node = getRandomElement(node.getChildNodes());
        }

        return node;
    }

    private void expandNode(Node parent) {
        State boardState = parent.getState();
        ArrayList<Move> validMoves = boardState.getAllLegalMoves();
        validMoves.forEach(move -> {
            State newBoardState = boardState.executeMove(move);
            Node newNode = new Node(newBoardState);
            newNode.setParentAndMove(parent, move);
            parent.getChildNodes().add(newNode);
        });
    }

    // Fairly temporary random playout simulator. Can likely be greatly improved.
    private double simulatePlayout(Node node) {
        // For the moment, random playout
        State boardState = node.getState();
        while (boardState.getGameStatus() == State.IN_PROGRESS) {
            ArrayList<Move> validMoves = boardState.getAllLegalMoves();
            boardState = boardState.executeMove(getRandomElement(validMoves));
        }

        // Temporary basic reward function
        if (boardState.getGameStatus() == State.WHITE_WIN) {
            return ourColour == PlayerColour.WHITE ? 1 : 0;
        } else if (boardState.getGameStatus() == State.BLACK_WIN) {
            return ourColour == PlayerColour.BLACK ? 1 : 0;
        } else {
            // Draw
            return 0.3; // We'd prefer to win once than draw 3 times.
        }
    }

    private void backPropagateResult(Node node, double playoutResult) {
        while (node != null) {
            node.addVisit(playoutResult);
            node = node.getParentNode();
        }
    }

    // Returns the approximately best move to make from the root. Will be null if starting at a won/lost/drawn position.
    private Move getBestMoveFromFinishedTree() {
        Node bestNode = Collections.max(root.getChildNodes(), Comparator.comparing(Node::getVisits));
        return bestNode.getMove();
    }

    private double UCTValue(Node node, int parentVisits) {
        if (node.getVisits() == 0) {
            // Always visit each node once
            return Integer.MAX_VALUE;
        }

        return node.getReward() / (double) node.getVisits()
                + Math.sqrt(2.0 * Math.log(parentVisits) / (double) node.getVisits());
    }

    /**
     * Helper function to get a random element from a list
     *
     * @param list the list
     * @param <T> the type of the list
     *
     * @return the random element
     */
    private <T> T getRandomElement(List<T> list) {
        return list.get(randomInt(0, list.size()));
    }

    /**
     * Helper function for generating a random int from min to max (inclusive min, exclusive max)
     *
     * @param min the lower bound of the random range (inclusive)
     * @param max the upper bound of the random range (exclusive)
     *
     * @return the random number
     */
    private static int randomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt(max - min) + min;
    }
}
