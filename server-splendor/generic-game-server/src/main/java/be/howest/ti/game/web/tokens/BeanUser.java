package be.howest.ti.game.web.tokens;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;

public class BeanUser implements User {

    private final Object bean;
    private final JsonObject principal;


    public BeanUser(String token, Object bean) {
        this.bean = bean;
        this.principal = User.fromToken(token).principal();
    }


    @Override
    public JsonObject attributes() {
        return JsonObject.mapFrom(bean);
    }

    public Object getBean(){
        return bean;
    }

    @Override
    public User isAuthorized(Authorization authority, Handler<AsyncResult<Boolean>> resultHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonObject principal() {
        return principal;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
        // do nothing for now (same as UserImpl)
    }

    @Override
    public User merge(User other) {
        throw new UnsupportedOperationException();
    }
}
