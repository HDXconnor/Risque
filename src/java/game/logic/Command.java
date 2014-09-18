/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.logic;

import game.objects.Game;
import game.objects.Phase;
import game.objects.Player;
import game.objects.exceptions.PlayerException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class Command {
    private static final String CREATE = "Create";
    private static final String JOIN = "Join";
    private static final String QUIT = "Quit";
    
    /*
    Commands to support:
    Setup
    Deploy
    Attack
    Move
    EndPhase (end phase command)
    
    TODO:
    Need to check which player sent the command - other players are able to send another players commands currently
    
    */
    public static void parseInput(JSONObject json, Game game) throws JSONException {
        String cmd = (String) json.get("Command");
        JSONObject data = (JSONObject) json.get("Data");
        
        switch (cmd) {
            case Phase.SETUP:
                String country = (String) data.get("CountryClicked");
                int player = (Integer) data.get("CurrentPlayer");
                if (game.getCurrentPlayer() == player && game.getBoard().getCountry(country).getOwner() == -1) {
                    game.getBoard().getCountry(country).setOwner(player);
                } else {
                    // country already owned by another player
                }
                break;
            case Phase.DEPLOY:
                
                break;
            case Phase.ATTACK:
                
                break;
            case Phase.MOVE:
                
                break;
            case Phase.ENDPHASE:
                game.endPhase();
                break;
            case CREATE:
                break;
            case JOIN:
                String name = (String) data.get("DisplayName");
                try {
                    game.getPlayers().joinGame(new Player(name, 20));
                } catch (PlayerException ex) {
                    //join game fails, response?
                }
                break;
            case QUIT:
                break;
            default:
                break;
            
        }
        
    }
    
}
