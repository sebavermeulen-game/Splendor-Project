package be.howest.ti.game.logic;

import java.util.EnumMap;
import java.util.Map;

public class Purse {
    private final Map<GemType, Integer> tokens;

    public Purse() {
        this.tokens = new EnumMap<>(GemType.class);
        for (GemType gem : GemType.values()) {
            tokens.put(gem, 0);
        }
    }

    public void addTokens(GemType gem, int amount) {
        tokens.put(gem, tokens.getOrDefault(gem, 0) + amount);
    }

    public int getTokens(GemType gem) {
        return tokens.getOrDefault(gem, 0);
    }

    public void removeTokens(GemType gem, int amount) {
        int currentAmount = tokens.getOrDefault(gem, 0);
        if (currentAmount >= amount) {
            tokens.put(gem, currentAmount - amount);
        } else {
            throw new IllegalArgumentException("Not enough tokens to remove");
        }
    }

    public Map<GemType, Integer> getTokensMap() {
        return tokens;
    }
}