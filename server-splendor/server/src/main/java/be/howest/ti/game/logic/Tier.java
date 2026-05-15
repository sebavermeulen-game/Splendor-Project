package be.howest.ti.game.logic;

import java.util.Set;

public class Tier {
    private String name;
    private Set<DevelopmentCard> cards;

    public Tier(String name, Set<DevelopmentCard> cards) {
        this.name = name;
        this.cards = cards;
    }

    public String getName() {
        return name;
    }

    public Set<DevelopmentCard> getCards() {
        return cards;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCards(Set<DevelopmentCard> cards) {
        this.cards = cards;
    }
}
