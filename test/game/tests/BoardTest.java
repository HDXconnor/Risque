/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.tests;

import game.data.CountriesData;
import game.objects.Country;
import game.objects.Game;
import game.objects.Player;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Session;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StandardSessionFacade;
import org.json.JSONArray;
import org.json.JSONException;
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
public class BoardTest {
    Game game;
    private static final String testCountryId = "NA01";
    
    public BoardTest() {
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
    public void testNumCountries() {
        assertTrue(game.getBoard().getAllCountries().size() == CountriesData.countriesMap.size());
    }
    
    @Test
    public void testGetCountry() {
        Country country = game.getBoard().getCountry(testCountryId);
        assertTrue(country.getName().equals(CountriesData.countriesMap.get(testCountryId)));
    }
    
    @Test
    public void testUnassigned() {
        assumeTrue(game.getBoard().getUnassigned() == CountriesData.countriesMap.size());
        game.getBoard().getCountry(testCountryId).setOwner(1);
        assertTrue(game.getBoard().getUnassigned() == CountriesData.countriesMap.size() - 1);
    }
    
    @Test
    public void testBoardJSON() throws JSONException {
        JSONArray arr = game.getBoard().getBoardJSON();
        assertTrue(arr.length() == CountriesData.countriesMap.size());
    }
}
