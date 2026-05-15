package be.howest.ti.game.logic;

import java.util.*;

public class Player {
    private String name;
    private Purse purse;
    private Set<DevelopmentCard> cards;
    private Set<DevelopmentCard> reserved;
    private Set<NobleCard> acquiredNobles;
    private Map<GemType, Integer> bonuses;
    private int totalPrestigePoints;

    public Player(String name){
        this.name = name;
        this.purse = new Purse();
        this.cards = new HashSet<>();
        this.reserved = new HashSet<>();
        this.acquiredNobles = new HashSet<>();
        this.bonuses = new EnumMap<>(GemType.class);
        this.totalPrestigePoints = 0;
    }

    public String getName() {
        return name;
    }

    public Purse getPurse() {
        return purse;
    }

    public int getTotalPrestigePoints() {
        return totalPrestigePoints;
    }

    public Set<NobleCard> getAcquiredNobles() {
        return acquiredNobles;
    }

    public Set<DevelopmentCard> getReserved() {
        return reserved;
    }

    public Set<DevelopmentCard> getCards() {
        return cards;
    }

    public Map<GemType, Integer> getBonuses() {
        return bonuses;
    }

    public void addBonus(GemType gemType, int amount) {
        bonuses.put(gemType, bonuses.getOrDefault(gemType, 0) + amount);
    }

    public DevelopmentCard getreservedByName(String reservedCardName) {
        for (DevelopmentCard card : reserved) {
            if (card.getName().equals(reservedCardName)) {
                return card;
            }
        }
        return null;
    }

    public void removeReserved(DevelopmentCard card) {
        if (reserved.contains(card)) {
            reserved.remove(card);
            cards.add(card);
        } else {
            throw new IllegalArgumentException("Card not found in reserved cards");
        }
    }

    public void setTotalPrestigePoints(int totalPrestigePoints) {
        this.totalPrestigePoints = totalPrestigePoints;
    }

    public void remove1GoldBonus() {
        int currentAmount = bonuses.get(GemType.GOLD);
        bonuses.put(GemType.GOLD, currentAmount - 1);
    }

    public void checkIfPlayerHasEnoughTokens(Player player, Game game) {
        Set<NobleCard> nobles = game.getNobles();
        nobles.forEach(noble -> {
            Map<GemType, Integer> nobleCost = noble.getNeededBonuses();
            if (validateNobleCost(player, nobleCost)){
                player.getAcquiredNobles().add(noble);
                player.setTotalPrestigePoints(player.getTotalPrestigePoints() + noble.getPrestigePoints());
                game.getNobles().remove(noble);
            }
        });
    }

    private boolean validateNobleCost(Player player, Map<GemType, Integer> nobleCost) {
        Map<GemType, Integer> playerBonuses = player.getBonuses();

        for (Map.Entry<GemType, Integer> entry : nobleCost.entrySet()) {
            GemType gem = entry.getKey();
            int requiredAmount = entry.getValue();
            int playerBonus = gem == GemType.GOLD ? 0 : playerBonuses.getOrDefault(gem, 0);

            if (playerBonus < requiredAmount) {
                return false;
            }
        }

        return true;
    }


}
