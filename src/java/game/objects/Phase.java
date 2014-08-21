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
public class Phase {
    public static final String[] PHASES = {"Deploy", "Attack", "Move"};
    private int currentPhase;
    
    public Phase() {
        this.currentPhase = 0;
    }
    
    public int getPhase() {
        return currentPhase;
    }
    
    public void nextPhase() {
        currentPhase = (currentPhase + 1) % 3;
    }
}
