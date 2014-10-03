package game.objects;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi;
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
public class Oauth {

    // Global stuff
    private String loginName;
    private boolean isLoggedIn = false;
    private OAuthService service;
    private Token accessToken;
    private static String callback = "http://111.69.101.129:8084/Risque/OAuthServlet";

    // Facebook stuff
    private static final Token EMPTY_TOKEN = null;
    private static String facebookKey = "766984246696208";
    private static String facebookSecret = "a92c4944d4c9a4c3c8247c4514cdbd46";

    // Google stuff
    private Token requestToken;
    private static String googleKey = "anonymous";
    private static String googleSecret = "anonymous";
    private static final String AUTHORIZE_URL = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token=";
    private static final String SCOPE = "https://www.googleapis.com/auth/plus.login";

    public Oauth(String loginName) {
        this.loginName = loginName;
    }

    // Facebook methods
    public String facebookOauth() {
        service = new ServiceBuilder().provider(FacebookApi.class).apiKey(facebookKey).apiSecret(facebookSecret).callback(callback).build();
        String state = this.loginName;
        return service.getAuthorizationUrl(EMPTY_TOKEN) + "&state=" + state;
    }

    public void facebookOauth(String code) {
        Verifier verifier = new Verifier(code);
        accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        isLoggedIn = true;
    }

    public String requestFBResource(String resource) {
        OAuthRequest authRequest = new OAuthRequest(Verb.GET, resource);
        service.signRequest(accessToken, authRequest);
        Response authResponse = authRequest.send();
        return authResponse.getBody();
    }

    // Google methods
    public String googleOauth() {
        service = new ServiceBuilder().provider(GoogleApi.class).apiKey(googleKey).apiSecret(googleSecret).scope(SCOPE).callback(callback).build();
        requestToken = service.getRequestToken();
        String state = this.loginName;
        return AUTHORIZE_URL + requestToken.getToken() + "&state=" + state;
    }

    public void googleOauth(String code) {
        Verifier verifier = new Verifier(code);
        accessToken = service.getAccessToken(requestToken, verifier);
        isLoggedIn = true;
    }

    public String requestGoogleResource(String resource) {
        OAuthRequest request = new OAuthRequest(Verb.GET, resource);
        service.signRequest(accessToken, request);
        request.addHeader("GData-Version", "3.0");
        Response response = request.send();
        return response.getBody();
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

}
