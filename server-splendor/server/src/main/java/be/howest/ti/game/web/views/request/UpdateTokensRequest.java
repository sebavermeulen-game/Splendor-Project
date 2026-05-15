package be.howest.ti.game.web.views.request;

import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject;
import be.howest.ti.game.logic.GemType;
import be.howest.ti.game.logic.Purse;

import java.util.Map;

public class UpdateTokensRequest extends BaseSplendorRequest {
    public UpdateTokensRequest(RoutingContext ctx) {
        super(ctx);
    }

    public int getGameId() {
        return params.pathParameter("gameId").getInteger();
    }

    public String getPlayerName() {
        return params.pathParameter("playerName").getString();
    }

    public Purse getTokens() {
        JsonObject body = params.body().getJsonObject();
        Purse purse = new Purse();

        for (String action : new String[]{"take", "return"}) {
            if (body.containsKey(action)) {
                int multiplier = action.equals("take") ? 1 : -1;
                JsonObject obj = body.getJsonObject(action);
                for (String key : obj.fieldNames()) {
                    GemType gem = GemType.valueOf(key.toUpperCase());
                    int amount = obj.getInteger(key);
                    purse.addTokens(gem, multiplier * amount);
                }
            }
        }
        return purse;
    }

    public Map<GemType, Integer> getTokenMap() {
        return getTokens().getTokensMap();
    }
}
