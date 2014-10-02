package game.objects;

import game.objects.exceptions.TroopsException;

public class Player {
    private final String name;
    private final String playerImage;
    private int troopsToDeploy;
    private int playerNum;

    public Player(String name, int troopsToDeploy) {
        this.name = name;
        this.troopsToDeploy = troopsToDeploy;
        this.playerImage = "image.jpg";
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

    public int getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }
}