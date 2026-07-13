package pacman.controllers.agents;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
public class PolicyIterationAgent extends Controller<MOVE> {
private final Map<GameState, MOVE> policy = new HashMap<>();
private final Map<GameState, Double> valueFunction = new HashMap<>();
private final double gamma = 0.9;
private final int maxIterations = 20;
private final List<GameState> states;

public PolicyIterationAgent() {
    states = Collections.unmodifiableList(StateGenerator.getAllStates());

    Random rng = new Random(0);

    for (GameState s : states) {
        valueFunction.put(s, 0.0);
        List<MOVE> legal = s.getLegalMoves();
        MOVE a = legal.get(rng.nextInt(legal.size()));
        policy.put(s, a);
    }

    for (int i = 0; i < maxIterations; i++) {
        policyEvaluation(states);
        boolean stable = policyImprovement(states);
        if (stable) {
            break;
        }
    }
}

private void policyEvaluation(List<GameState> states) {
    Game dummyGame = new Game(0);
    int evalSweeps = 10;

    for (int sweep = 0; sweep < evalSweeps; sweep++) {
        Map<GameState, Double> newValues = new HashMap<>();

        for (GameState s : states) {
            MOVE a = policy.get(s);
            if (a == null) {
                newValues.put(s, 0.0);
                continue;
            }

            double expected = 0.0;
            for (Transition t : s.getTransitions(dummyGame, a)) {
                double nextV = valueFunction.getOrDefault(t.nextState, 0.0);
                expected += t.probability * (t.reward + gamma * nextV);
            }

            newValues.put(s, expected);
        }

        valueFunction.clear();
        valueFunction.putAll(newValues);
    }
}

private boolean policyImprovement(List<GameState> states) {
    Game dummyGame = new Game(0);
    boolean stable = true;

    for (GameState s : states) {
        MOVE oldAction = policy.get(s);

        double bestValue = Double.NEGATIVE_INFINITY;
        MOVE bestAction = oldAction;

        for (MOVE a : s.getLegalMoves()) {
            double q = 0.0;
            for (Transition t : s.getTransitions(dummyGame, a)) {
                double nextV = valueFunction.getOrDefault(t.nextState, 0.0);
                q += t.probability * (t.reward + gamma * nextV);
            }

            if (q > bestValue) {
                bestValue = q;
                bestAction = a;
            }
        }

        if (bestAction == null) {
            bestAction = MOVE.NEUTRAL;
        }

        policy.put(s, bestAction);

        if (oldAction != bestAction) {
            stable = false;
        }
    }

    return stable;
}

@Override
public MOVE getMove(Game game, long timeDue) {
    GameState state = GameState.fromGame(game);
    MOVE action = policy.get(state);
    if (action == null) {
        return MOVE.NEUTRAL;
    }
    return action;
}
}