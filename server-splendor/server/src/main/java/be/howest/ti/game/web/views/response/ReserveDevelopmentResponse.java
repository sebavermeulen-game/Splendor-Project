package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.DevelopmentCard;
import be.howest.ti.game.logic.GemType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReserveDevelopmentResponse extends AbstractResponseWithHiddenStatus {
    private final Set<DevelopmentCard> reserve;
    private final Map<String, Integer> tokens;

    public ReserveDevelopmentResponse(Set<DevelopmentCard> reserve, Map<GemType, Integer> tokensMap) {
        super(200);
        this.reserve = reserve;
        
        this.tokens = new HashMap<>();
        for (Map.Entry<GemType, Integer> entry : tokensMap.entrySet()) {
            if (entry.getValue() > 0) {
                tokens.put(entry.getKey().toString(), entry.getValue());
            }
        }
    }

    public Set<DevelopmentCard> getReserve() {
        return reserve;
    }

    public Map<String, Integer> getTokens() {
        return tokens;
    }
}
