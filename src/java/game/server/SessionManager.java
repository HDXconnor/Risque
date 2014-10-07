/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.server;

import game.objects.Game;
import game.objects.exceptions.PlayerException;
import game.objects.exceptions.SessionManagerException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Simeon
 */
public class SessionManager {
    static Map<Game, Set<HttpSession>> games = new ConcurrentHashMap<>();
    
    public static void createGame(HttpSession session, String gameName) {
        Set<HttpSession> sessionSet = new HashSet<>();
        sessionSet.add(session);
        games.put(new Game(gameName), sessionSet);
    }
    
    public static void joinGame(int gameID, HttpSession session) throws SessionManagerException {
        Game game = getGameByID(gameID);
        games.get(game).add(session);
    }
    
    public static void leaveGame(int gameID, HttpSession session) throws SessionManagerException, PlayerException {
        Game game = getGameByID(gameID);
        game.getPlayerList().removePlayer(session);
    }
    
    public static void destroyGame(Game game) {
        for (HttpSession session : games.get(game)) {
            session.invalidate(); // invalidate too much?
        }
        games.remove(game);
    }
    
    private static Game getGameByID(int gameID) throws SessionManagerException {
        for (Game game : games.keySet()) {
            if (game.getGameID() == gameID) return game;
        }
        throw new SessionManagerException("Cannot find game with that game name");
    }
}