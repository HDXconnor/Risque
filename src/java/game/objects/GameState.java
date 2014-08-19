/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class GameState {
    
    private boolean gameStarted;
    private int currentPlayer;
    private int turn;
    private final Phase phase;
    
    public GameState() {
        this.gameStarted = false;
        this.currentPlayer = 0;
        this.turn = 0;
        this.phase = new Phase();
    }
    
    public void startGame() {
        this.gameStarted = true;
    }
    
    public boolean gameStarted() {
        return gameStarted;
    }
    
    public void nextTurn() {
        this.turn++;
    }
    
    public void nextPlayer() {
        this.currentPlayer = (this.currentPlayer + 1) % 6;
    }
    
    public JSONObject getGameStateJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("CurrentPlayer", currentPlayer);
        json.put("Turn", turn);
        json.put("Phase", "dummylol");
        return json;
    }

}
