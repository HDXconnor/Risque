/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.tests;

import game.data.CountriesData;
import game.objects.Game;
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
public class CountryTest {
    Game game;
    private static final String testCountryId = "NA01";
    
    public CountryTest() {
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
    public void testDefaultOwner() {
        assertTrue(game.getBoard().getCountry(testCountryId).getOwner() == -1);
    }
    
    @Test
    public void testSetOwner() {
        assumeTrue(game.getBoard().getCountry(testCountryId).getOwner() == -1);
        game.getBoard().getCountry(testCountryId).setOwner(1);
        assertTrue(game.getBoard().getCountry(testCountryId).getOwner() == 1);
    }
    
    @Test
    public void testName() {
        assertTrue(game.getBoard().getCountry(testCountryId).getName().equals(CountriesData.countriesMap.get(testCountryId)));
    }
    
    @Test
    public void testIncrementTroops() {
        assumeTrue(game.getBoard().getCountry(testCountryId).getTroops() == 0);
        game.getBoard().getCountry(testCountryId).incrementTroops();
        assertTrue(game.getBoard().getCountry(testCountryId).getTroops() == 1);
    }
    
    @Test
    public void testHasOwner() {
        assumeTrue(!game.getBoard().getCountry(testCountryId).hasOwner());
        game.getBoard().getCountry(testCountryId).setOwner(1);
        assertTrue(game.getBoard().getCountry(testCountryId).hasOwner());
    }
    
    @Test
    public void testIsOwnedBy() {
        game.getBoard().getCountry(testCountryId).setOwner(1);
        assertTrue(game.getBoard().getCountry(testCountryId).isOwnedBy(1));
    }
    
    @Test
    public void testRemoveTroops() {
        game.getBoard().getCountry(testCountryId).setTroops(100);
        assumeTrue(game.getBoard().getCountry(testCountryId).getTroops() == 100);
        game.getBoard().getCountry(testCountryId).removeTroops(15);
        assertTrue(game.getBoard().getCountry(testCountryId).getTroops() == 85);
    }
}
