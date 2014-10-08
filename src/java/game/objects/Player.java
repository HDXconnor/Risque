package game.objects;

import game.objects.exceptions.TroopsException;
import javax.servlet.http.HttpSession;

public class Player {
    private final String name;
    private int troopsToDeploy;
    private int playerNum;
    public Oauth auth;
    public HttpSession session;

    public Player(String name, HttpSession session) {
        this.name = name;
        this.session = session;
        this.auth = new Oauth(name);
        this.troopsToDeploy = 3;
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

    public int getPlayerNum() {
        return playerNum;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }
    
    public HttpSession getSession() {
        return session;
    }
}