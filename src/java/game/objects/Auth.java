package game.objects;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class Auth {

    private String state, code;
    private Token token;

    public Auth(String state, String code) {
        this.state = state;
        this.code = code;
    }

    public void createVerifier(OAuthService service) {
        Verifier verifier = new Verifier(this.code);
        this.token = service.getAccessToken(null, verifier);
    }

    public JSONObject fetchProfileData(String origin, OAuthService service) {
        try {
            OAuthRequest authRequest;
            Response authResponse;
            JSONObject responseJSON = null;
            switch (origin) {
                case "facebook":
                    authRequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
                    service.signRequest(token, authRequest);
                    authResponse = authRequest.send();
                    responseJSON = new JSONObject(authResponse.getBody());
                    authRequest = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me/picture");
                    service.signRequest(token, authRequest);
                    responseJSON.put("image", authRequest.getCompleteUrl());
                    break;
                case "google":
                    authRequest = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v1/userinfo?alt=json");
                    service.signRequest(token, authRequest);
                    authResponse = authRequest.send();
                    responseJSON = new JSONObject(authResponse.getBody());
                    break;
            }
            return responseJSON;
        } catch (JSONException ex) {
            return null;
        }
    }
}
