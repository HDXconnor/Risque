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
package game.logic;

import game.objects.*;
import game.objects.exceptions.CommandException;
import game.objects.exceptions.DiceException;
import game.objects.exceptions.PlayerException;
import game.objects.exceptions.TroopsException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;

public class Commands {

    private static final String LOGIN = "Login";
    private static final String LOGOUT = "Logout";
    private static final String CREATE = "Create";
    private static final String DELETE = "Delete";
    private static final String JOIN = "Join";
    private static final String QUIT = "Quit";
    private static final String KICK = "Kick";
    private static final String CLOSELOBBY = "CloseLobby";
    private static final String QUICKSTART = "QuickStart";
    private static final String ENDPHASE = "EndPhase";
    private static final String ENDTURN = "EndTurn";
    private static final String SETUP = Phase.SETUP;
    private static final String DEPLOY = Phase.DEPLOY;
    private static final String ATTACK = Phase.ATTACK;
    private static final String MOVE = Phase.MOVE;

    public static void doCommand(JSONObject json, HttpSession session, PrintWriter out)
            throws JSONException, CommandException, DiceException, TroopsException, PlayerException {

        JSONObject data = json.getJSONObject("Data");
        String command = json.getString("Command");

        // LOGIN, CREATE AND JOIN - GAME NEED NOT EXIST FOR THESE
        // Login:
        // Username = a string of the player's name
        switch (command) {
            case LOGIN: {
                String name = data.getString("Username");
                session.setAttribute("Username", name);
                session.removeAttribute("UsernameSent");
                pushAllChanges(session);
                break;
            }
            case LOGOUT:
                session.invalidate();
                GameList.pushChanges();
                break;

            // Create:
            // GameName = a string of the game's name
            case CREATE: {
                String name = (String) session.getAttribute("Username");
                String gameName = data.getString("GameName");
                String gamePassword = data.getString("GamePassword");
                Game game = new Game(gameName, gamePassword);
                game.getPlayerList().joinGame(new Player(name, session));
                GameList.add(game);
                session.setAttribute("Game", game);
                pushAllChanges(session, game, out);
                break;
            }

            // Join:
            // GameID = an int of the target game's gameid
            case JOIN: {
                if (session.getAttribute("Game") != null) {
                    throw new CommandException("Command: JOIN. You are already in game.");
                }
                String name = (String) session.getAttribute("Username");
                int gameID = data.getInt("GameID");
                String gamePassword = data.getString("GamePassword");
                Game game = GameList.getGame(gameID);
                if (gamePassword.equals(game.getPassword())) {
                    game.getPlayerList().joinGame(new Player(name, session));
                    session.setAttribute("Game", game);
                    pushAllChanges(session, game, out);
                } else {
                    throw new CommandException("Command: JOIN. Invalid password.");
                }
                break;
            }

            // QUIT, STARTGAME, ENDPHASE, ENDTURN - GAME MUST EXIST PAST THIS POINT
            // Quit:
            // No additional params required.
            case QUIT: {
                if (session.getAttribute("Game") == null) {
                    throw new CommandException("Command: QUIT. You are not in a game.");
                }
                Game game = (Game) session.getAttribute("Game");
                game.getPlayerList().removePlayer(session);
                session.removeAttribute("Game");
                if (game.getPlayerList().getNumberOfPlayers() == 0) {
                    GameList.remove(game);
                }
                game.getMessages().addGameMessage(new GameMessage("Setup", session.getAttribute("Username") + " has left the game"));
                pushAllChanges(session, game);
                break;
            }

            // Kick: TODO - ONLY HOST SHOULD BE ABLE TO KICK
            // Username = username of player to be kicked
            case KICK: {
                if (session.getAttribute("Game") == null) {
                    throw new CommandException("Command: KICK. You are not in a game.");
                }
                String name = data.getString("Username"); // name of player to be kicked, NOT YOUR OWN NAME

                Game game = (Game) session.getAttribute("Game");
                if (!game.getGameState().gameStarted()) {
                    throw new CommandException("Command: KICK. Can only kick when in lobby.");
                }
                HttpSession kickedPlayerSession = game.getPlayerList().getPlayer(name).getSession();
                game.getPlayerList().removePlayer(kickedPlayerSession);
                kickedPlayerSession.removeAttribute("Game");
                game.getMessages().addGameMessage(new GameMessage("Setup", kickedPlayerSession.getAttribute("Username") + " has been kicked from the game"));
                pushAllChanges(session, game, out);
                break;
            }

            // Closelobby:
            // No additional params required.
            // TODO - only host should be able to close the lobby
            case CLOSELOBBY: {
                if (session.getAttribute("Game") == null) {
                    throw new CommandException("Command: CLOSELOBBY. You are not in a game.");
                }
                Game game = (Game) session.getAttribute("Game");
                game.getGameState().closeLobby();
                game.getMessages().addGameMessage(new GameMessage("Setup", "The battle begins!"));
                pushAllChanges(session, game, out);
                break;
            }

            // Endphase
            // No additional params required.
            case ENDPHASE: {
                if (session.getAttribute("Game") == null) {
                    throw new CommandException("Command: ENDPHASE. You are not in a game.");
                }
                Game game = (Game) session.getAttribute("Game");
                if (!session.equals(game.getCurrentPlayerObject().getSession())) {
                    throw new CommandException("Command: ENDPHASE. Not your turn. (session mismatch)");
                }
                if ((game.getCurrentPlayerObject().getTroopsToDeploy() > 0 && game.getGameState().getPhase().getPhase().equals(Phase.DEPLOY)) || game.getBoard().getUnassigned() > 0) {
                    throw new CommandException("Command: ENDPHASE. Cannot use endphase when you still have more troops to deploy or if there are unassigned countries.");
                }
                game.endPhase();
                pushAllChanges(session, game, out);
                break;
            }

            // Endturn
            // No additional params required.
            case ENDTURN: {
                if (session.getAttribute("Game") == null) {
                    throw new CommandException("Command: ENDTURN. You are not in a game.");
                }
                Game game = (Game) session.getAttribute("Game");
                if (!session.equals(game.getCurrentPlayerObject().getSession())) {
                    throw new CommandException("Command: ENDTURN. Not your turn. (session mismatch)");
                }
                game.nextPlayer();
                pushAllChanges(session, game, out);
                break;
            }

            // GAME PHASES - SETUP, DEPLOY, ATTACK, MOVE
            // Setup:
            // CountryClicked = string country code of the country that has been selected
            case SETUP: {
                if (session.getAttribute("Game") == null) {
                    throw new CommandException("Command: SETUP. You are not in a game.");
                }
                Game game = (Game) session.getAttribute("Game");
                Board board = game.getBoard();
                Player player = game.getCurrentPlayerObject();
                if (!session.equals(player.getSession())) {
                    throw new CommandException("Command: SETUP. Not your turn. (session mismatch)");
                }
                Country selectedCountry = board.getCountry(data.getString("CountryClicked"));
                if (!selectedCountry.hasOwner()) {
                    selectedCountry.setOwner(player.getPlayerNum());
                    selectedCountry.setTroops(1);
                    game.getMessages().addGameMessage(new GameMessage("Setup", player.getName() + " claims " + selectedCountry.getName()));
                    game.endTurn();
                } else {
                    throw new CommandException("Command: SETUP. Country already has an owner!");
                }
                pushAllChanges(session, game, out);
                break;
            }

            // Deploy
            // CountryClicked - string
            case DEPLOY: {
                if (session.getAttribute("Game") == null) {
                    throw new CommandException("Command: DEPLOY. You are not in a game.");
                }
                Game game = (Game) session.getAttribute("Game");
                Board board = game.getBoard();
                Player player = game.getCurrentPlayerObject();
                if (!session.equals(player.getSession())) {
                    throw new CommandException("Command: DEPLOY. Not your turn. (session mismatch)");
                }
                if (!game.getGameState().getPhase().getPhase().equals(Phase.DEPLOY)) {
                    throw new CommandException("Command: DEPLOY. Not in deploy phase.");
                }
                // if country is owned by player, and player has troops to deploy, deploy 1x troop
                Country selectedCountry = board.getCountry(data.getString("CountryClicked"));
                if (selectedCountry.isOwnedBy(player.getPlayerNum()) && player.getTroopsToDeploy() > 0) {
                    selectedCountry.incrementTroops();
                    player.decrementTroopsToDeploy();
                    game.getMessages().addGameMessage(new GameMessage("Deploy", player.getName() + " deploys to " + selectedCountry.getName()));
                } else {
                    throw new CommandException("Command: DEPLOY. Country is not the player's, or player has no troops to deploy");
                }
                pushAllChanges(session, game, out);
                break;
            }

            // Attack
            // AttackingCountry - string
            // DefendingCountry - string
            case ATTACK: {
                if (session.getAttribute("Game") == null) {
                    throw new CommandException("Command: ATTACK. You are not in a game.");
                }
                Game game = (Game) session.getAttribute("Game");
                Board board = game.getBoard();
                Player player = game.getCurrentPlayerObject();
                if (!session.equals(player.getSession())) {
                    throw new CommandException("Command: ATTACK. Not your turn. (session mismatch)");
                }
                if (!game.getGameState().getPhase().getPhase().equals(Phase.ATTACK)) {
                    throw new CommandException("Command: ATTACK. Not in attack phase.");
                }
                // Get data from the sent JSON
                if (data.getString("AttackingCountry").equals(null) || data.getString("DefendingCountry").equals(null)) {
                    throw new CommandException("Command: ATTACK. Null data.");
                }
                String attacker = data.getString("AttackingCountry");
                String defender = data.getString("DefendingCountry");
                Country attackingCountry = board.getCountry(attacker);
                Country defendingCountry = board.getCountry(defender);
                
                Player defendingPlayer = game.getPlayerList().getPlayer(defendingCountry.getOwner());

                // the two countries must be neighbours
                if (!BoardLogic.isNeighbour(attacker, defender)) {
                    throw new CommandException("Command: ATTACK. The two selected countries are not neighbours.");
                }

                // do nothing if attacking player owns the country he is trying to attack
                if (defendingCountry.isOwnedBy(player.getPlayerNum())) {
                    throw new CommandException("Command: ATTACK. Player " + player + " cannot attack his own country.");
                }

                // do nothing if attacking player has less than 2 troops
                if (attackingCountry.getTroops() < 2) {
                    throw new CommandException("Command: ATTACK. Country needs at least 2 troops to be able to attack another country.");
                }

                // set the number of dice to be rolled
                int attackingDice = attackingCountry.getTroops();
                int defendingDice = defendingCountry.getTroops();
                if (attackingDice > 3) {
                    attackingDice = 3;
                }
                if (defendingDice > 2) {
                    defendingDice = 2;
                }

                // roll the dice
                AttackOutcome outcome = Dice.Roll(attackingDice, defendingDice);
                int troopsLostByAttacker = outcome.getTroopsLostByAttacker();
                int troopsLostByDefender = outcome.getTroopsLostByDefender();
                System.out.println(outcome);

                // country loses troops
                if (troopsLostByAttacker == attackingCountry.getTroops()) troopsLostByAttacker--;
                attackingCountry.removeTroops(troopsLostByAttacker);
                defendingCountry.removeTroops(troopsLostByDefender);
                
                // check if takeover occurred
                boolean takeoverOccured = false;
                if (defendingCountry.getTroops() == 0) {
                    takeoverOccured = true;
                    defendingCountry.setOwner(player.getPlayerNum());
                    defendingCountry.setTroops(attackingCountry.getTroops() - 1);
                    attackingCountry.setTroops(1);
                }
                
                // Win condition - if all countries owned by player, win.
                if (BoardLogic.checkWinner(player, board)) {
                    game.getGameState().setWinner(player);
                }
                
                StringBuilder message = new StringBuilder();
                
                message.append(attackingCountry.getName());
                message.append(" has attacked ");
                message.append(defendingCountry.getName());
                
                if (troopsLostByAttacker > 0) {
                    message.append(";");
                    message.append(attackingCountry.getName());
                    message.append(" lost ");
                    message.append(troopsLostByAttacker);
                    message.append(" troop");
                    if (troopsLostByAttacker > 1) {
                        message.append("s");
                    }
                }
                
                if (troopsLostByDefender > 0) {
                    message.append(";");
                    message.append(defendingPlayer.getName());
                    message.append(" lost ");
                    message.append(troopsLostByDefender);
                    message.append(" troop");
                    if (troopsLostByDefender > 1) {
                        message.append("s");
                    }
                }
                
                if (takeoverOccured) {
                    message.append(";");
                    message.append(player.getName());
                    message.append(" has taken over ");
                    message.append(defendingPlayer.getName());
                    message.append("'s country, ");
                    message.append(defendingCountry.getName());
                    message.append(".");
                }
                
                game.getMessages().addGameMessage(new GameMessage("Attack", message.toString()));
                pushAllChanges(session, game, out);
                break;
            }

            // Move
            // SourceCountry - from this country...
            // CountryClicked - to this country
            case MOVE: {
                if (session.getAttribute("Game") == null) {
                    throw new CommandException("Command: MOVE. You are not in a game.");
                }
                Game game = (Game) session.getAttribute("Game");
                Board board = game.getBoard();
                Player player = game.getCurrentPlayerObject();
                if (!session.equals(player.getSession())) {
                    throw new CommandException("Command: MOVE. Not your turn. (session mismatch)");
                }

                if (!game.getGameState().getPhase().getPhase().equals(Phase.MOVE)) {
                    throw new CommandException("Command: MOVE. Not in move phase.");
                }

                // Get data from the sent JSON
                if (data.getString("AttackingCountry").equals(null) || data.getString("DefendingCountry").equals(null)) {
                    throw new CommandException("Command: ATTACK. Null data.");
                }
                String to = data.getString("CountryClicked");
                String from = data.getString("SourceCountry");

                // player doesn't own both countries
                if (board.getCountry(from).getOwner() != player.getPlayerNum() || board.getCountry(to).getOwner() != player.getPlayerNum()) {
                    throw new CommandException("Command: MOVE. Player " + player + " does not own both countries");
                }

                // check if the two countries are neighbours
                if (BoardLogic.isNeighbour(to, from)) {
                    // move troops
                    int troops = data.getInt("Troops");
                    board.getCountry(to).setTroops(board.getCountry(to).getTroops() + troops);
                    board.getCountry(from).setTroops(board.getCountry(from).getTroops() - troops);
                    game.getMessages().addGameMessage(new GameMessage("Move", player.getName() + " moves " + troops + " from " + board.getCountry(from).getName() + " to " + board.getCountry(to).getName()));
                }
                pushAllChanges(session, game, out);
                break;
            }

            // QUICKSTART
            case QUICKSTART: {
                if (session.getAttribute("Game") == null) {
                    throw new CommandException("Command: QUICKSTART. You are not in a game.");
                }
                Game game = (Game) session.getAttribute("Game");
                Board board = game.getBoard();
                List<Country> countries = new ArrayList<>();
                countries.addAll(board.getAllCountries().values());
                Collections.shuffle(countries);
                for (Country country : countries) {
                        country.setOwner(game.getGameState().getCurrentPlayer());
                        country.setTroops(1);
                        game.nextPlayer();
                }
                game.getGameState().closeLobby();
                game.endPhase();
                pushAllChanges(session, game, out);
                break;
            }
        }
    }

    private static void pushAllChanges(HttpSession session) {
        session.removeAttribute("GameListLastModified");
        session.removeAttribute("LastModified");
        GameList.pushChanges();
    }

    private static void pushAllChanges(HttpSession session, Game game) {
        session.removeAttribute("GameListLastModified");
        session.removeAttribute("LastModified");
        GameList.pushChanges();
        game.pushChanges();
    }

    private static void pushAllChanges(HttpSession session, Game game, PrintWriter out) throws JSONException {
        session.removeAttribute("GameListLastModified");
        session.removeAttribute("LastModified");
        GameList.pushChanges();
        game.pushChanges();
        out.write(game.getGameJSON().toString());
        out.flush();
    }

}
