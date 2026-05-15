package be.howest.ti.game.logic;

public enum GemType {
    GOLD,
    EMERALD,
    SAPPHIRE,
    RUBY,
    DIAMOND,
    ONYX;

    public static GemType mapGemCodeToName(String code) {
        return switch (code) {
            case "R" -> RUBY;
            case "S" -> SAPPHIRE;
            case "E" -> EMERALD;
            case "C" -> DIAMOND;
            case "O" -> ONYX;
            case "G" -> GOLD;
            default -> throw new IllegalArgumentException("Invalid gem code: " + code);
        };
    }
    @Override
    public String toString() {
        return name();
    }
}