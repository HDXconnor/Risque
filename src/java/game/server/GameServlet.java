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

/**
 * Implementation of the game Servlet and its high level methods.
 */
public class GameServlet extends HttpServlet {
    private final Game game = new Game(new PlayerList());
    private final boolean useSSE = true;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //tempJoinGame(request);
        if (useSSE) {
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Connection", "keep-alive");
            try (PrintWriter out = response.getWriter()) {
                JSONObject json = game.getGameJSON();
                System.out.println("GET data sending:   " + json);
                if (game.updated()) {
                    out.write("event: gamestate\ndata: " + json + "\n\n"); // SSE requires a blank line: \n\n
                }
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
        try {
            JSONObject json = new JSONObject(request.getReader().readLine());
            System.out.println("POST data received: " + json);
            Command.parseInput(json, game);
        } catch (JSONException | CommandException | DiceException | TroopsException | PlayerException e) {
            Logger.getLogger(GameServlet.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void tempJoinGame(HttpServletRequest request) {
        try {
            String name = request.getCookies()[0].getValue();
            HashMap<Integer, Player> players = game.getPlayerList().getPlayers();
            for (Player p : players.values()) {
                if (p.getName().equals(name)) {
                    return;
                }
            }
            game.getPlayerList().joinGame(new Player(name, 20));
        } catch (PlayerException | NullPointerException ex) {
            Logger.getLogger(GameServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getServletInfo() {
        return "Risque Game Servlet.";
    }
}