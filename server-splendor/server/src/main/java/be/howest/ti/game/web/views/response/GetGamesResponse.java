package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.Game;
import java.util.List;


public class GetGamesResponse extends AbstractResponseWithHiddenStatus {
    private final List<Game> games;
    public GetGamesResponse(List<Game> games) {
        super(200);
        this.games = games;
    }

    public List<Game> getGames() {
        return games;
    }
}
