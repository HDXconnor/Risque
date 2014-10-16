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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ConcurrentHashMap;

public class GameList {

    private static final ConcurrentHashMap<Integer, Game> games = new ConcurrentHashMap<>();
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
        cullEmptyGames();
        JSONArray arr = new JSONArray();
        for (int gameID:games.keySet()) {
            Game game = games.get(gameID);
            if (!game.getGameState().lobbyClosed()) {
                JSONObject json = new JSONObject();
                json.put("GameID", game.getGameID());
                json.put("GameName", game.getGameName());
                json.put("Players", game.getPlayerList().getNumberOfPlayers());
                json.put("PasswordProtected", game.isPasswordProtected());
                arr.put(json);
            }
        }
        return new JSONObject().put("GameList", arr);
    }
    
    private static void cullEmptyGames() {
        for (int gameID : games.keySet()) {
            if (games.get(gameID).getPlayerList().getNumberOfPlayers() == 0) {
                games.remove(gameID);
            }
        }
    }
    
    public static long getLastModified() {
        return lastModified;
    }
    
    public static void pushChanges() {
        lastModified = System.currentTimeMillis();
    }

}
