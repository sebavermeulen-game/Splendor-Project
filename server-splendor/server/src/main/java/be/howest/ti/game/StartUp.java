package be.howest.ti.game;

import be.howest.ti.game.util.Config;
import be.howest.ti.game.web.SplendorOpenApiBridge;
import be.howest.ti.game.web.OpenApiBridge;
import be.howest.ti.game.web.Webserver;
import io.vertx.core.AbstractVerticle;


public class StartUp extends AbstractVerticle {

    private final String path2spec;
    private final OpenApiBridge bridge;


    public StartUp(String path2spec, OpenApiBridge bridge) {
        this.path2spec = path2spec;
        this.bridge = bridge;
    }

    public StartUp() {
        this(
                Config.getString("spec.url"),
                new SplendorOpenApiBridge()
        );
    }

    @Override
    public void start() {
        vertx.deployVerticle(new Webserver(Config.getInteger("server.port"),
                path2spec,
                bridge));
    }

}
