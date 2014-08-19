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
    private final GameState gameState;
    private final Board board;
    
    public Game(PlayerList players) {
        this.players = players;
        this.gameState = new GameState();
        this.board = new Board();
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
    
    public JSONObject getGameJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Players", players.getPlayersJSON());
        json.put("GameState", gameState.getGameStateJSON());
        json.put("Board", board.getBoardJSON());
        return new JSONObject().put("Game", json);
    }
    
}
