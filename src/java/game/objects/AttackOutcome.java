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
