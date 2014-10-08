package game.logic;

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

    private static final String CREATE = "Create";
    private static final String JOIN = "Join";
    private static final String QUIT = "Quit";
    private static final String KICK = "Kick"; // nyi
    //private static final String STARTGAME = "StartGame";
    private static final String CLOSELOBBY = "CloseLobby";
    private static final String DEBUG = "Debug";
    private static final String ENDPHASE = "EndPhase";
    private static final String SETUP = Phase.SETUP;
    private static final String DEPLOY = Phase.DEPLOY;
    private static final String ATTACK = Phase.ATTACK;
    private static final String MOVE = Phase.MOVE;
    
    
    public static void doCommand(JSONObject json, HttpSession session)
            throws JSONException, CommandException, DiceException, TroopsException, PlayerException {
        
        JSONObject data = json.getJSONObject("Data");
        String command = json.getString("Command");
        
        // CREATE AND JOIN - GAME SHOULDN'T EXIST
        // Create - additional data parameters required:
        // Username = a string of the player's name
        // GameName = a string of the game's name
        if (command.equals(CREATE)) {
            String name = data.getString("Username");
            String gameName = data.getString("GameName");
            Game game = new Game(gameName);
            game.getPlayerList().joinGame(new Player(name, session));
            session.setAttribute("Game", game);
            GameList.add(game);
            game.pushChanges();
        }
        
        else if (command.equals(JOIN)) {
            if (session.getAttribute("Game") != null)
                throw new CommandException("Command: JOIN. You are already in game.");
            String name = data.getString("Username");
            int gameID = data.getInt("GameID");
            Game game = GameList.getGame(gameID);
            game.getPlayerList().joinGame(new Player(name, session));
            session.setAttribute("Game", game);
            game.pushChanges();
        }
        
        
        // GAME MUST EXIST PAST THIS POINT
        // QUIT, STARTGAME, ENDPHASE
        
        else if (command.equals(QUIT)) {
            if (session.getAttribute("Game") == null)
                throw new CommandException("Command: QUIT. You are not in a game.");
            Game game = (Game) session.getAttribute("Game");
            game.getPlayerList().removePlayer(session);
            session.removeAttribute("Game");
            game.pushChanges();
        }
        
        // TODO - only host can close the lobby
        else if (command.equals(CLOSELOBBY)) {
            if (session.getAttribute("GameID") == null)
                throw new CommandException("Command: CLOSELOBBY. You are not in a game.");
            Game game = (Game) session.getAttribute("Game");
            game.getGameState().closeLobby();
            game.pushChanges();
        }
        
        else if (command.equals(ENDPHASE)) {
            if (session.getAttribute("GameID") == null)
                throw new CommandException("Command: ENDPHASE. You are not in a game.");
            Game game =(Game) session.getAttribute("Game");
            game.endPhase();
            game.pushChanges();
        }
        
        
        // GAME PHASES - 
        
        
        // DEBUG
    }

}
