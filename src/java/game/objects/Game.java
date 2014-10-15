/**
 * Copyright 2014 Team Awesome Productions
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package game.objects;

import game.objects.exceptions.TroopsException;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Game {

    private static int numOfGames = 0;
    private final PlayerList playerList;
    private final Board board;
    private final GameState gameState;
    private final Phase phase;
    private long lastModified;
    private final String gameName;
    private final int gameID;
    private final String password;
    private final MessageList messages;
    private Player winner;

    public Game(String gameName, String password) {
        this.gameName = gameName;
        this.playerList = new PlayerList();
        this.board = new Board();
        this.gameState = new GameState(board);
        this.phase = gameState.getPhase();
        this.lastModified = System.currentTimeMillis();
        this.gameID = numOfGames++;
        this.password = password;
        this.messages = new MessageList();
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

    public void endPhase() throws TroopsException {
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

    public void endTurn() throws TroopsException {
        nextPlayer();
        gameState.nextTurn();
    }

    public void nextPlayer() throws TroopsException {
        gameState.setCurrentPlayer((gameState.getCurrentPlayer() + 1) % playerList.getNumberOfPlayers());
        int countriesOwned = board.getNumberOfCountriesOwned(playerList.getPlayer(gameState.getCurrentPlayer()));
        int countryBonus = (int) (Math.floor(countriesOwned / 4));
        playerList.getPlayer(gameState.getCurrentPlayer()).setNumberOfTroopsToDeploy(Math.max(3, countryBonus));

        boolean loser = true;
        HashMap<String, Country> countries = board.getAllCountries();
        for (String key : countries.keySet()) {
            if (countries.get(key).getOwner() == gameState.getCurrentPlayer() && !phase.equals(Phase.SETUP)) {
                loser = false;
            }
        }
        if (loser) {
            this.nextPlayer();
        }
    }

    public Player getCurrentPlayerObject() {
        return (Player) playerList.getPlayer(gameState.getCurrentPlayer());
    }

    public JSONObject getGameJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Players", playerList.getPlayersJSON());
        json.put("GameState", gameState.getGameStateJSON());
        json.put("Board", board.getBoardJSON());
        json.put("GameName", gameName);
        json.put("GameID", gameID);
        return new JSONObject().put("Game", json);
    }

    public long getLastModified() {
        return lastModified;
    }

    public void pushChanges() {
        this.lastModified = System.currentTimeMillis();
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
        for (String key : players.keySet()) {
            sessions.add(players.get(key).getSession());
        }
        return sessions;
    }

    public boolean isPasswordProtected() {
        return !password.equals("");
    }

    public String getPassword() {
        return password;
    }

    public MessageList getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "Game ID: " + gameID + ", Name: \"" + gameName + "\" with " + playerList.getNumberOfPlayers() + " players.";
    }
}
