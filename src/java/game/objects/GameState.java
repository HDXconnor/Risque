package game.objects;

import game.objects.exceptions.CommandException;
import org.json.JSONException;
import org.json.JSONObject;

public class GameState {    
    private boolean gameStarted, lobbyClosed;
    private int currentPlayer, turn;
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

    /**
     * Starts the game.
     */
    public void startGame() {
        this.gameStarted = true;
    }

    /**
     * Returns whether or not the game has been started.
     *
     * @return  <code>true</code> if the game has started;
     *          otherwise <code>false</code>
     * @see     GameState#startGame() startGame
     */
    public boolean gameStarted() {
        return gameStarted;
    }

    /**
     * Removes the lobby from view.
     */
    public void closeLobby() {
        this.lobbyClosed = true;
    }

    /**
     * Returns whether or not the game lobby has been closed.
     *
     * @return  <code>true</code> if the lobby has closed;
     *          otherwise <code>false</code>.
     * @see     GameState#closeLobby() closeLobby
     */
    public boolean lobbyClosed() {
        return lobbyClosed;
    }

    /**
     * Generates and returns a JSON object containing all of
     * the current game state information. The keys include:
     * <ul>
     * <li>CurrentPlayer</li>
     * <li>Turn</li>
     * </ul>
     *
     * @return  the current game state as JSON.
     * @throws JSONException
     */
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

    /**
     * Returns the current phase that the game is in.
     *
     * @return  the string name of the current phase.
     */
    public Phase getPhase() {return phase;}

    /**
     * Returns the player number corresponding to who's turn it currently is.
     *
     * @return          player number corresponding to who's turn it currently is
     */
    public int getCurrentPlayer() {return currentPlayer;}

    /**
     * Check to see if it is a certain player's turn.
     *
     * @param player    player number
     * @return          <code>true</code> if it is
     *                  the player's turn; otherwise
     *                  throws <code>CommandException</code>.
     * @throws CommandException
     *                  if player attempts to issue a command
     *                  and it's not their turn.
     */
    public boolean isCurrentPlayer(int player) throws CommandException {
        if(currentPlayer == player){
            return true;
        }
        else {
            throw new CommandException(
                    "Player " +player+ " sent a command during " +currentPlayer+ "'s turn.");
        }
    }

    /**
     * Makes the turn belong to a specific player.
     *
     * @param player    player number
     */
    public void setCurrentPlayer(int player) {currentPlayer = player;}

    /**
     * Passes turn to next player.
     */
    public void nextTurn() {turn++;}
}