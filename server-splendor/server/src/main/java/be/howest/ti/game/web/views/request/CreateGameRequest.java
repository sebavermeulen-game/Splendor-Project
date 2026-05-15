package be.howest.ti.game.web.views.request;

import io.vertx.ext.web.RoutingContext;

public class CreateGameRequest extends BaseSplendorRequest {
    public CreateGameRequest(RoutingContext ctx) {
        super(ctx);
    }

    public String getPlayerName(){
        return params.body().getJsonObject().getString("playerName");
    }
    public String getGameName(){
        return params.body().getJsonObject().getString("gameName");
    }

    public int getNumberOfPlayers(){
        return params.body().getJsonObject().getInteger("numberOfPlayers");
    }
}
