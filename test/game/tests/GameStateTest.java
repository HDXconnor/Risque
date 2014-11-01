/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.tests;

import game.objects.Game;
import org.json.JSONException;
import org.json.JSONObject;
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
public class GameStateTest {
    Game game;
    
    public GameStateTest() {
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
    public void testStartGame() {
        assumeTrue(!game.getGameState().gameStarted());
        game.getGameState().startGame();
        assertTrue(game.getGameState().gameStarted());
    }
    
    @Test
    public void testLobbyClosed() {
        assumeTrue(!game.getGameState().lobbyClosed());
        game.getGameState().closeLobby();
        assertTrue(game.getGameState().lobbyClosed());
    }
    
    @Test
    public void testGameStateJSON() throws JSONException {
        JSONObject json = game.getGameState().getGameStateJSON();
        json.get("CurrentPlayer");
        json.get("Turn");
        json.get("Phase");
        json.get("Unassigned");
        json.get("LobbyClosed");
        json.get("Winner");
    }
    
}
