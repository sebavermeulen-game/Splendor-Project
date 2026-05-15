package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.GemType;

public class GetGemsResponse extends AbstractResponseWithHiddenStatus {
    private final GemType[] gems;
    public GetGemsResponse() {
        super(200);
        this.gems = GemType.values();
    }

    public GemType[] getGems() {
        return gems;
    }
}
