/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class GameMessage {
    
    private final String phase;
    private final String message;
    private final long timestamp;
    
    public GameMessage(String phase, String message) {
        this.phase = phase;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public JSONObject getMessage() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Phase", phase);
        json.put("Message", message);
        json.put("Timestamp", timestamp);
        return json;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}
