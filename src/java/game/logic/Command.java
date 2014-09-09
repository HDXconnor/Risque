/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.logic;

import game.objects.Game;
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
            case "Setup":
                data.get("CountryClicked");
                
                break;
            case "Deploy":
                
                break;
            case "Attack":
                
                break;
            case "Move":
                
                break;
            case "EndPhase":
                game.getGameState().getPhase().nextPhase();
                break;
            default:
                break;
            
        }
        
    }
    
}
