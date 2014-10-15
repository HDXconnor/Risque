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

import game.objects.Auth;
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
    private static final String facebookScope = "publish_actions";
    private static final String googleKey = "373200947340-vvqdr3itb4l6ema6nvjq54lddtr2bl6a.apps.googleusercontent.com";
    private static final String googleSecret = "OYmdBnb08OANrn1teNYaI304";
    private static final String googleScope = "openid profile email";

    OAuthService fbService = new ServiceBuilder().provider(FacebookApi.class).apiKey(facebookKey).apiSecret(facebookSecret).scope(facebookScope).callback(callback).build();
    OAuthService googleService = new ServiceBuilder().provider(Google2Api.class).apiKey(googleKey).apiSecret(googleSecret).scope(googleScope).callback(callback).build();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Connection", "keep-alive");
        response.addHeader("Cache-Control", "no-cache");
        HttpSession session = request.getSession();
        if (session.getAttribute("state").equals(request.getParameter("state"))) {
            Auth auth = new Auth((String) session.getAttribute("state"), request.getParameter("code"));
            session.setAttribute("Auth", auth);
            try {
                if (request.getParameter("state").startsWith("facebook")) {
                    auth.createVerifier(fbService);
                    JSONObject profileData = auth.fetchProfileData("facebook", fbService);
                    String name = profileData.getString("first_name");
                    name = name.substring(0, Math.min(name.length(), 15));
                    String imageUrl = profileData.getString("image");
                    session.setAttribute("Username", name);
                    session.setAttribute("PlayerImage", imageUrl);
                } else if (request.getParameter("state").startsWith("google")) {
                    auth.createVerifier(googleService);
                    JSONObject profileData = auth.fetchProfileData("google", googleService);
                    String name = profileData.getString("given_name");
                    name = name.substring(0, Math.min(name.length(), 15));
                    session.setAttribute("Username", name);
                    session.setAttribute("PlayerImage", profileData.getString("picture"));
                }
            } catch (JSONException ex) {

            }
        }
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", "http://raptorrisk.me");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (request.getParameter("id").startsWith("facebook")) {
            String state = request.getParameter("id");
            session.setAttribute("state", state);
            String authorizationUrl = fbService.getAuthorizationUrl(EMPTY_TOKEN) + "&state=" + state;
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(authorizationUrl);
        } else if (request.getParameter("id").startsWith("google")) {
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
