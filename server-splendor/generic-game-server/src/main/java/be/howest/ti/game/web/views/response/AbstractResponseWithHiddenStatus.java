package be.howest.ti.game.web.views.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AbstractResponseWithHiddenStatus {

    private final int status;


    public AbstractResponseWithHiddenStatus(int status) {
        this.status = status;
    }

    @JsonIgnore
    public int getStatus() {
        return status;
    }
}
