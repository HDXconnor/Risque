/**
 * Copyright 2014 Connor Anderson
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package game.server;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;

public class OAuthServlet extends HttpServlet {

    private static final Token EMPTY_TOKEN = null;
    private static String callback = "http://raptorrisk.me/OAuthServlet";
    private static String facebookKey = "766984246696208";
    private static String facebookSecret = "a92c4944d4c9a4c3c8247c4514cdbd46";
    private static String googleKey = "anonymous";
    private static String googleSecret = "anonymous";
    private static final String googleScope = "https://www.googleapis.com/auth/plus.me";

    OAuthService fbService = new ServiceBuilder().provider(FacebookApi.class).apiKey(facebookKey).apiSecret(facebookSecret).callback(callback).build();
    HttpSession session;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Connection", "keep-alive");
        response.addHeader("Cache-Control", "no-cache");
        session = request.getSession();
        if (session.getAttribute("state").equals(request.getParameter("state"))) {
            Verifier verifier = new Verifier(request.getParameter("code"));
            Token accessToken = fbService.getAccessToken(EMPTY_TOKEN, verifier);
            session.setAttribute("fbToken", accessToken);
            OAuthRequest authRequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
            fbService.signRequest(accessToken, authRequest);
            Response authResponse = authRequest.send();
            try {
                JSONObject responseJSON = new JSONObject(authResponse.getBody());
                session.setAttribute("Username", responseJSON.getString("first_name"));
                response.setStatus(response.SC_MOVED_TEMPORARILY);
                response.setHeader("Location", "http://raptorrisk.me/");
            } catch (JSONException ex) {

            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        session = request.getSession();
        if (request.getParameter("service").equals("facebook")) {
            session.setAttribute("state", request.getParameter("id"));
            String state = request.getParameter("id");
            String authorizationUrl = fbService.getAuthorizationUrl(EMPTY_TOKEN) + "&state=" + state;
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(authorizationUrl);
        } else if (request.getParameter("service").equals("google")) {
            OAuthService service = new ServiceBuilder().provider(GoogleApi.class).apiKey(googleKey).apiSecret(googleSecret).scope(googleScope).callback(callback).build();
            Token requestToken = service.getRequestToken();
            String state = request.getParameter("id");
            String authorizationUrl = "https://www.google.com/accounts/OAuthAuthorizeToken?state=" + state + "&oauth_token=" + requestToken.getToken();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(authorizationUrl);
        } else {
            PrintWriter out = response.getWriter();
            out.println("Invalid service");
        }
    }
}
