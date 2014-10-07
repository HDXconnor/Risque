package game.server;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public class OAuthServlet extends HttpServlet {

    private static final Token EMPTY_TOKEN = null;
    private static String callback = "http://111.69.101.129:8084/Risque/OAuthServlet";
    private static String facebookKey = "766984246696208";
    private static String facebookSecret = "a92c4944d4c9a4c3c8247c4514cdbd46";
    private static String googleKey = "anonymous";
    private static String googleSecret = "anonymous";
    private static final String googleScope = "https://www.googleapis.com/auth/plus.login";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("service").equals("facebook")) {
            OAuthService service = new ServiceBuilder().provider(FacebookApi.class).apiKey(facebookKey).apiSecret(facebookSecret).callback(callback).build();
            String state = request.getParameter("id");
            String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN) + "&state=" + state;
            response.setContentType("text/html");
            response.setStatus(response.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", authorizationUrl);
        } else if (request.getParameter("service").equals("google")) {
            OAuthService service = new ServiceBuilder().provider(GoogleApi.class).apiKey(googleKey).apiSecret(googleSecret).scope(googleScope).callback(callback).build();
            Token requestToken = service.getRequestToken();
            String state = request.getParameter("id");
            String authorizationUrl = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token=" + requestToken.getToken() + "&state=" + state;
            response.setContentType("text/html");
            response.setStatus(response.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", authorizationUrl);
        } else {
            PrintWriter out = response.getWriter();
            out.println("Invalid service");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
