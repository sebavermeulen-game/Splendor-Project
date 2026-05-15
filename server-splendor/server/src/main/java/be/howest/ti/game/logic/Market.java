package be.howest.ti.game.logic;

import be.howest.ti.game.logic.service.MarketException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class Market {

    private static final int MAX_CARDS = 4;
    private static final int TIER_AMOUNT = 3;
    private static final String JSON_OBJECT_NAME = "developments";
    private List<Tier> visibleCards;

    private List<Tier> hiddenCards;
    private final Random random = new SecureRandom();

    public Market() {
        setTierCards();
    }

    private void setTierCards() {
        List<Tier> visibleCardsFillable = new ArrayList<>();
        List<Tier> hiddenCardsFillable = new ArrayList<>();
        for (int i = 1; i <= TIER_AMOUNT; i++) {
            String tierName = "tier" + i;
            Set<DevelopmentCard> selectedVisibleCards = fillVisibleCards(tierName);
            hiddenCardsFillable.add(setHiddenCards(selectedVisibleCards, tierName));
            visibleCardsFillable.add(new Tier(tierName, selectedVisibleCards));
        }
        this.visibleCards = visibleCardsFillable;
        this.hiddenCards = hiddenCardsFillable;
    }

    public Tier setHiddenCards(Set<DevelopmentCard> cards, String tierName) {
            Set<DevelopmentCard> selectedHiddenCards = fillHiddenCards(cards, tierName);
            return new Tier(tierName, selectedHiddenCards);
    }

    private Set<DevelopmentCard> fillVisibleCards(String tier) {
        Set<DevelopmentCard> cards = new HashSet<>();
        while (cards.size() < MAX_CARDS) {
            try {
                JsonNode developments = Data.getJson("/data/Developments.json");
                int index = this.random.nextInt(developments.get(JSON_OBJECT_NAME).get(tier).size());
                JsonNode cardNode = developments.get(JSON_OBJECT_NAME).get(tier).get(index);
                int points = cardNode.get("points").asInt();
                Map<GemType, Integer> cost = Data.retrieveCost(cardNode);
                String name = cardNode.get("name").asText();
                GemType bonus = GemType.mapGemCodeToName(cardNode.get("gem_type").asText());
                int level = cardNode.get("level").asInt();
                DevelopmentCard card = new DevelopmentCard(name, level, bonus, points, cost);
                cards.add(card);
            } catch (IOException e) {
                throw new MarketException("Error reading JSON file", e);
            }
        }
        return cards;
    }

    private Set<DevelopmentCard> fillHiddenCards(Set<DevelopmentCard> visibleCards, String tier){
        Set<DevelopmentCard> cards = new HashSet<>();

        try {
        JsonNode developments = Data.getJson("/data/Developments.json");
        JsonNode tierCards = developments.get(JSON_OBJECT_NAME).get(tier);
        for (JsonNode card : tierCards) {
            int hiddenPoints = card.get("points").asInt();
            Map<GemType, Integer> hiddenCost = Data.retrieveCost(card);
            String hiddenName = card.get("name").asText();
            GemType hiddenBonus = GemType.mapGemCodeToName(card.get("gem_type").asText());
            int hiddenLevel = card.get("level").asInt();
            DevelopmentCard hiddenCard = new DevelopmentCard(hiddenName, hiddenLevel, hiddenBonus, hiddenPoints, hiddenCost);
            if (!visibleCards.contains(hiddenCard)) {
                cards.add(hiddenCard);
            }
        }
        } catch (IOException e) {
            throw new MarketException("Error reading JSON file", e);
        }

        return cards;
    }

    public List<Tier> getVisibleCards() {
        return visibleCards;
    }

    public DevelopmentCard getCardByName(String cardName) {
        for (Tier tier : visibleCards) {
            for (DevelopmentCard card : tier.getCards()) {
                if (card.getName().equals(cardName)) {
                    return card;
                }
            }
        }
        return null;
    }

    public void buyDevelopmentCard(DevelopmentCard card, Player player) {
        if (card == null) {
            throw new IllegalArgumentException("Card not found");
        }
        String tierName = "tier" + card.getLevel();
        for (Tier tier : visibleCards) {
            if (tier.getName().equals(tierName)) {
                tier.getCards().remove(card);
                addNewCardFromHidden(tier);
            }
        }
        player.getCards().add(card);
    }

    public void addNewCardFromHidden(Tier tier) {
        String tierName = tier.getName();

        for (Tier hiddenTier : hiddenCards) {
            if (hiddenTier.getName().equals(tierName)) {
                List<DevelopmentCard> tempHiddenCards = new ArrayList<>(hiddenTier.getCards());
                int randomIndex = random.nextInt(hiddenTier.getCards().size());
                hiddenTier.getCards().remove(tempHiddenCards.get(randomIndex));
                tier.getCards().add(tempHiddenCards.get(randomIndex));
                return;
            }
        }
    }
}