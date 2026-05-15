package be.howest.ti.game.web.tokens;

public class PlainTextTokens implements TokenManager {

    private static final int TOKEN_GAME_ID_PART = 0;
    private static final int TOKEN_PLAYER_PART = 1;
    private static final int TOKEN_EXPECTED_PARTS = 2;

    public String createToken(SplendorHTTPPlayer user) {
        return String.format("%s_%s", user.getGameId(), user.getPlayerName());
    }

    @Override
    public <T> String createToken(T user) {
        if (user instanceof SplendorHTTPPlayer) {
            return createToken((SplendorHTTPPlayer) user);
        } else {
            throw new IllegalArgumentException("Unsupported user type");
        }
    }

    public SplendorHTTPPlayer parseToken(String token) {
        String[] parts = token.split("_");

        if (parts.length != TOKEN_EXPECTED_PARTS) {
            throw new InvalidTokenException();
        }

        return new SplendorHTTPPlayer(Integer.parseInt(parts[TOKEN_GAME_ID_PART]), parts[TOKEN_PLAYER_PART]);
    }


}
