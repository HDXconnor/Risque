package game.objects;

import java.util.ArrayList;
import java.util.Arrays;

public class Phase {
    private String currentPhase;
    public static final String SETUP = "Setup";
    public static final String DEPLOY = "Deploy";
    public static final String ATTACK = "Attack";
    public static final String MOVE = "Move";
    public static final String ENDPHASE = "EndPhase";
    
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