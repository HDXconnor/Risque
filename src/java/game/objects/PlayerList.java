package game.objects;

import game.objects.exceptions.PlayerException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import javax.servlet.http.HttpSession;

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

    public Player getPlayerById(int player) {
        return players.get(player);
    }

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
    
    public Player getPlayerBySession(HttpSession session) throws PlayerException {
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
//    public void removePlayer(String player) {
//        for (int i = 0; i < players.size(); i++) {
//            if (players.get(i).getName().equals(player)) {
//                players.remove(i);
//                return;
//            }
//        }
//    }
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
            json.put("PlayerID", "TEMP_ID_00"+(key+1));
            json.put("DisplayName", players.get(key).getName());
            json.put("PlayerOrder", key);
            json.put("TroopsToDeploy", players.get(key).getTroopsToDeploy());
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