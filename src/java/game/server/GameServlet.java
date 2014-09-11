/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.server;

import game.logic.Command;
import game.objects.Game;
import game.objects.PlayerList;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class GameServlet extends HttpServlet {
    
    Game game = new Game(new PlayerList("Awaiting player...","Awaiting player...","Awaiting player...","Awaiting player...","Awaiting player...","Awaiting player..."));
    boolean useSSE = true;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        if (useSSE) {
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter out = response.getWriter()) {
                JSONObject json = game.getGameJSON();
                System.out.println("GET data sending:   " + json);
                if (game.updated()) out.write("event: gamestate\ndata: " + json + "\n\n"); // SSE requires a blank line: \n\n
            } catch (JSONException e) {}
            
        // keeping the old method around for testing purposes...
        } else {
            response.setContentType("application/json");
            try (PrintWriter out = response.getWriter()) {
                JSONObject json = game.getGameJSON();
                out.print(json);
                System.out.println("GET data sending:   " + json);
            } catch (JSONException e) {}
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            
            JSONObject clicked = new JSONObject(request.getParameter("clicked"));
            System.out.println("POST data received: " + clicked);
            Command.parseInput(clicked, game);
        } catch (JSONException e) {
        
        }
        
        
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Risque Game Servlet.";
    }

}
