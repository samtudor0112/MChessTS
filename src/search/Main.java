package search;

import chessboard.InvalidMoveException;
import chessboard.Move;
import chessboard.PlayerColour;
import chessboard.State;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

// Entry point for the engine
public class Main {

    // Basic playing method. User inputs enemy moves and time remaining before each move
    public static void main(String[] args) {
        if (args.length == 1) {
            // Starting from start board
        }
        PlayerColour ourColour = args[0].equals("white") ? PlayerColour.WHITE : PlayerColour.BLACK;

        State currentState = new State();

        if (ourColour == PlayerColour.BLACK) {
            // Read the oppositions move for the first move (white starts)
            currentState = takeInMoveInput(currentState);
        }

        while (true) {
            // Take in the remaining time as an input
            int timeRemaining = takeInTimeInput();
            // Time per move is maximum of 15 seconds or 5% of remaining time
            int timeToUse = Math.min(15000, 50 * timeRemaining);

            // Search for the best move
            MCTS search = new MCTS(currentState, timeToUse, ourColour);
            Move bestMove = search.getBestMove();

            // Output the move then execute it
            System.out.println(bestMove.toString());
            currentState = currentState.executeMove(bestMove);

            // Read the oppositions move
            currentState = takeInMoveInput(currentState);
        }
    }

    // Execute opposition move
    private static State takeInMoveInput(State currentState) {
        System.out.println("Enter the enemy's move: ");
        Move move = null;
        while (move == null) {
            String stringMove = readInput();
            try {
                move = currentState.getMoveFromString(stringMove);
            } catch (InvalidMoveException e) {
                System.out.println("Invalid move!");
            }
        }

        return currentState.executeMove(move);
    }

    // Get the time remaining as an integer
    private static int takeInTimeInput() {
        System.out.println("Enter the time remaining (format: mm:ss): ");
        String stringTime = readInput();
        String[] strings = stringTime.split(":");
        return 60 * Integer.parseInt(strings[0]) + Integer.parseInt(strings[1]);
    }

    // Read a user input string
    private static String readInput() {
        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                return br.readLine();
            } catch (IOException e) {
                System.out.println("Invalid!");
            }
        }
    }


}
