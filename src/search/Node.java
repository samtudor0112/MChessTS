package search;

import chessboard.Move;
import chessboard.State;

import java.util.ArrayList;

/**
 * A node in the MCTS search tree
 */
public class Node {

    private Node parentNode;

    private Move move;

    private ArrayList<Node> childNodes;

    private State state;

    private int visits;

    private double reward;

    public Node(State state) {
        this.state = state;
        childNodes = new ArrayList<>();
        visits = 0;
        reward = 0;
    }

    public void setParentAndMove(Node parentNode, Move move) {
        this.parentNode = parentNode;
        this.move = move;
    }

    public void addVisit(double result) {
        visits++;
        reward += result;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public State getState() {
        return state;
    }

    public Move getMove() {
        return move;
    }

    public ArrayList<Node> getChildNodes() {
        return childNodes;
    }

    public int getVisits() {
        return visits;
    }

    public double getReward() {
        return reward;
    }
}
