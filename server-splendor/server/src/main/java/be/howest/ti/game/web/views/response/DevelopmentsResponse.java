package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.DevelopmentCard;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DevelopmentsResponse extends AbstractResponseWithHiddenStatus {

    @JsonProperty("developments")
    private final List<DevelopmentCard> developmentCards;

    public DevelopmentsResponse(List<DevelopmentCard> developmentCards) {
        super(200);
        this.developmentCards = developmentCards;
    }
}
