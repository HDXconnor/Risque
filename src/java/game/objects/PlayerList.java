package game.objects;

import game.objects.exceptions.PlayerException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PlayerList {
    private final HashMap<Integer, Player> players;
    
    public PlayerList() {
        players = new HashMap<>();
    }
    
    public void joinGame(Player newPlayer) throws PlayerException {
        if (players.size() >= 6) throw new PlayerException("Games are limited to 6 players!");
        int nextAvailableSpot = getNextAvailableSpot();
        if (nextAvailableSpot == -1) throw new PlayerException("Invalid player number!");
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
        for (int i=0; i<6; i++) {
            if (players.get(i) == null) return i;
        }
        return -1;
    }
    
    public HashMap getPlayers() {return players;}

    /**
     *
     *
     * @param player
     * @return
     * @throws PlayerException
     */
    public Player getPlayerByName(String player) throws PlayerException {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(player)) {
                return players.get(i);
            }
        }
        throw new PlayerException("Player not found");
    }

    public Player getPlayerByName(int player) throws PlayerException {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerNum() == player) {
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
    
    public JSONArray getPlayersJSON() throws JSONException {
        JSONArray arr = new JSONArray();
        for (int key:players.keySet()) {
            JSONObject json = new JSONObject();
            json.put("PlayerID", "TEMP_ID_00"+(key+1));
            json.put("DisplayName", players.get(key).getName());
            json.put("PlayerOrder", key);
            json.put("TroopsToDeploy", players.get(key).getTroopsToDeploy());
            json.put("PlayerImage", players.get(key).getPlayerImage());
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
}