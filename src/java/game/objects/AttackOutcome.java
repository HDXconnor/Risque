/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.objects;

/**
 *
 * @author Simeon
 */
public class AttackOutcome {

    private final int troopsLostByAttacker;
    private final int troopsLostByDefender;

    public AttackOutcome(int a, int d) {
        this.troopsLostByAttacker = a;
        this.troopsLostByDefender = d;
    }

    public int getTroopsLostByAttacker() {
        return troopsLostByAttacker;
    }

    public int getTroopsLostByDefender() {
        return troopsLostByDefender;
    }

}
