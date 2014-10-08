package game.logic;

import game.objects.Board;
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
    private static final String CREATE = "Create";
    private static final String JOIN = "Join";
    private static final String QUIT = "Quit";
    private static final String KICK = "Kick"; // nyi
    //private static final String STARTGAME = "StartGame";
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
        }
        
        // Create:
        // Username = a string of the player's name - TODO USE SESSION ATTRIBUTE USERNAME
        // GameName = a string of the game's name
        if (command.equals(CREATE)) {
            String name = data.getString("Username"); // = session.getAttribute("Username");
            String gameName = data.getString("GameName");
            Game game = new Game(gameName);
            game.getPlayerList().joinGame(new Player(name, session));
            session.setAttribute("Game", game);
            GameList.add(game);
            game.pushChanges();
        }
        
        // Join:
        // Username = a string of the player's name - TODO USE SESSION ATTRIBUTE USERNAME
        // GameID = an int of the target game's gameid
        else if (command.equals(JOIN)) {
            if (session.getAttribute("Game") != null)
                throw new CommandException("Command: JOIN. You are already in game.");
            String name = data.getString("Username"); // = session.getAttribute("Username");
            int gameID = data.getInt("GameID");
            Game game = GameList.getGame(gameID);
            game.getPlayerList().joinGame(new Player(name, session));
            session.setAttribute("Game", game);
            game.pushChanges();
        }
        
        
        // QUIT, STARTGAME, ENDPHASE, ENDTURN - GAME MUST EXIST PAST THIS POINT

        // Quit:
        // No additional params required.
        else if (command.equals(QUIT)) {
            if (session.getAttribute("Game") == null)
                throw new CommandException("Command: QUIT. You are not in a game.");
            Game game = (Game) session.getAttribute("Game");
            game.getPlayerList().removePlayer(session);
            session.removeAttribute("Game");
            game.pushChanges();
        }
        
        // Closelobby:
        // No additional params required.
        // TODO - only host should be able to close the lobby
        else if (command.equals(CLOSELOBBY)) {
            if (session.getAttribute("Game") == null)
                throw new CommandException("Command: CLOSELOBBY. You are not in a game.");
            Game game = (Game) session.getAttribute("Game");
            game.getGameState().closeLobby();
            game.pushChanges();
        }
        
        // Endphase
        // No additional params required.
        else if (command.equals(ENDPHASE)) {
            if (session.getAttribute("Game") == null)
                throw new CommandException("Command: ENDPHASE. You are not in a game.");
            Game game =(Game) session.getAttribute("Game");
            if (!session.equals(game.getCurrentPlayerObject().getSession()))
                throw new CommandException("Command: ENDPHASE. Not your turn. (session mismatch)");
            game.endPhase();
            game.pushChanges();
        }
        
        // Endturn
        // No additional params required.
        else if (command.equals(ENDTURN)) {
            if (session.getAttribute("Game") == null)
                throw new CommandException("Command: ENDTURN. You are not in a game.");
            Game game =(Game) session.getAttribute("Game");
            if (!session.equals(game.getCurrentPlayerObject().getSession()))
                throw new CommandException("Command: ENDTURN. Not your turn. (session mismatch)");
            game.nextPlayer();
            game.pushChanges();
        }
        
        
        // GAME PHASES - SETUP, DEPLOY, ATTACK, MOVE
        
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
