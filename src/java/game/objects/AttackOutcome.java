/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.objects;

import java.util.ArrayList;

/**
 *
 * @author Simeon
 */
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

}
