/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.logic;

import game.objects.Game;
import game.objects.Phase;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class Command {
    
    /*
    Commands to support:
    Setup
    Deploy
    Attack
    Move
    EndTurn (end phase command)
    
    Need to check which player sent the command
    
    */
    public static void parseInput(JSONObject json, Game game) throws JSONException {
        String cmd = (String) json.get("Command");
        JSONObject data = (JSONObject) json.get("Data");
        
        switch (cmd) {
            case Phase.SETUP:
                String country = (String) data.get("CountryClicked");
                int player = (Integer) data.get("CurrentPlayer");
                game.getBoard().getCountry(country).setOwner(player);
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
            default:
                break;
            
        }
        
    }
    
}