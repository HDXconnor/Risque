package game.objects;

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

    /**
     * Returns the current phase name.
     *
     * @return the current phase string.
     */
    public String getPhase() {
        return currentPhase;
    }

    /**
     * Increments the current phase to the next one.
     */
    public void nextPhase() {
        System.out.println("in nextPhase()");
        System.out.println("current phase: "+currentPhase);
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
        System.out.println("new current phase: "+currentPhase);
    }
}