/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Simeon
 */
public class Phase {

    private String currentPhase;
    public static final String SETUP = "Setup";
    public static final String DEPLOY = "Deploy";
    public static final String ATTACK = "Attack";
    public static final String MOVE = "Move";
    
    public Phase() {
        this.currentPhase = SETUP;
    }
    
    public String getPhase() {
        return currentPhase;
    }
    
    public void nextPhase() {
        switch (currentPhase) {
            case SETUP:
                this.currentPhase = DEPLOY;
                break;
            case DEPLOY:
                this.currentPhase = ATTACK;
                break;
            case ATTACK:
                this.currentPhase = MOVE;
                break;
            case MOVE:
                this.currentPhase = DEPLOY;
                break;
            default:
                break;
        }
    }
    
    
    
}
