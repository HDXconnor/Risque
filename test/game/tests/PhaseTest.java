/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.tests;

import game.objects.Game;
import game.objects.Phase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 *
 * @author Simeon
 */
public class PhaseTest {
    Game game;
    
    public PhaseTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        game = new Game("name", "pw");
    }
    
    @After
    public void tearDown() {
        game = null;
    }

    @Test
    public void testNextPhaseDeploy() {
        assumeTrue(game.getGameState().getPhase().getPhase().equals(Phase.SETUP));
        game.getGameState().getPhase().nextPhase();
        assertTrue(game.getGameState().getPhase().getPhase().equals(Phase.DEPLOY));
    }
    
    @Test
    public void testNextPhaseAttack() {
        assumeTrue(game.getGameState().getPhase().getPhase().equals(Phase.SETUP));
        game.getGameState().getPhase().nextPhase();
        assumeTrue(game.getGameState().getPhase().getPhase().equals(Phase.DEPLOY));
        game.getGameState().getPhase().nextPhase();
        assertTrue(game.getGameState().getPhase().getPhase().equals(Phase.ATTACK));
    }
    
    @Test
    public void testNextPhaseMove() {
        assumeTrue(game.getGameState().getPhase().getPhase().equals(Phase.SETUP));
        game.getGameState().getPhase().nextPhase();
        assumeTrue(game.getGameState().getPhase().getPhase().equals(Phase.DEPLOY));
        game.getGameState().getPhase().nextPhase();
        assumeTrue(game.getGameState().getPhase().getPhase().equals(Phase.ATTACK));
        game.getGameState().getPhase().nextPhase();
        assertTrue(game.getGameState().getPhase().getPhase().equals(Phase.MOVE));
    }
    
    @Test
    public void testNextPhaseDeployAgain() {
        assumeTrue(game.getGameState().getPhase().getPhase().equals(Phase.SETUP));
        game.getGameState().getPhase().nextPhase();
        assumeTrue(game.getGameState().getPhase().getPhase().equals(Phase.DEPLOY));
        game.getGameState().getPhase().nextPhase();
        assumeTrue(game.getGameState().getPhase().getPhase().equals(Phase.ATTACK));
        game.getGameState().getPhase().nextPhase();
        assumeTrue(game.getGameState().getPhase().getPhase().equals(Phase.MOVE));
        game.getGameState().getPhase().nextPhase();
        assertTrue(game.getGameState().getPhase().getPhase().equals(Phase.DEPLOY));
    }
}
