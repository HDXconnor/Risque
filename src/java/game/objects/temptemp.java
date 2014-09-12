/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

import game.data.CountriesData;
import game.logic.Dice;


/**
 *
 * @author Simeon
 */
public class temptemp {
    
    public static void main(String[] args) throws Exception {
        PlayerList p = new PlayerList();
        Game g = new Game(p);
        p.joinGame(new Player("one", 123));
        p.joinGame(new Player("two", 123));
        p.joinGame(new Player("three", 123));
        p.joinGame(new Player("four", 123));
        p.joinGame(new Player("five", 123));
        p.joinGame(new Player("six", 123));
        p.joinGame(new Player("seven", 123));
        System.out.println(g.getGameJSON());
        
//        AttackOutcome ao = Dice.Roll(3, 2);
//        System.out.println("Attacker rolled " + ao.getAttackerDice() + " and lost " + ao.getTroopsLostByAttacker() + " troops.\nDefender rolled " + ao.getDefenderDice() + " and lost " + ao.getTroopsLostByDefender() + " troops.");

        


    }
}
