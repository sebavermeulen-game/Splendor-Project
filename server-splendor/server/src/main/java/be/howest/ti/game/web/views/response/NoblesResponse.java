package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.NobleCard;
import java.util.List;

public class NoblesResponse extends AbstractResponseWithHiddenStatus {
    private final List<NobleCard> nobles;

    public NoblesResponse(List<NobleCard> nobles) {
        super(200);
        this.nobles = nobles;
    }

    public List<NobleCard> getNobles() {
        return nobles;
    }
}
