/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class PlayerList {
    private HashMap<Integer, Player> players;
        
    public PlayerList(String a, String b, String c, String d, String e, String f) {
        players = new HashMap<>();
        players.put(0, new Player(a, 20));
        players.put(1, new Player(b, 20));
        players.put(2, new Player(c, 20));
        players.put(3, new Player(d, 20));
        players.put(4, new Player(e, 20));
        players.put(5, new Player(f, 20));
    }
    
    public HashMap getPlayers() {
        return players;
    }
    
    public Player getCurrentPlayer(int id) {
        return players.get(id);
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
    
}
