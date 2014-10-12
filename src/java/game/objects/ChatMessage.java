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
public class ChatMessage {
    
    private final String username;
    private final String message;
    private final long timestamp;
    
    public ChatMessage(String username, String message) {
        this.username = username;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public JSONObject getMessage() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("Username", username);
        json.put("Message", message);
        json.put("Timestamp", timestamp);
        return json;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}
