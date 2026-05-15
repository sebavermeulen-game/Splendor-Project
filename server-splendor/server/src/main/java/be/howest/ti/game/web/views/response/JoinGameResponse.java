package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.Game;

public class JoinGameResponse extends AbstractResponseWithHiddenStatus {
    private final String playerToken;
    private final String playerName;
    private final Game game;

    public JoinGameResponse(String playerToken, String playerName, Game game) {
        super(200);
        this.playerToken = playerToken;
        this.playerName = playerName;
        this.game = game;
    }

    public String getPlayerToken() {
        return playerToken;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Game getGame() {
        return game;
    }
}
