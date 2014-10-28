/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.tests;

import game.objects.Game;
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
public class GameTest {
    Game game1;
    Game game2;
    
    public GameTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        game1 = new Game("testgame1", "testpassword1");
        game2 = new Game("testgame2", "");
    }
    
    @After
    public void tearDown() {
        game1 = null;
        game2 = null;
    }

    @Test
    public void testGameName() {
        String gameName = game1.getGameName();
        assertTrue(gameName.equals("testgame1"));
    }
    
    @Test
    public void testPasswordProtected1() {
        assertTrue(game1.isPasswordProtected());
    }
    
    @Test
    public void testPasswordProtected2() {
        assertTrue(!game2.isPasswordProtected());
    }
    
    @Test
    public void testPassword() {
        assertTrue(game1.getPassword().equals("testpassword1"));
    }
    
}
