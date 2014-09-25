package game.objects;

import game.objects.exceptions.TroopsException;
/**
 *
 * @author Simeon
 */
public class Player {
    private final String name;
    private final String playerImage;
    private int troopsToDeploy;
    
    public Player(String name, int troopsToDeploy) {
        this.name = name;
        this.playerImage = "image.jpg";
        this.troopsToDeploy = 0;
    }

    public int getTroopsToDeploy() {
        return troopsToDeploy;
    }

    public void setNumberOfTroopsToDeploy(int troopsToDeploy) throws TroopsException {
        if (troopsToDeploy < 0) throw new TroopsException("Cannot have negative number of troops.");
        this.troopsToDeploy = troopsToDeploy;
    }
    
    public void decrementTroopsToDeploy() throws TroopsException {
        if (this.troopsToDeploy <= 0) throw new TroopsException("Cannot have negative number of troops.");
        this.troopsToDeploy--;
    }

    public String getName() {
        return name;
    }
    
    public String getPlayerImage() {
        return playerImage;
    } 
}