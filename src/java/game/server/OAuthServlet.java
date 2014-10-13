/**
 * Copyright 2014 Team Awesome Productions
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
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.Google2Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;

public class OAuthServlet extends HttpServlet {

    private static final Token EMPTY_TOKEN = null;
    private static final String callback = "http://raptorrisk.me/OAuthServlet";
    private static final String facebookKey = "766984246696208";
    private static final String facebookSecret = "a92c4944d4c9a4c3c8247c4514cdbd46";
    private static final String googleKey = "373200947340-vvqdr3itb4l6ema6nvjq54lddtr2bl6a.apps.googleusercontent.com";
    private static final String googleSecret = "OYmdBnb08OANrn1teNYaI304";
    private static final String googleScope = "openid profile email";

    OAuthService fbService = new ServiceBuilder().provider(FacebookApi.class).apiKey(facebookKey).apiSecret(facebookSecret).callback(callback).build();
    OAuthService googleService = new ServiceBuilder().provider(Google2Api.class).apiKey(googleKey).apiSecret(googleSecret).scope(googleScope).callback(callback).build();
    HttpSession session;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Connection", "keep-alive");
        response.addHeader("Cache-Control", "no-cache");
        session = request.getSession();
        if (session.getAttribute("state").equals(request.getParameter("state"))) {
            if (request.getParameter("state").startsWith("facebook")) {
                Verifier verifier = new Verifier(request.getParameter("code"));
                Token accessToken = fbService.getAccessToken(EMPTY_TOKEN, verifier);
                session.setAttribute("fbToken", accessToken);
                try {
                    // Get profile data
                    OAuthRequest authRequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
                    fbService.signRequest(accessToken, authRequest);
                    Response authResponse = authRequest.send();
                    JSONObject responseJSON = new JSONObject(authResponse.getBody());
                    session.setAttribute("Username", responseJSON.getString("name"));

                    // Get profile picture
                    authRequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me/picture");
                    fbService.signRequest(accessToken, authRequest);
                    session.setAttribute("PlayerImage", authRequest.getCompleteUrl());

                    response.setStatus(response.SC_MOVED_TEMPORARILY);
                    response.setHeader("Location", "http://raptorrisk.me");

                } catch (JSONException ex) {

                }
            } else if (request.getParameter("state").startsWith("google")) {
                Verifier verifier = new Verifier(request.getParameter("code"));
                Token accessToken = googleService.getAccessToken(EMPTY_TOKEN, verifier);
                session.setAttribute("googleToken", accessToken);
                try {
                    // Get profile data and picture
                    OAuthRequest authRequest = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v1/userinfo?alt=json");
                    googleService.signRequest(accessToken, authRequest);
                    Response authResponse = authRequest.send();
                    JSONObject responseJSON = new JSONObject(authResponse.getBody());
                    session.setAttribute("Username", responseJSON.getString("name"));
                    session.setAttribute("PlayerImage", responseJSON.getString("picture"));

                    response.setStatus(response.SC_MOVED_TEMPORARILY);
                    response.setHeader("Location", "http://raptorrisk.me");

                } catch (JSONException ex) {

                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        session = request.getSession();
        if (request.getParameter("service").equals("facebook")) {
            String state = request.getParameter("id");
            session.setAttribute("state", state);
            String authorizationUrl = fbService.getAuthorizationUrl(EMPTY_TOKEN) + "&state=" + state;
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(authorizationUrl);
        } else if (request.getParameter("service").equals("google")) {
            String state = request.getParameter("id");
            session.setAttribute("state", state);
            String authorizationUrl = googleService.getAuthorizationUrl(EMPTY_TOKEN) + "&state=" + state;
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(authorizationUrl);
        } else {
            PrintWriter out = response.getWriter();
            out.println("Invalid service");
        }
    }
}
