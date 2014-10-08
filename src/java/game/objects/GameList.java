/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class GameList {
    
    //private static final List<Game> games = new ArrayList<>();
    private static final Map<Integer, Game> games = new HashMap<>();
    
    public static JSONObject getAllGames() throws JSONException {
        JSONArray arr = new JSONArray();
        for (int game:games.keySet()) {
            games.get(game);
        }
        
        
        return new JSONObject().put("GameList", arr);
    }
    
}
