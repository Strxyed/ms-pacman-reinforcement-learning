package pacman.controllers.agents;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * ValueIterationAgent — template summary:
 * Initialize: generate your abstract state space, set V(s)=0, and set policy(s)=MOVE.NEUTRAL.
 * Repeat for a fixed number of sweeps:
 * 1. For each state, enumerate legal actions.
 * 2. For each action, evaluate the expectation of r + γ·V(s′) over all transitions returned by the model.
 * 3. Store the best value and action into fresh maps and replace the old ones at the end of the sweep.
 * Acting: map the live game to a GameState and return policy(state), falling back to MOVE.NEUTRAL when the state was never seen.
 */
public class ValueIterationAgent extends Controller<MOVE> {


	private final Map<GameState, Double> valueFunction = new HashMap<>();
	private final Map<GameState, MOVE> policy = new HashMap<>();
	private final double gamma = 0.9;
	private final int iterations = 20;
	private final List<GameState> states;

	public ValueIterationAgent() {
	    // build abstract state space
	    this.states = Collections.unmodifiableList(StateGenerator.getAllStates());

	    // initialise V(s) and policy(s)
	    for (GameState s : states) {
	        valueFunction.put(s, 0.0);
	        policy.put(s, MOVE.NEUTRAL);
	    }

	    // run value iteration once at construction
	    runValueIteration();
	}

	private void runValueIteration() {
	    // dummy game object as planning model base
	    Game dummyGame = new Game(0);

	    for (int it = 0; it < iterations; it++) {
	        Map<GameState, Double> newValues = new HashMap<>();
	        Map<GameState, MOVE> newPolicy = new HashMap<>();

	        for (GameState state : states) {
	            double bestValue = Double.NEGATIVE_INFINITY;
	            MOVE bestAction = MOVE.NEUTRAL;

	            for (MOVE action : state.getLegalMoves()) {
	                List<Transition> transitions = state.getTransitions(dummyGame, action);

	                double q = 0.0;
	                for (Transition t : transitions) {
	                    double nextV = valueFunction.getOrDefault(t.nextState, 0.0);
	                    q += t.probability * (t.reward + gamma * nextV);
	                }

	                if (q > bestValue) {
	                    bestValue = q;
	                    bestAction = action;
	                }
	            }

	            if (bestValue == Double.NEGATIVE_INFINITY) {
	                bestValue = 0.0;
	            }

	            newValues.put(state, bestValue);
	            newPolicy.put(state, bestAction);
	        }

	        valueFunction.clear();
	        valueFunction.putAll(newValues);
	        policy.clear();
	        policy.putAll(newPolicy);
	    }
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
	    GameState state = GameState.fromGame(game);
	    MOVE action = policy.get(state);
	    if (action == null) {
	        action = MOVE.NEUTRAL;
	    }
	    return action;
	}
}