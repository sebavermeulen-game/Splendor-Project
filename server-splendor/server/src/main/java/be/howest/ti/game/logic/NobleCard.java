package be.howest.ti.game.logic;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.*;

public class NobleCard {
    private final String name;
    private final Map<GemType, Integer> neededBonuses;


    private final int prestigePoints;

    public NobleCard(String name, Map<GemType, Integer> neededBonuses, int prestigePoints) {
        this.name = name;
        this.neededBonuses = neededBonuses;
        this.prestigePoints = prestigePoints;
    }

    public String getName() {
        return name;
    }

    public Map<GemType, Integer> getNeededBonuses() {
        return neededBonuses;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }
    public static List<NobleCard> loadNoblesFromFile() throws IOException {
        JsonNode root = Data.getJson( "/data/Nobles.json" );

        JsonNode noblesArray = root.get("nobles");

        List<NobleCard> nobles = new ArrayList<>();
        for (JsonNode node : noblesArray) {
            String name = node.get("name").asText();
            int points = Card.getPoints(node);
            Map<GemType,Integer> cost = Data.retrieveCost(node);

            nobles.add(new NobleCard(name, cost, points));
        }
        return nobles;
    }
}
