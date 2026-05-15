package be.howest.ti.game.web.views.request;

import be.howest.ti.game.logic.GemType;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.EnumMap;
import java.util.Map;

public class BuyDevelopmentRequest extends BaseSplendorRequest {
    public BuyDevelopmentRequest(RoutingContext ctx) {
        super(ctx);
    }

    public int getGameId() {
        return params.pathParameter("gameId").getInteger();
    }

    public String getPlayerName() {
        return params.pathParameter("playerName").getString();
    }

    public String getCardName() {
        return params.body().getJsonObject().getJsonObject("development").getString("name");
    }

    public Map<GemType, Integer> getPayment() {
         JsonObject paymentInBody = params.body().getJsonObject().getJsonObject("payment");
         Map<GemType, Integer> payment = new EnumMap<>(GemType.class);
            if (paymentInBody != null) {
                for (String key : paymentInBody.fieldNames()) {
                        GemType gemType = GemType.valueOf(key);
                        int amount = paymentInBody.getInteger(key);
                        payment.put(gemType, amount);
                }
            }
        return payment;
    }

}
