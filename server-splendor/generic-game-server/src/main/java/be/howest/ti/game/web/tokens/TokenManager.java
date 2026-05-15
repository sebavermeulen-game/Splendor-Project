package be.howest.ti.game.web.tokens;

public interface TokenManager {

    <T> String createToken(T user);

    <T> T parseToken(String token);

}
