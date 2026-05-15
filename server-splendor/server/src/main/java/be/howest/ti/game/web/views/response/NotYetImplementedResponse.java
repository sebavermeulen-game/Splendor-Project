package be.howest.ti.game.web.views.response;

public class NotYetImplementedResponse extends AbstractResponseWithHiddenStatus {

    private final String message;

    public NotYetImplementedResponse(String message) {
        super(501);
        this.message = message;
    }

    public String getMessage() {
        return "NYI: " + message;
    }
}
