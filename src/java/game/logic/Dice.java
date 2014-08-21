/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.logic;

import game.objects.AttackOutcome;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 *
 * @author Simeon
 */
public class Dice {

    private static final Random rng = new Random();

    public static AttackOutcome Roll(int numberOfAttackerDice, int numberOfDefenderDice) { 
        int attackerTroopsLost = 0;
        int defenderTroopsLost = 0;
        ArrayList<Integer> attackerDice = new ArrayList();
        ArrayList<Integer> defenderDice = new ArrayList();
        
        for (int i = 0; i < numberOfAttackerDice; i++) {
            attackerDice.add(rng.nextInt(6) + 1);
        }
        
        for (int i = 0; i < numberOfDefenderDice; i++) {
            defenderDice.add(rng.nextInt(6) + 1);
        }
        
        Collections.sort(attackerDice, Collections.reverseOrder());
        Collections.sort(defenderDice, Collections.reverseOrder());

        System.out.println("Attacker dice rolls: " + attackerDice);
        System.out.println("Defender dice rolls: " + defenderDice);
        
        int checks = Math.min(attackerDice.size(), defenderDice.size());
        
        for (int i = 0; i < checks; i++) {
            if (attackerDice.get(i) > defenderDice.get(i)) {
                defenderTroopsLost++;
            } else {
                attackerTroopsLost++;
            }
        }
        
        System.out.println("Outcome: Attacker lost " + attackerTroopsLost + ", defender lost " + defenderTroopsLost);

        return new AttackOutcome(attackerTroopsLost, defenderTroopsLost);
    }
    
}
