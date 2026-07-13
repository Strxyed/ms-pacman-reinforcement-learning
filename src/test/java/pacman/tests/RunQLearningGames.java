package test.java.pacman.tests;

import pacman.controllers.agents.QLearningAgent;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


import java.util.EnumMap;
import java.util.Random;

public class RunQLearningGames {

    private static final int MAX_STEPS = 2000;
    private static final Random RNG = new Random(123);

    private static double playOneGame(QLearningAgent pacman,
                                      StarterGhosts ghosts,
                                      int[] winCounter) {

        Game game = new Game(RNG.nextLong());
        int steps = 0;

        while (!game.gameOver() && steps < MAX_STEPS) {
            MOVE pacMove = pacman.getMove(game.copy(), System.currentTimeMillis());
            EnumMap<GHOST, MOVE> ghostMoves =
                    ghosts.getMove(game.copy(), System.currentTimeMillis());
            game.advanceGame(pacMove, ghostMoves);
            steps++;
        }

        // win if all pills and power pills are eaten
        if (game.getNumberOfActivePills() == 0 &&
            game.getNumberOfActivePowerPills() == 0) {
            winCounter[0]++;
        }

        return game.getScore();
    }

    public static void main(String[] args) {
        QLearningAgent pacman = new QLearningAgent();
        StarterGhosts ghosts = new StarterGhosts();

        int games = 50;
        double total = 0.0;
        int[] winCounter = new int[]{0};

        System.out.println("Q learning evaluation vs StarterGhosts");
        System.out.println("Game   Score");

        for (int i = 0; i < games; i++) {
            double score = playOneGame(pacman, ghosts, winCounter);
            total += score;
            System.out.printf("%2d   %.0f%n", i, score);
        }

        double average = total / games;
        int wins = winCounter[0];
        int losses = games - wins;

        System.out.println();
        System.out.printf("Average score: %.1f%n", average);
        System.out.printf("Wins: %d  Losses: %d%n", wins, losses);
    }
}
