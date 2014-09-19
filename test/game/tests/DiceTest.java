/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.tests;

import game.logic.Dice;
import game.objects.AttackOutcome;
import game.objects.exceptions.DiceException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 * @author Simeon
 */
public class DiceTest {
    
    ArrayList<Integer> attackerDice;
    ArrayList<Integer> defenderDice;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        attackerDice = null;
        defenderDice = null;
    }
    
    @Test(expected = DiceException.class)
    public void testNumberOfDice_0_0() throws DiceException {
        Dice.Roll(0, 0);
    }
    
    @Test(expected = DiceException.class)
    public void testNumberOfDice_9_1() throws DiceException {
        Dice.Roll(9, 1);
    }
    
    @Test(expected = DiceException.class)
    public void testNumberOfDice_1_9() throws DiceException {
        Dice.Roll(1, 9);
    }
    
    @Test
    public void testDiceRolled() throws DiceException {
        int nAttackerDice = 3;
        int nDefenderDice = 2;
        AttackOutcome ao = Dice.Roll(nAttackerDice, nDefenderDice);
        assertTrue(ao.getAttackerDice().size() == nAttackerDice);
        assertTrue(ao.getDefenderDice().size() == nDefenderDice);
    }
    
    @Test
    public void testTroopLosses() throws DiceException {
        AttackOutcome ao = Dice.Roll(3, 2);
        attackerDice = ao.getAttackerDice();
        defenderDice = ao.getDefenderDice();
        int attackerLosses = 0;
        int defenderLosses = 0;
        for (int i = 0; i < 2; i++) {
            if (attackerDice.get(i) > defenderDice.get(i)) {
                defenderLosses++;
            } else {
                attackerLosses++;
            }
        }
        assertTrue(ao.getTroopsLostByAttacker() == attackerLosses);
        assertTrue(ao.getTroopsLostByDefender() == defenderLosses);
    }
    
}
