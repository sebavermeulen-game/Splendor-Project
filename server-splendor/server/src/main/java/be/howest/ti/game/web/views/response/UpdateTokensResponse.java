package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.Purse;

public class UpdateTokensResponse extends AbstractResponseWithHiddenStatus {
    private final Purse purse;

    public UpdateTokensResponse(Purse purse) {
        super(200);
        this.purse = purse;
    }

    public Purse getPurse() {
        return purse;
    }
}