package game.server;

import game.logic.Commands;
import game.objects.Game;
import game.objects.GameList;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;

/**
 * Implementation of the game Servlet and its high level methods.
 */
public class GameServlet extends HttpServlet {

    //private final Game game = new Game("test_game_name");
    private final boolean useSSE = true;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (useSSE) {
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Connection", "keep-alive");
            response.setHeader("Cache-Control", "no-cache");
            HttpSession session = request.getSession();
            
            // send username
            if (session.getAttribute("UsernameSent") == null) {
                try (PrintWriter out = response.getWriter()) {
                    if (session.getAttribute("Username") != null) {
                        JSONObject json = new JSONObject();
                        json.put("Username", session.getAttribute("Username"));
                        session.setAttribute("UsernameSent", true);
                        System.out.println("GET data sending:   " + json);
                        out.write("event: username\ndata: " + json + "\n\n");
                        out.flush();
                    }
                } catch (JSONException e) {
                    Logger.getLogger(GameServlet.class.getName()).log(Level.SEVERE, null, e);
                    session.removeAttribute("UsernameSent");
                }
                return;
            }

            // If session does not contain a game, show the game list instead
            if (session.getAttribute("Game") == null) {

                if (session.getAttribute("gameListLastModified") != null) {
                    // exit early if gamelist has not changed
                    if ((long) session.getAttribute("gameListLastModified") == GameList.getLastModified()) {
                        return;
                    }
                }
                session.setAttribute("gameListLastModified", GameList.getLastModified());

                // send out the gamelist json
                try (PrintWriter out = response.getWriter()) {
                    JSONObject json = GameList.getGameListJSON();
                    System.out.println("GET data sending:   " + json);
                    out.write("event: gamelist\ndata: " + json + "\n\n");
                    out.flush();
                } catch (JSONException e) {
                    Logger.getLogger(GameServlet.class.getName()).log(Level.SEVERE, null, e);
                }
                return;
            }

            // GAME EXISTS PAST THIS POINT
            Game game = (Game) session.getAttribute("Game");
            if (session.getAttribute("lastModified") != null) {
                // exit early if game has not changed
                if ((long) session.getAttribute("lastModified") == game.getLastModified()) {
                    return;
                }
            }
            session.setAttribute("lastModified", game.getLastModified());

            // send out the gamestate json
            try (PrintWriter out = response.getWriter()) {
                JSONObject json = game.getGameJSON();
                System.out.println("GET data sending:   " + json);
                out.write("event: gamestate\ndata: " + json + "\n\n");
                out.flush();
            } catch (JSONException e) {
                Logger.getLogger(GameServlet.class.getName()).log(Level.SEVERE, null, e);
            }

        } else {
            //nonSSEGet(response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        try {
            JSONObject json = new JSONObject(request.getReader().readLine());
            System.out.println("POST data received: " + json);
            Commands.doCommand(json, session);
        } catch (JSONException | CommandException | DiceException | TroopsException | PlayerException e) {
            Logger.getLogger(GameServlet.class.getName()).log(Level.SEVERE, null, e);
        }
    }

//    private void nonSSEGet(HttpServletResponse response, Game game) throws IOException {
//        response.setContentType("application/json");
//        try (PrintWriter out = response.getWriter()) {
//            JSONObject json = game.getGameJSON();
//            out.print(json);
//            System.out.println("GET data sending:   " + json);
//        } catch (JSONException e) {
//            Logger.getLogger(GameServlet.class.getName()).log(Level.SEVERE, null, e);
//        }
//    }
    @Override
    public String getServletInfo() {
        return "Risque Game Servlet.";
    }
}
