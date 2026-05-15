package be.howest.ti.game.web.tokens;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.TokenCredentials;

public class AuthenticationProviderWithTokenManager implements io.vertx.ext.auth.authentication.AuthenticationProvider{

    private final TokenManager tokenManager;

    public AuthenticationProviderWithTokenManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> handler) {
        TokenCredentials tokenCredentials = credentials.mapTo(TokenCredentials.class);
        String token = tokenCredentials.getToken();

        try {
            handler.handle(Future.succeededFuture(
                    new BeanUser(token, tokenManager.parseToken(token))
            ));
        } catch (InvalidTokenException ex) {
            handler.handle(Future.failedFuture(ex));
        }
    }
}
