package be.howest.ti.game.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

public class Data {
    private static final ObjectMapper mapper = new ObjectMapper();

    private Data() {
    }

    public static JsonNode getJson(String filePath) throws IOException {
        URL resource = Market.class.getResource(filePath);
        return mapper.readTree(resource);
    }

    public static Map<GemType, Integer> retrieveCost(JsonNode cardNode) {
        Map<GemType, Integer> cost = new EnumMap<>(GemType.class);
        for (Iterator<Map.Entry<String, JsonNode>> it = cardNode.get("cost").fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            String letter = entry.getKey();
            int amount = entry.getValue().asInt();
            cost.put(GemType.mapGemCodeToName(letter), amount);
        }
        return cost;
    }


}
