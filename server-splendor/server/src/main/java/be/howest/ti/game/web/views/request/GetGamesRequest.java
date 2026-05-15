package be.howest.ti.game.web.views.request;

import io.vertx.ext.web.RoutingContext;
import java.util.Optional;

public class GetGamesRequest extends BaseSplendorRequest {
    public GetGamesRequest(RoutingContext ctx) {
        super(ctx);
    }

    public Optional<Boolean> isStarted() {
        if (params.queryParameter("started") == null) {
            return Optional.empty();
        } else {
            return Optional.of(params.queryParameter("started").getBoolean());
        }
    }
}