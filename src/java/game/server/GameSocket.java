package game.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/GameSocket")
public class GameSocket {
    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());
    
    @OnOpen
    public void onOpen (Session peer) {
        clients.add(peer);
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
    }

    @OnClose
    public void onClose (Session peer) {
        clients.remove(peer);
    }
}