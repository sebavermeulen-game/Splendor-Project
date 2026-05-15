package be.howest.ti.game.web.views.response;

import be.howest.ti.game.logic.Info;

@SuppressWarnings("unused")
public class GetInfoResponse extends AbstractResponseWithHiddenStatus {

    public GetInfoResponse() {
        super(200);
    }

    public String[] getAuthors(){
        return Info.getAuthors();
    }
}
