package be.howest.ti.game.web.views.request;

import io.vertx.ext.web.RoutingContext;

public class JoinGameRequest extends BaseSplendorRequest{
    public JoinGameRequest(RoutingContext ctx) {
        super(ctx);
    }
    public String getPlayerName(){
        return params.pathParameter("playerName").getString();
    }

    public int getGameId(){
        return params.pathParameter("gameId").getInteger();
    }
}
