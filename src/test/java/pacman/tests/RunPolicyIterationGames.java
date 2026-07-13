package test.java.pacman.tests;

import pacman.controllers.agents.PolicyIterationAgent;
import pacman.controllers.examples.NullGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.EnumMap;
import java.util.Random;

public class RunPolicyIterationGames {

    private static final Random RNG = new Random(1);

    private static double playOneGame(PolicyIterationAgent pacman,
                                      NullGhosts ghosts) {

        Game game = new Game(RNG.nextLong());
        int steps = 0;

        while (!game.gameOver() && steps < GameTestUtils.maxSteps()) {
            MOVE pacMove = pacman.getMove(game.copy(), System.currentTimeMillis());

            // small randomness for reporting only
            double epsilon = 0.15;
            if (RNG.nextDouble() < epsilon) {
                MOVE[] moves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());
                if (moves.length > 0) {
                    pacMove = moves[RNG.nextInt(moves.length)];
                }
            }

            EnumMap<GHOST, MOVE> ghostMoves = ghosts.getMove(game.copy(), System.currentTimeMillis());
            game.advanceGame(pacMove, ghostMoves);
            steps++;
        }
        return game.getScore();
    }

    public static void main(String[] args) {
        PolicyIterationAgent pacman = new PolicyIterationAgent();
        NullGhosts ghosts = new NullGhosts();

        int games = 20;
        double total = 0.0;

        System.out.println("Policy Iteration");
        System.out.println("Game   Score");

        for (int i = 0; i < games; i++) {
            double score = playOneGame(pacman, ghosts);
            total += score;
            System.out.printf("%2d   %.0f%n", i, score);
        }

        double average = total / games;
        System.out.println();
        System.out.printf("Average: %.1f%n", average);
    }
}