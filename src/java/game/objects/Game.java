package game.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;

public class Game {
    private static int numOfGames = 0;
    private final PlayerList playerList;
    private final Board board;
    private final GameState gameState;
    private final Phase phase;
    private long lastModified;
    private final String gameName;
    private final int gameID;

    public Game(String gameName) {
        this.gameName = gameName;
        this.playerList = new PlayerList();
        this.board = new Board();
        this.gameState = new GameState(board);
        this.phase = gameState.getPhase();
        this.lastModified = System.currentTimeMillis();
        this.gameID = numOfGames++;
    }

    public PlayerList getPlayerList() {
        return playerList;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Board getBoard() {
        return board;
    }

    public void endPhase() {
        switch (phase.getPhase()) {
            case Phase.SETUP:
                gameState.startGame();
                break;
            case Phase.MOVE:
                endTurn();
                break;
            default:
                break;
        }
        phase.nextPhase();
    }

    public void endTurn() {
        nextPlayer();
        gameState.nextTurn();
    }

    public void nextPlayer () {
        gameState.setCurrentPlayer((gameState.getCurrentPlayer() + 1) % playerList.getNumberOfPlayers());
    }

    public Player getCurrentPlayerObject() {
        return (Player) playerList.getPlayerById(gameState.getCurrentPlayer());
    }

    public JSONObject getGameJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Players", playerList.getPlayersJSON());
        json.put("GameState", gameState.getGameStateJSON());
        json.put("Board", board.getBoardJSON());
        return new JSONObject().put("Game", json);
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified() {
        this.lastModified = System.currentTimeMillis();
    }
    
    public void pushChanges() {
        setLastModified(); // just for readability
    }
    
    public String getGameName() {
        return gameName;
    }
    
    public int getGameID() {
        return gameID;
    }
    
    public Set<HttpSession> getSessions() {
        Set<HttpSession> sessions = new HashSet<>();
        Map<String, Player> players = playerList.getPlayerHashMap();
        for (String key:players.keySet()) {
            sessions.add(players.get(key).getSession());
        }
        return sessions;
    }
    
    @Override
    public String toString() {
        return "Game ID: " + gameID + ", Name: \"" + gameName + "\" with " + playerList.getNumberOfPlayers() + " players.";
    }
}