package game.server;

import game.logic.Command;
import game.objects.Game;
import game.objects.Player;
import game.objects.PlayerList;
import game.objects.exceptions.CommandException;
import game.objects.exceptions.DiceException;
import game.objects.exceptions.PlayerException;
import game.objects.exceptions.TroopsException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;

/**
 * Implementation of the game Servlet and its high level methods.
 */
public class GameServlet extends HttpServlet {
    private final Game game = new Game("test_game_name");
    private final boolean useSSE = true;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //tempJoinGame(request);
        if (useSSE) {

            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Connection", "keep-alive");
            response.setHeader("Cache-Control", "no-cache");

            HttpSession session = request.getSession();
            
            if (session.getAttribute("lastModified") != null) {
                if ((long) session.getAttribute("lastModified") == game.getLastModified()) return;
            }
            
            session.setAttribute("lastModified", game.getLastModified());

            try (PrintWriter out = response.getWriter()) {
                JSONObject json = game.getGameJSON();
                System.out.println("GET data sending:   " + json);
                out.write("event: gamestate\ndata: " + json + "\n\n"); // SSE requires a blank line: \n\n
                out.flush();
            } catch (JSONException e) {
                Logger.getLogger(GameServlet.class.getName()).log(Level.SEVERE, null, e);
            }

            // keeping the old method around for testing purposes...
        } else {
            response.setContentType("application/json");
            try (PrintWriter out = response.getWriter()) {
                JSONObject json = game.getGameJSON();
                out.print(json);
                System.out.println("GET data sending:   " + json);
            } catch (JSONException e) {
                Logger.getLogger(GameServlet.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(request.getContextPath());
        HttpSession session = request.getSession();
        try {
            JSONObject json = new JSONObject(request.getReader().readLine());
            System.out.println("POST data received: " + json);
            Command.parseInput(json, session, game);
        } catch (JSONException | CommandException | DiceException | TroopsException | PlayerException e) {
            Logger.getLogger(GameServlet.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public String getServletInfo() {
        return "Risque Game Servlet.";
    }
}