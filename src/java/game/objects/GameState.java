package game.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class GameState {    
    private boolean gameStarted;
    private boolean lobbyClosed;
    private int currentPlayer;
    private int turn;
    private final Phase phase;
    private final Board board;
    
    public GameState(Board board) {
        this.gameStarted = false;
        this.lobbyClosed = false;
        this.currentPlayer = 0;
        this.turn = 0;
        this.phase = new Phase();
        this.board = board;
    }
    
    public void startGame() {
        this.gameStarted = true;
    }
    
    public boolean gameStarted() {
        return gameStarted;
    }
    
    public void closeLobby() {
        this.lobbyClosed = true;
    }
    
    public boolean lobbyClosed() {
        return lobbyClosed;
    }
    
    public JSONObject getGameStateJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("CurrentPlayer", getCurrentPlayer());
        json.put("Turn", turn);
        if (gameStarted) json.put("Phase", phase.getPhase());
        else json.put("Phase", Phase.SETUP);
        json.put("Unassigned", board.getUnassigned());
        json.put("LobbyClosed", lobbyClosed());
        return json;
    }

    public Phase getPhase() {return phase;}

    public int getCurrentPlayer() {return currentPlayer;}
    
    public void setCurrentPlayer(int player) {currentPlayer = player;}
    
    public void nextTurn() {turn++;}
}