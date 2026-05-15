package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.DevelopmentCard;
import be.howest.ti.game.logic.Purse;

import java.util.Set;

public class BuyDevelopmentResponse extends AbstractResponseWithHiddenStatus{
    private Set<DevelopmentCard> cards;
    private Purse purse;

    public BuyDevelopmentResponse(Set<DevelopmentCard> cards, Purse purse) {
        super(200);
        this.cards = cards;
        this.purse = purse;
    }

    public Set<DevelopmentCard> getCards() {
        return cards;
    }

    public Purse getPurse() {
        return purse;
    }
}
