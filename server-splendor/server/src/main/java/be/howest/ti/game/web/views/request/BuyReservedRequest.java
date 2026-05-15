package be.howest.ti.game.web.views.request;

import be.howest.ti.game.logic.GemType;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.EnumMap;
import java.util.Map;

public class BuyReservedRequest extends BaseSplendorRequest {
    public BuyReservedRequest(RoutingContext ctx) {
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
        JsonObject reservedPaymentInBody = params.body().getJsonObject().getJsonObject("payment");
        Map<GemType, Integer> reservedPayment = new EnumMap<>(GemType.class);
        if (reservedPaymentInBody != null) {
            for (String key : reservedPaymentInBody.fieldNames()) {
                GemType gemType = GemType.valueOf(key);
                int amount = reservedPaymentInBody.getInteger(key);
                reservedPayment.put(gemType, amount);
            }
        }
        return reservedPayment;
    }
}
