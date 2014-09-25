/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.server;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 *
 * @author Johannes
 */
public class OAuthServlet extends HttpServlet {

    private static final Token EMPTY_TOKEN = null;
    private static String apiKey = "766984246696208";
    private static String apiSecret = "a92c4944d4c9a4c3c8247c4514cdbd46";
    OAuthService service;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Verifier verifier = new Verifier(request.getParameter("code"));
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
//        PrintWriter out = response.getWriter();
//        out.println(request.getParameter("code"));

        // Testing our access
        OAuthRequest authRequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
        service.signRequest(accessToken, authRequest);
        Response authResponse = authRequest.send();
        PrintWriter out = response.getWriter();
        out.println(authResponse.getCode());
        out.println(authResponse.getBody());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        service = new ServiceBuilder().provider(FacebookApi.class).apiKey(apiKey).apiSecret(apiSecret).callback("http://111.69.101.129:8084/Risque/OAuthServlet").build();
        String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN); // Send the user here clientside
//        PrintWriter out = response.getWriter();
//        out.println(authorizationUrl);
        response.setContentType("text/html");
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", authorizationUrl);
    }
}
