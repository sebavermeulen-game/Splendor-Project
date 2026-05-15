package be.howest.ti.game.logic;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public class Card {
    private Map<GemType, Integer> cost;
    private final int prestigePoints;

    public Card (int prestigePoints) {
        this.cost = new EnumMap<>(GemType.class);
        this.prestigePoints = prestigePoints;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public Map<GemType, Integer> getCost() {
        return cost;
    }

    public int getGemCostAmount(GemType gem) {
        return cost.getOrDefault(gem, 0);
    }

    public void setCost(Map<GemType, Integer> cost) {
        this.cost = cost;
    }


    public static int getPoints(JsonNode node) {
        return node.get("points").asInt();
    }

    @Override
    public String toString() {
        return "{" +
                "cost:" + cost +
                ", prestigePoints:" + prestigePoints +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;
        return prestigePoints == card.prestigePoints &&
                Objects.equals(cost, card.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cost, prestigePoints);
    }
}
