/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.objects;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class GameList {

    private static final Map<Integer, Game> games = new HashMap<>();
    private static long lastModified = System.currentTimeMillis();
    
    public static void add(Game game) {
        games.put(game.getGameID(), game);
    }
    
    public static void remove(Game game) {
        games.remove(game.getGameID());
    }
    
    public static Game getGame(int gameID) {
        return games.get(gameID);
    }
    
    public static JSONObject getGameListJSON() throws JSONException {
        JSONArray arr = new JSONArray();
        for (int gameID:games.keySet()) {
            Game game = games.get(gameID);
            JSONObject json = new JSONObject();
            json.put("GameID", game.getGameID());
            json.put("GameName", game.getGameName());
            json.put("Players", game.getPlayerList().getNumberOfPlayers());
            arr.put(json);
        }
        return new JSONObject().put("GameList", arr);
    }
    
    public static long getLastModified() {
        return lastModified;
    }

    public static void setLastModified() {
        lastModified = System.currentTimeMillis();
    }
    
    public static void pushChanges() {
        setLastModified();
    }

}
