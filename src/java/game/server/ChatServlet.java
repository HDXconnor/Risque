/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.server;

import game.objects.Game;
import game.objects.ChatMessage;
import game.objects.exceptions.MessageException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Simeon
 */
public class ChatServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Cache-Control", "no-cache");
        HttpSession session = request.getSession();
        
        Game game = (Game) session.getAttribute("Game");
        try (PrintWriter out = response.getWriter()) {
            if (game != null) {
                JSONObject json = game.getMessages().getMessages(session);
                session.setAttribute("LastChatMessageSeen", System.currentTimeMillis());
                out.write("event: messages\ndata: " + json + "\n\n");
                out.flush();
            } else {
                throw new MessageException("Not in a game.");
            }
        } catch (JSONException | MessageException ex) {
            Logger.getLogger(ChatServlet.class.getName()).log(Level.SEVERE, null, ex);
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
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        try (PrintWriter out = response.getWriter()) {
            JSONObject json = new JSONObject(request.getReader().readLine());
            Game game = (Game) session.getAttribute("Game");
            String username = (String) session.getAttribute("Username");
            if (game != null && username != null) {
                if ((long) session.getAttribute("LastChatMessageSeen") < game.getMessages().getLastModified()) {
                    JSONObject data = json.getJSONObject("Data");
                    String dataUser = data.getString("Username");
                    if (!dataUser.equals(username)) {
                        throw new MessageException("Username in session does not match username in sent data");
                    }
                    String dataMessage = data.getString("Message");
                    game.getMessages().addChatMessage(new ChatMessage(dataUser, dataMessage));
                }
                JSONObject outputjson = game.getMessages().getMessages(session);
                out.write(outputjson.toString());
                out.flush();
            } else {
                throw new MessageException("Not in a game, or do not have a username.");
            }
        } catch (JSONException | MessageException ex) {
            Logger.getLogger(ChatServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Chat servlet";
    }// </editor-fold>

}
