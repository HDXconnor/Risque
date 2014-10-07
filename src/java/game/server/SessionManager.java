/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.server;

import game.objects.Game;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Simeon
 */
public class SessionManager {
    static Map<Game, Set<HttpSession>> gameParticipants = new ConcurrentHashMap<>();

}
