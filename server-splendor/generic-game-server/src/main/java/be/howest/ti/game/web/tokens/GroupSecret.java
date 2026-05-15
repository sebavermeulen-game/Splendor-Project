package be.howest.ti.game.web.tokens;


public class GroupSecret {

    private final String txt;

    public GroupSecret(String secret) {
        txt = secret;
    }

    public String toString() {
        return txt;
    }

}
