package be.howest.ti.game.web.views.request;

import be.howest.ti.game.web.tokens.BeanUser;
import be.howest.ti.game.web.tokens.GroupSecret;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;


public abstract class ContextBasedRequestView {

    protected final RoutingContext ctx;
    protected final RequestParameters params;

    ContextBasedRequestView(RoutingContext ctx) {
        this.ctx = ctx;
        this.params = ctx.get(ValidationHandler.REQUEST_CONTEXT_KEY);
    }

    @SuppressWarnings("unchecked")
    protected  <T> T getPlayer() {
        return (T) ((BeanUser) ctx.user()).getBean();
    }

    public GroupSecret getGroupSecret() {
        String txt = ctx.request().getHeader("X-Group-Secret");
        return new GroupSecret(txt);
    }
}
