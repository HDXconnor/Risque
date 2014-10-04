package game.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class Game {
    private final PlayerList playerList;
    private final Board board;
    private final GameState gameState;
    private final Phase phase;
    private long lastModified;

    public Game(PlayerList players) {
        this.playerList = players;
        this.board = new Board();
        this.gameState = new GameState(board); // board is a parameter here for the GameState field "Unassigned". TODO - find a better way of doing this
        this.phase = gameState.getPhase();
        this.lastModified = System.currentTimeMillis();
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
        return (Player) playerList.getPlayers().get(gameState.getCurrentPlayer());
    }

    /**
     * Removes a specified player from the game.
     *
     * @param playerName    name of user to be removed
     * @see game.objects.PlayerList
     */
    public void removePlayer(String playerName) {
        if (playerList.getNumberOfPlayers() > 0) {
            playerList.removePlayer(playerName);
        }
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
}