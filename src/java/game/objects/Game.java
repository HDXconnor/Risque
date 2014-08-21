/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.objects;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */

public class Game {
    private final PlayerList players;
    private final Board board;
    private final GameState gameState;
    
    public Game(PlayerList players) {
        this.players = players;
        this.board = new Board();
        this.gameState = new GameState(board); // board is a parameter here for the GameState field "Unassigned". TODO - find a better way of doing this
    }

    public PlayerList getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Board getBoard() {
        return board;
    }
    
    public boolean updated() {
        return true; //TODO
    }
    
    public JSONObject getGameJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Players", players.getPlayersJSON());
        json.put("GameState", gameState.getGameStateJSON());
        json.put("Board", board.getBoardJSON());
        return new JSONObject().put("Game", json);
    }
    
}
