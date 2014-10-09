/**
 * Copyright 2014 Connor Anderson

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package game.logic;

import game.objects.AttackOutcome;
import game.objects.exceptions.DiceException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Dice {
    private static final Random rng = new Random();

    public static AttackOutcome Roll(int numberOfAttackerDice, int numberOfDefenderDice) throws DiceException {
        // Attacker must have 1, 2, or 3 dice. Defender must have 1 or 2 dice.
        if (numberOfAttackerDice < 1 || numberOfAttackerDice > 3 || numberOfDefenderDice < 1 || numberOfDefenderDice > 2) throw new DiceException("Invalid number of dice: nAttDice=" + numberOfAttackerDice + ", nDefDice=" + numberOfDefenderDice);
        
        int troopsLostByAttacker = 0;
        int troopsLostByDefender = 0;
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
        
        int checks = Math.min(attackerDice.size(), defenderDice.size());
        
        for (int i = 0; i < checks; i++) {
            if (attackerDice.get(i) > defenderDice.get(i)) {
                troopsLostByDefender++;
            } else {
                troopsLostByAttacker++;
            }
        }
        return new AttackOutcome(troopsLostByAttacker, troopsLostByDefender, attackerDice, defenderDice);
    }
}
