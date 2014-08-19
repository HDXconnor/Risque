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
    public static final int DEPLOYPHASE = 0;
    public static final int ATTACKPHASE = 1;
    public static final int REINFORCEPHASE = 2;
    public int currentPhase;
    
    public Phase() {
        this.currentPhase = DEPLOYPHASE;
    }
    
    public int getPhase() {
        return currentPhase;
    }
    
    public void nextPhase() {
        currentPhase = (currentPhase + 1) % 3;
    }
}
