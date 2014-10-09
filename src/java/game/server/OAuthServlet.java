/**
 * Copyright 2014 Connor Anderson

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
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

public class OAuthServlet extends HttpServlet {

    private static final Token EMPTY_TOKEN = null;
    private static String callback = "http://raptorrisk.me/OAuthServlet";
    private static String facebookKey = "766984246696208";
    private static String facebookSecret = "a92c4944d4c9a4c3c8247c4514cdbd46";
    private static String googleKey = "anonymous";
    private static String googleSecret = "anonymous";
    private static final String googleScope = "https://www.googleapis.com/auth/plus.me";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String thisParam = names.nextElement();
            out.print(thisParam + ": ");
            for (String x : request.getParameterValues(thisParam)) {
                out.print(x + ", ");
            }
            out.print("\n");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("service").equals("facebook")) {
            OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(facebookKey).apiSecret(facebookSecret).callback(callback).build();
            String state = request.getParameter("id");
            String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN) + "&state=" + state;
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