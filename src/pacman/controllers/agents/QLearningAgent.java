package pacman.controllers.agents;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
public class QLearningAgent extends Controller<MOVE> {
	private final Map<StateActionPair, Double> qTable = new HashMap<>();

	private final double alpha = 0.1;          // learning rate
	private final double gamma = 0.9;          // discount factor

	// single epsilon field so tests can read it
	private final double epsilon = 0.1;

	// you can still keep a schedule if you want
	private final double epsilonStart = epsilon;
	private final double epsilonEnd = 0.05;

	private final int episodes = 500;
	private final int maxSteps = 2000;

	private final Random rng = new Random(0);

public QLearningAgent() {
    train();
}

public void train() {
    StarterGhosts ghosts = new StarterGhosts();

    int evalInterval = 50;     // for plotting
    int evalGames = 5;

    for (int ep = 0; ep < episodes; ep++) {
        Game game = new Game(rng.nextLong());
        int steps = 0;
        int lastScore = 0;

        while (!game.gameOver() && steps < maxSteps) {
            GameState state = GameState.fromGame(game);

            double epsilon = currentEpsilon(ep);
            MOVE action = selectActionEpsilonGreedy(state, epsilon);

            EnumMap<GHOST, MOVE> ghostMoves =
                    ghosts.getMove(game.copy(), System.currentTimeMillis());
            game.advanceGame(action, ghostMoves);
            steps++;

            int newScore = game.getScore();
            double reward = newScore - lastScore;
            lastScore = newScore;

            GameState nextState = GameState.fromGame(game);

            updateQ(state, action, reward, nextState);
        }

        if ((ep + 1) % evalInterval == 0) {
            double avg = evaluatePolicy(evalGames);
            System.out.printf(
                    "[QLearning] Episode %d, average score over %d games: %.1f%n",
                    ep + 1, evalGames, avg);
        }
    }
}

private double currentEpsilon(int episode) {
    double t = (double) episode / Math.max(1, episodes - 1);
    return epsilonStart + t * (epsilonEnd - epsilonStart);
}

private MOVE selectActionEpsilonGreedy(GameState state, double epsilon) {
    if (rng.nextDouble() < epsilon) {
        List<MOVE> moves = state.getLegalMoves();
        return moves.get(rng.nextInt(moves.size()));
    }
    return getBestAction(state);
}

private double getQValue(GameState state, MOVE action) {
    StateActionPair key = new StateActionPair(state, action);
    Double val = qTable.get(key);
    if (val == null) {
        return 0.0;
    }
    return val;
}

private void setQValue(GameState state, MOVE action, double value) {
    StateActionPair key = new StateActionPair(state, action);
    qTable.put(key, value);
}

private double getMaxQValue(GameState state) {
    double best = 0.0;
    for (MOVE a : state.getLegalMoves()) {
        double q = getQValue(state, a);
        if (q > best) {
            best = q;
        }
    }
    return best;
}

private void updateQ(GameState state, MOVE action,
                     double reward, GameState nextState) {

    double oldQ = getQValue(state, action);
    double maxNextQ = getMaxQValue(nextState);

    double updated = oldQ + alpha * (reward + gamma * maxNextQ - oldQ);
    setQValue(state, action, updated);
}

private MOVE getBestAction(GameState state) {
    MOVE bestAction = MOVE.NEUTRAL;
    double bestValue = Double.NEGATIVE_INFINITY;

    for (MOVE a : state.getLegalMoves()) {
        double q = getQValue(state, a);
        if (q > bestValue) {
            bestValue = q;
            bestAction = a;
        }
    }

    if (bestAction == null) {
        bestAction = MOVE.NEUTRAL;
    }
    return bestAction;
}

private double evaluatePolicy(int games) {
    StarterGhosts ghosts = new StarterGhosts();
    double total = 0.0;

    for (int i = 0; i < games; i++) {
        Game game = new Game(rng.nextLong());
        int steps = 0;
        int lastScore = 0;

        while (!game.gameOver() && steps < maxSteps) {
            GameState state = GameState.fromGame(game);
            MOVE action = getBestAction(state);   // greedy

            EnumMap<GHOST, MOVE> ghostMoves =
                    ghosts.getMove(game.copy(), System.currentTimeMillis());
            game.advanceGame(action, ghostMoves);
            steps++;

            int newScore = game.getScore();
            double reward = newScore - lastScore;
            lastScore = newScore;

            GameState nextState = GameState.fromGame(game);
            updateQ(state, action, reward, nextState);   // mild on-policy update
        }

        total += game.getScore();
    }

    return total / games;
}

@Override
public MOVE getMove(Game game, long timeDue) {
    GameState state = GameState.fromGame(game);
    return getBestAction(state);
}
}