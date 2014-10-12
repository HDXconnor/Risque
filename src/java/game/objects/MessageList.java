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
    Collection<Message> messageList;
    private long lastModified;
    private int cleanup;
    
    public MessageList() {
        this.messageList = Collections.synchronizedList(new LinkedList());
        this.lastModified = System.currentTimeMillis();
        this.cleanup = 0;
    }
    
    public void addMessage(Message message) {
        messageList.add(message);
        lastModified = System.currentTimeMillis();
    }

    public JSONObject getMessages(HttpSession session) throws JSONException {
        long lastMessageSeen = (long) session.getAttribute("LastChatMessageSeen");
        JSONArray arr = new JSONArray();
        Iterator<Message> iterator = messageList.iterator();
        cleanup = messageList.size();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (message.getTimestamp() > lastMessageSeen) {
                arr.put(message.getMessage());
            } else if (cleanup > 1000) {
                if (message.getTimestamp() < System.currentTimeMillis() - 60000) {
                    iterator.remove();
                }
            }
        }
        if (arr.length() > 1) {
            session.setAttribute("LastChatMessageSeen", System.currentTimeMillis());
        }
        return new JSONObject().put("Messages", arr);
    }
    public long getLastModified() {
        return lastModified;
    }
}
