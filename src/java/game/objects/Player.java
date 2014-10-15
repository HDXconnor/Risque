/**
 * Copyright 2014 Team Awesome Productions

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package game.objects;

import game.objects.exceptions.TroopsException;
import javax.servlet.http.HttpSession;

public class Player {
    private final String name;
    private final HttpSession session;
    private int troopsToDeploy;
    private int playerNum;
    private String playerImage;
    private Auth auth;

    public Player(String name, HttpSession session) {
        this.name = name;
        this.session = session;
        this.troopsToDeploy = 0;
        if (session.getAttribute("PlayerImage") != null) {
            this.playerImage = (String) session.getAttribute("PlayerImage");
        }
        if (session.getAttribute("Auth") != null) {
            this.auth = (Auth) session.getAttribute("Auth");
        }
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
    
    public String getPlayerImage() {
        return playerImage;
    }
    
    public void setPlayerImage(String playerImage) {
        this.playerImage = playerImage;
    }
    
    public Auth getAuth() {
        return auth;
    }
}