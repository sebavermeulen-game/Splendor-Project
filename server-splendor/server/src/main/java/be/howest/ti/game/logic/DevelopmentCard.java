package be.howest.ti.game.logic;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.*;

public class DevelopmentCard extends Card {
    private final int level;
    private final GemType bonus;
    private final String name;
    private final Map<GemType, Integer> cost;


    public DevelopmentCard(String name, int level, GemType bonus, int prestigePoints, Map<GemType, Integer> cost) {
        super(prestigePoints);
        this.name = name;
        this.level = level;
        this.bonus = bonus;
        this.cost = cost;
    }

    public int getLevel() {
        return level;
    }

    public GemType getBonus() {
        return bonus;
    }

    public String getName() {
        return name;
    }

    @Override
    public Map<GemType, Integer> getCost() {
        return cost;
    }

    public static List<DevelopmentCard> loadDevelopmentCardsFromFile() throws IOException {
        JsonNode root = Data.getJson("/data/Developments.json");

        List<DevelopmentCard> cards = new ArrayList<>();
        JsonNode developmentsNode = root.get("developments");
        developmentsNode.fields().forEachRemaining(tierEntry -> {
            JsonNode tierArray = tierEntry.getValue();
            for (JsonNode node : tierArray) {
                String name = node.get("name").asText();
                int level = node.get("level").asInt();
                String gemTypeStr = node.get("gem_type").asText();
                GemType bonus = GemType.mapGemCodeToName(gemTypeStr);
                int points = Card.getPoints(node);
                Map<GemType, Integer> cost = Data.retrieveCost(node);
                cards.add(new DevelopmentCard(name, level, bonus, points, cost));
            }
        });
        return cards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DevelopmentCard that = (DevelopmentCard) o;
        return level == that.level && Objects.equals(name, that.name) && bonus == that.bonus && Objects.equals(cost, that.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, level, bonus, cost);
    }
}
