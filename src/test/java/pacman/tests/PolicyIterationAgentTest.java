package test.java.pacman.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import pacman.controllers.Controller;
import pacman.controllers.agents.PolicyIterationAgent;
import pacman.controllers.examples.NullGhosts;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

import java.lang.reflect.Field;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(3)
@TestMethodOrder(OrderAnnotation.class)
class PolicyIterationAgentTest {

    private static final int GAMES = 3;

    private final Controller<EnumMap<GHOST, MOVE>> ghosts = new NullGhosts();

    @Test
    @Order(1)
    @DisplayName("PolicyIterationAgent smoke check vs NullGhosts (>10)")
    void policyIterationBasicSanity() {
        Controller<MOVE> trained = new PolicyIterationAgent();

        System.out.printf("[PolicyIteration#1] Worth 3/20 points in coursework 2.%n");
        System.out.printf("[PolicyIteration#1] Setup: PolicyIterationAgent vs NullGhosts (%d games, max %d iterations, <=%ds).%n",
                GAMES, GameTestUtils.maxSteps(), GameTestUtils.maxDurationSeconds());
        System.out.printf("[PolicyIteration#1] Pass condition: average score > 10.%n");

        double average = GameTestUtils.averageScore("[PolicyIteration#1]", trained, ghosts, GAMES);
        boolean pass = average > 10.0;
        System.out.printf("[PolicyIteration#1] Result: %s (average %.2f)%n", pass ? "PASS" : "FAIL", average);
        TestProgressTracker.record("PolicyIteration#1", pass, 3.0);
        TestProgressTracker.singleDivider();
        assertTrue(pass, "[PolicyIteration#1] Expected average score > 10 against NullGhosts.");
    }

    @Test
    @Order(2)
    @DisplayName("PolicyIterationAgent achieves >=100 vs NullGhosts")
    void policyIterationScoresAgainstNullGhosts() {
        Controller<MOVE> trained = new PolicyIterationAgent();

        int maxIterations = getIntField(trained, "maxIterations");
        System.out.printf("[PolicyIteration#2] Worth 3/20 points in coursework 2.%n");
        System.out.printf("[PolicyIteration#2] Max improvement iterations: %d%n", maxIterations);
        System.out.printf("[PolicyIteration#2] Setup: PolicyIterationAgent vs NullGhosts (%d games, max %d iterations, <=%ds).%n",
                GAMES, GameTestUtils.maxSteps(), GameTestUtils.maxDurationSeconds());
        System.out.printf("[PolicyIteration#2] Pass condition: average score >= 100.%n");

        double trainedAverage = GameTestUtils.averageScore("[PolicyIteration#2]", trained, ghosts, GAMES);

        boolean pass = trainedAverage >= 100.0;
        System.out.printf("[PolicyIteration#2] Result: %s (average %.2f)%n", pass ? "PASS" : "FAIL", trainedAverage);
        TestProgressTracker.record("PolicyIteration#2", pass, 3.0);

        TestProgressTracker.singleDivider();
        System.out.println("[PolicyIteration#2] Information only: PolicyIterationAgent vs StarterGhosts (3 games, max "
                + GameTestUtils.maxSteps() + " iterations, <=" + GameTestUtils.maxDurationSeconds() + "s).");
        GameTestUtils.averageScore("[PolicyIteration#2][Info]", trained, new StarterGhosts(), 3);
        TestProgressTracker.doubleDivider();

        assertTrue(pass,
                String.format("Expected PolicyIterationAgent average to be at least 100 but was %.2f", trainedAverage));
    }

    private int getIntField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(target);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to read field '" + fieldName + "'", e);
        }
    }
}
