/**
 * Copyright 2014 Connor Anderson

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

import game.objects.exceptions.PlayerException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpSession;

public class PlayerList {
    private final HashMap<Integer, Player> players;
    
    public PlayerList() {
        players = new HashMap<>();
    }
    
    public void joinGame(Player newPlayer) throws PlayerException {
        if (players.size() >= 6) {
            throw new PlayerException("Games are limited to 6 players!");
        }
        int nextAvailableSpot = getNextAvailableSpot();
        if (nextAvailableSpot == -1) {
            throw new PlayerException("Invalid player number!");
        }
        for (Player player:players.values()) {
            if (player.getSession().equals(newPlayer.getSession())) {
                throw new PlayerException("Player session is already in the game");
            }
        }
        players.put(nextAvailableSpot, newPlayer);
        newPlayer.setPlayerNum(nextAvailableSpot);
    }

    /**
     * Finds the next spot in the players list.
     *
     * @return  a number indicating an available
     *          spot in the list.
     */
    private int getNextAvailableSpot() {
        for (int i = 0; i < 6; i++) {
            if (players.get(i) == null) {
                return i;
            }
        }
        return -1;
    }
    
    public HashMap getPlayerHashMap() {
        return players;
    }

    public Player getPlayer(int player) {
        return players.get(player);
    }

    /**
     *
     *
     * @param player
     * @return
     * @throws PlayerException
     */
    public Player getPlayer(String player) throws PlayerException {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(player)) {
                return players.get(i);
            }
        }
        throw new PlayerException("Player not found");
    }
    
    public Player getPlayer(HttpSession session) throws PlayerException {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getSession().equals(session)) {
                return players.get(i);
            }
        }
        throw new PlayerException("Player not found");
    }

    /**
     * Removes a specified player from the game.
     *
     * @param player    player name.
     */
    public void removePlayer(String player) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(player)) {
                players.remove(i);
                return;
            }
        }
    }
    
    public void removePlayer(HttpSession session) throws PlayerException {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getSession().equals(session)) {
                players.remove(i);
                return;
            }
        }
        throw new PlayerException("Player not found");
    }
    
    public JSONArray getPlayersJSON() throws JSONException {
        JSONArray arr = new JSONArray();
        for (int key:players.keySet()) {
            JSONObject json = new JSONObject();
            Player player = players.get(key);
            json.put("PlayerID", "TEMP_ID_00"+(key+1));
            json.put("DisplayName", player.getName());
            json.put("PlayerOrder", key);
            if (player.getPlayerImage() != null) {
                json.put("PlayerImage", player.getPlayerImage());
            }
            json.put("TroopsToDeploy", player.getTroopsToDeploy());
            arr.put(json);
        }
        return arr;
    }

    /**
     * Returns the number of players in the game.
     *
     * @return  the number of players.
     */
    public int getNumberOfPlayers() {
        return players.size();
    }
    
    public List<HttpSession> getSessions() {
        List<HttpSession> sessions = new ArrayList<>();
        for (Player player:players.values()) {
            sessions.add(player.getSession());
        }
        return sessions;
    }
    
}