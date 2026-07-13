package pacman.controllers.agents;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import pacman.Executor;
import pacman.controllers.agents.ValueIterationAgent;
import pacman.controllers.examples.StarterGhosts;

/**
 * Simple smoke test: run one timed game and check it completes with a non-negative score.
 */
@Disabled("Legacy template test – enable manually if you need it")
public class LegacyGameSmokeTest {

    @Test
    public void playsOneGameAndFinishes() {
    	double score=0;
    	Executor exec = new Executor();
        ValueIterationAgent agent = new  ValueIterationAgent();
        score = exec.runGameTimed(agent, new StarterGhosts(), false);
        assertTrue(score >= 0, "Score should be >= 0");
    }
}
