/**
 * Copyright 2014 Team Awesome Productions

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
package game.objects;

import java.util.ArrayList;

public class AttackOutcome {

    private final int troopsLostByAttacker;
    private final int troopsLostByDefender;
    private final ArrayList<Integer> attackerDice;
    private final ArrayList<Integer> defenderDice;

    public AttackOutcome(int troopsLostByAttacker, int troopsLostByDefender, ArrayList<Integer> attackerDice, ArrayList<Integer> defenderDice) {
        this.troopsLostByAttacker = troopsLostByAttacker;
        this.troopsLostByDefender = troopsLostByDefender;
        this.attackerDice = attackerDice;
        this.defenderDice = defenderDice;
    }

    public int getTroopsLostByAttacker() {
        return troopsLostByAttacker;
    }

    public int getTroopsLostByDefender() {
        return troopsLostByDefender;
    }

    public ArrayList<Integer> getAttackerDice() {
        return attackerDice;
    }

    public ArrayList<Integer> getDefenderDice() {
        return defenderDice;
    }
    
    @Override
    public String toString() {
        return "ATTACKOUTCOME: Attacker rolled: " + attackerDice + ", Defender Rolled: " + defenderDice + ", Attacker lost " + troopsLostByAttacker + " troops, Defender lost " + troopsLostByDefender + " troops.";
    }


}
