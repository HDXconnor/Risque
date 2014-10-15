/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.objects;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class MessageList {
    Collection<ChatMessage> chatMessageList;
    Collection<GameMessage> gameMessageList;
    private long lastModified;
    
    public MessageList() {
        this.chatMessageList = Collections.synchronizedList(new LinkedList());
        this.gameMessageList = Collections.synchronizedList(new LinkedList());
        this.lastModified = System.currentTimeMillis();
    }
    
    public void addChatMessage(ChatMessage message) {
        chatMessageList.add(message);
        lastModified = System.currentTimeMillis();
    }
   
    public void addGameMessage(GameMessage message) {
        gameMessageList.add(message);
        lastModified = System.currentTimeMillis();
    }

    public JSONObject getMessages(HttpSession session) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("ChatMessages", getChatMessages(session));
        json.put("GameMessages", getGameMessages(session));
        return json;
    }
    
    private JSONArray getChatMessages(HttpSession session) throws JSONException {
        JSONArray arr = new JSONArray();
        if (session.getAttribute("LastMessageSeen") != null) {
            Iterator<ChatMessage> iterator = chatMessageList.iterator();
            while (iterator.hasNext()) {
                ChatMessage message = iterator.next();
                arr.put(message.getMessage());
                if (chatMessageList.size() > 100) {
                    if (message.getTimestamp() < System.currentTimeMillis() - 60000) {
                        iterator.remove();
                    }
                }
            }
        } else {
            session.setAttribute("LastMessageSeen", (long) 0);
        }
        return arr;
    }

    private JSONArray getGameMessages(HttpSession session) throws JSONException {
        JSONArray arr = new JSONArray();
        if (session.getAttribute("LastMessageSeen") != null) {
            Iterator<GameMessage> iterator = gameMessageList.iterator();
            while (iterator.hasNext()) {
                GameMessage message = iterator.next();
                arr.put(message.getMessage());
                if (gameMessageList.size() > 100) {
                    if (message.getTimestamp() < System.currentTimeMillis() - 60000) {
                        iterator.remove();
                    }
                }
            }
        } else {
            session.setAttribute("LastMessageSeen", (long) 0);
        }
        return arr;
    }
    
    public long getLastModified() {
        return lastModified;
    }
}
