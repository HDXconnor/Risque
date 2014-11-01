/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.tests;

import game.objects.Game;
import game.objects.exceptions.PlayerException;
import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Simeon
 */
public class PlayerListTest {
    Game game;
    
    public PlayerListTest() {
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
    public void testNextAvailableSpot() throws JSONException {
        game.getPlayerList().getPlayersJSON();
    }
    
    @Test(expected=PlayerException.class)
    public void testPlayerExceptionWithGet() throws PlayerException {
        game.getPlayerList().getPlayer("doesn't exist");
    }
    
    @Test(expected=PlayerException.class)
    public void testPlayerExceptionWithRemove() throws PlayerException {
        game.getPlayerList().removePlayer("doesn't exist");
    }
}
