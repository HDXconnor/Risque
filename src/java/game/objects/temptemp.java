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
        PlayerList p = new PlayerList("a", "b", "c", "d", "e", "f");
        Game g = new Game(p);
        for (int i=0; i < 20; i++) {
            System.out.println(g.getGameJSON());
            g.endPhase();
        }
        
//        AttackOutcome ao = Dice.Roll(3, 2);
//        System.out.println("Attacker rolled " + ao.getAttackerDice() + " and lost " + ao.getTroopsLostByAttacker() + " troops.\nDefender rolled " + ao.getDefenderDice() + " and lost " + ao.getTroopsLostByDefender() + " troops.");

        


    }
}
