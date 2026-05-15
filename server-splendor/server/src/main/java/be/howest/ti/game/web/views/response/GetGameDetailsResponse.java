package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.Game;

public class GetGameDetailsResponse extends AbstractResponseWithHiddenStatus{
    private Game game;

    public GetGameDetailsResponse(Game game) {
        super(200);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
