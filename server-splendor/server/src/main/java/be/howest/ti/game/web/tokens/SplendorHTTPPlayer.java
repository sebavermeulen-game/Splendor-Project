package be.howest.ti.game.web.tokens;

import io.vertx.ext.auth.impl.UserImpl;

public class SplendorHTTPPlayer extends UserImpl {


    private final int gameId;
    private final String playerName;

    public SplendorHTTPPlayer(int gameId, String playerName) {
        this.gameId = gameId;
        this.playerName = playerName;

    }

    public int getGameId() {
        return gameId;
    }

    public String getPlayerName() {
        return playerName;
    }
}
