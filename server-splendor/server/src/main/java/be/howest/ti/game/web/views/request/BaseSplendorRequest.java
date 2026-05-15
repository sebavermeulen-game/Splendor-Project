package be.howest.ti.game.web.views.request;

import be.howest.ti.game.web.tokens.SplendorHTTPPlayer;
import io.vertx.ext.web.RoutingContext;

public class BaseSplendorRequest extends ContextBasedRequestView {

    public BaseSplendorRequest(RoutingContext ctx) {
        super(ctx);
    }

    protected SplendorHTTPPlayer getSplendorPlayer() {
        return getPlayer();
    }

    public int getAuthorizedGameId() {
        return getSplendorPlayer().getGameId();
    }

    public String getAuthorizedPlayerName() {
        return getSplendorPlayer().getPlayerName();
    }
}
