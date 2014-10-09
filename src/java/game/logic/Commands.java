package game.logic;

import game.objects.AttackOutcome;
import game.objects.Board;
import game.objects.Country;
import game.objects.Game;
import game.objects.GameList;
import game.objects.Phase;
import game.objects.Player;
import game.objects.exceptions.*;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class Commands {

    private static final String LOGIN = "Login";
    private static final String LOGOUT = "Logout";
    private static final String CREATE = "Create";
    private static final String JOIN = "Join";
    private static final String QUIT = "Quit";
    private static final String KICK = "Kick";
    private static final String CLOSELOBBY = "CloseLobby";
    private static final String DEBUG = "Debug";
    private static final String ENDPHASE = "EndPhase";
    private static final String ENDTURN = "EndTurn";
    private static final String SETUP = Phase.SETUP;
    private static final String DEPLOY = Phase.DEPLOY;
    private static final String ATTACK = Phase.ATTACK;
    private static final String MOVE = Phase.MOVE;
    
    
    public static void doCommand(JSONObject json, HttpSession session)
            throws JSONException, CommandException, DiceException, TroopsException, PlayerException {
        
        JSONObject data = json.getJSONObject("Data");
        String command = json.getString("Command");
        
        // LOGIN, CREATE AND JOIN - GAME NEED NOT EXIST FOR THESE
        
        // Login:
        // Username = a string of the player's name
        if (command.equals(LOGIN)) {
            String name = data.getString("Username");
            session.setAttribute("Username", name);
            session.removeAttribute("UsernameSent");
        }
    
        else if (command.equals(LOGOUT)) {
            session.invalidate();
        }
        
        // Create:
        // GameName = a string of the game's name
        else if (command.equals(CREATE)) {
            String name = (String) session.getAttribute("Username");
            String gameName = data.getString("GameName");
            Game game = new Game(gameName);
            game.getPlayerList().joinGame(new Player(name, session));
            GameList.add(game);
            session.setAttribute("Game", game);
            session.removeAttribute("gameListLastModified");
            session.removeAttribute("lastModified");
            game.pushChanges();
        }
        
        // Join:
        // GameID = an int of the target game's gameid
        else if (command.equals(JOIN)) {
            if (session.getAttribute("Game") != null) {
                throw new CommandException("Command: JOIN. You are already in game.");
            }
            String name = (String) session.getAttribute("Username");
            int gameID = data.getInt("GameID");
            Game game = GameList.getGame(gameID);
            game.getPlayerList().joinGame(new Player(name, session));
            session.setAttribute("Game", game);
            session.removeAttribute("gameListLastModified");
            session.removeAttribute("lastModified");
            game.pushChanges();
        }
        
        
        // QUIT, STARTGAME, ENDPHASE, ENDTURN - GAME MUST EXIST PAST THIS POINT

        // Quit:
        // No additional params required.
        else if (command.equals(QUIT)) {
            if (session.getAttribute("Game") == null) {
                throw new CommandException("Command: QUIT. You are not in a game.");
            }
            Game game = (Game) session.getAttribute("Game");
            game.getPlayerList().removePlayer(session);
            session.removeAttribute("Game");
            session.removeAttribute("gameListLastModified");
            session.removeAttribute("lastModified");
            game.pushChanges();
        }
        
        // Kick: TODO - ONLY HOST SHOULD BE ABLE TO KICK
        // Username = username of player to be kicked
        else if (command.equals(KICK)) {
            if (session.getAttribute("Game") == null) {
                throw new CommandException("Command: KICK. You are not in a game.");
            }
            String name = data.getString("Username"); // name of player to be kicked, NOT YOUR OWN NAME
            Game game = (Game) session.getAttribute("Game");
            HttpSession kickedPlayerSession = game.getPlayerList().getPlayerByName(name).getSession();
            game.getPlayerList().removePlayer(kickedPlayerSession);
            kickedPlayerSession.removeAttribute("Game");
            game.pushChanges();
        }
        
        // Closelobby:
        // No additional params required.
        // TODO - only host should be able to close the lobby
        else if (command.equals(CLOSELOBBY)) {
            if (session.getAttribute("Game") == null) {
                throw new CommandException("Command: CLOSELOBBY. You are not in a game.");
            }
            Game game = (Game) session.getAttribute("Game");
            game.getGameState().closeLobby();
            game.pushChanges();
        }
        
        // Endphase
        // No additional params required.
        else if (command.equals(ENDPHASE)) {
            if (session.getAttribute("Game") == null) {
                throw new CommandException("Command: ENDPHASE. You are not in a game.");
            }
            Game game = (Game) session.getAttribute("Game");
            if (!session.equals(game.getCurrentPlayerObject().getSession())) {
                throw new CommandException("Command: ENDPHASE. Not your turn. (session mismatch)");
            }
            game.endPhase();
            game.pushChanges();
        }
        
        // Endturn
        // No additional params required.
        else if (command.equals(ENDTURN)) {
            if (session.getAttribute("Game") == null) {
                throw new CommandException("Command: ENDTURN. You are not in a game.");
            }
            Game game = (Game) session.getAttribute("Game");
            if (!session.equals(game.getCurrentPlayerObject().getSession())) {
                throw new CommandException("Command: ENDTURN. Not your turn. (session mismatch)");
            }
            game.nextPlayer();
            game.pushChanges();
        }
        
        // GAME PHASES - SETUP, DEPLOY, ATTACK, MOVE
        
        // Setup:
        // CountryClicked = string country code of the country that has been selected
        else if (command.equals(SETUP)) {
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
            if (selectedCountry.hasOwner()) {
                selectedCountry.setOwner(player.getPlayerNum());
                selectedCountry.setTroops(1);
                game.nextPlayer();
            } else {
                throw new CommandException("Command: SETUP. Country already has an owner!");
            }
            game.pushChanges();
        }
        
        // Deploy
        // CountryClicked - string
        else if (command.equals(DEPLOY)) {
            if (session.getAttribute("Game") == null) {
                throw new CommandException("Command: DEPLOY. You are not in a game.");
            }
            Game game = (Game) session.getAttribute("Game");
            Board board = game.getBoard();
            Player player = game.getCurrentPlayerObject();
            if (!session.equals(player.getSession())) {
                throw new CommandException("Command: DEPLOY. Not your turn. (session mismatch)");
            }
            // if country is owned by player, and player has troops to deploy, deploy 1x troop
            Country selectedCountry = board.getCountry(data.getString("CountryClicked"));
            if (selectedCountry.isOwnedBy(player.getPlayerNum()) && player.getTroopsToDeploy() > 0) {
                selectedCountry.incrementTroops();
                player.decrementTroopsToDeploy();
            } else {
                throw new CommandException("Command: DEPLOY. Country is not the player's, or player has no troops to deploy");
            }
            game.pushChanges();
        }
        
        // Attack
        // AttackingCountry - string
        // DefendingCountry - string
        else if (command.equals(ATTACK)) {
            if (session.getAttribute("Game") == null) {
                throw new CommandException("Command: ATTACK. You are not in a game.");
            }
            Game game = (Game) session.getAttribute("Game");
            Board board = game.getBoard();
            Player player = game.getCurrentPlayerObject();
            if (!session.equals(player.getSession())) {
                throw new CommandException("Command: ATTACK. Not your turn. (session mismatch)");
            }

            // Get data from the sent JSON
            String attacker = data.getString("AttackingCountry");
            String defender = data.getString("DefendingCountry");
            Country attackingCountry = board.getCountry(attacker);
            Country defendingCountry = board.getCountry(defender);

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
            System.out.println(outcome);

            // country loses troops
            attackingCountry.removeTroops(outcome.getTroopsLostByAttacker());
            defendingCountry.removeTroops(outcome.getTroopsLostByDefender());

            // check if takeover occurred
            if (defendingCountry.getTroops() == 0) {
                defendingCountry.setOwner(player.getPlayerNum());
                defendingCountry.setTroops(attackingCountry.getTroops() - 1);
                attackingCountry.setTroops(1);
            }
            game.pushChanges();
        }
        
        // Move
        // SourceCountry - from this country...
        // CountryClicked - to this country
        else if (command.equals(MOVE)) {
            if (session.getAttribute("Game") == null) {
                throw new CommandException("Command: MOVE. You are not in a game.");
            }
            Game game = (Game) session.getAttribute("Game");
            Board board = game.getBoard();
            Player player = game.getCurrentPlayerObject();
            if (!session.equals(player.getSession())) {
                throw new CommandException("Command: MOVE. Not your turn. (session mismatch)");
            }
            
            // Get data from the sent JSON
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
            }
            game.pushChanges();
        }
        
        // DEBUG
        else if (command.equals(DEBUG)) {
            if (session.getAttribute("Game") == null)
                throw new CommandException("Command: DEBUG. You are not in a game.");
            Game game = (Game) session.getAttribute("Game");
            Board board = game.getBoard();
            for (Object country : board.getAllCountries().keySet()) {
                board.getCountry((String) country).setOwner(game.getGameState().getCurrentPlayer());
                board.getCountry((String) country).setTroops(1);
                game.nextPlayer();
            }
            game.pushChanges();
        }
    }

}
