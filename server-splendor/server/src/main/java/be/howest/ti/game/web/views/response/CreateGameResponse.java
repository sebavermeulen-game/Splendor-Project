package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.Game;

public class CreateGameResponse extends AbstractResponseWithHiddenStatus {
    private final Game game;
    private final String playerName;
    private final String playerToken;
    public CreateGameResponse(Game game, String playerName,String playerToken) {
        super(200);
        this.game = game;
        this.playerName = playerName;
        this.playerToken = playerToken;
    }

    public Game getGame() {
        return game;
    }

    public String getPlayerToken() {
        return playerToken;
    }

    public String getPlayerName() {
        return playerName;
    }
}
