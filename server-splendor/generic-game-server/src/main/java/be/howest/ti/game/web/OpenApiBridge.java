package be.howest.ti.game.web;

import be.howest.ti.game.logic.GameResourceNotFoundException;
import be.howest.ti.game.logic.GameRuleException;
import be.howest.ti.game.web.tokens.AuthenticationProviderWithTokenManager;
import be.howest.ti.game.web.tokens.TokenManager;
import be.howest.ti.game.web.views.response.AbstractResponseWithHiddenStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.BearerAuthHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.validation.BadRequestException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class OpenApiBridge {

    private static final Logger LOGGER = Logger.getLogger(OpenApiBridge.class.getName());

    private final Map<String, AuthenticationHandler> securityHandlers = new HashMap<>();

    private TokenManager tokenManager;

    protected void setSecurityHandlers(Map<String, AuthenticationHandler> handlers) {
        this.securityHandlers.putAll(handlers);
    }

    protected void installPlayerTokenManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        setSecurityHandlers(Map.ofEntries(
                Map.entry("playerToken", BearerAuthHandler.create(
                        new AuthenticationProviderWithTokenManager(tokenManager)
                ))
        ));
    }

    protected <T> String createToken(T user) {
        return tokenManager.createToken(user);
    }

    public Router buildRouter(RouterBuilder routerBuilder) {
        LOGGER.log(Level.FINE, "Installing CORS handlers");
        routerBuilder.rootHandler(createCorsHandler());

        LOGGER.log(Level.FINE, "Installing body-handler handler");
        routerBuilder.rootHandler(BodyHandler.create());

        LOGGER.log(Level.FINE, "Installing security handlers");
        securityHandlers.forEach(routerBuilder::securityHandler);

        LOGGER.log(Level.FINE, "Installing General Failure handlers");
        routerBuilder.operations().forEach(op -> op.failureHandler(this::onFailedRequest));

        LOGGER.log(Level.FINE, "Installing API-Handlers handlers");
        routerBuilder.operations().forEach(this::installHandlers);


        LOGGER.log(Level.INFO, "All handlers are installed");
        return routerBuilder.createRouter();
    }

    private void installHandlers(io.vertx.ext.web.openapi.Operation operation) {
        Stream.of(this.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Operation.class))
                .filter(method -> method.getAnnotation(Operation.class).value().equals(operation.getOperationId()))
                .map(this::method2handler)
                .forEach(operation::handler);
    }

    private Handler<RoutingContext> method2handler(Method method) {
        Function<RoutingContext,?> mapper;
        try {
            mapper = createMapper(method);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("Operation handler method has unexpected signature (parameter)", e);
        }

        if (!AbstractResponseWithHiddenStatus.class.isAssignableFrom(method.getReturnType())) {
            throw new IllegalArgumentException("Operation handler method has unexpected signature (return type: "
                    + method.getReturnType().getName() + ")");
        }

        return ctx -> {
            try {
                Object wrappedCtx = mapper.apply(ctx);
                LOGGER.log(Level.INFO, ()->String.format("Calling method %s with as arg %s", method.getName(),wrappedCtx.getClass().getName()));
                AbstractResponseWithHiddenStatus responseObject = (AbstractResponseWithHiddenStatus) method.invoke(this, wrappedCtx);
                sendJson(ctx, responseObject.getStatus(), responseObject);
            } catch (IllegalAccessException e) {
                ctx.fail(e);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof RuntimeException){
                    throw (RuntimeException) e.getCause();
                } else {
                    ctx.fail(e);
                }
            }
        };
    }

    private Function<RoutingContext,?> createMapper(Method method) {
        Class<?> expectedType = method.getParameterTypes()[0];
        if (expectedType.equals(RoutingContext.class)) return ctx -> ctx;

        try {
            Constructor<?> constructor = expectedType.getConstructor(RoutingContext.class);
            return ctx -> {
                try {
                    return constructor.newInstance(ctx);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException | SecurityException |ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException("Operation handler method has unexpected parameter type", e);
        }
    }

    private void onFailedRequest(RoutingContext ctx) {
        Throwable cause = ctx.failure();
        int code = ctx.statusCode();
        String quote = Objects.isNull(cause) ? "" + code : cause.getMessage();

        // Map custom runtime exceptions to a HTTP status code.

        if (cause instanceof BadRequestException) {
            // throw new RequestPredicateException("message", cause); when you manually found some wrong data in a request
            code = ctx.statusCode(); // code should be 400
        } else if (cause instanceof IllegalArgumentException) {
            code = 400;
        } else if (cause instanceof ForbiddenAccessException) {
            code = 403;
        } else if (cause instanceof GameResourceNotFoundException) {
            code = 404;
        } else if (cause instanceof GameRuleException) {
            code = 409;
        } else {
            LOGGER.log(Level.SEVERE, () -> "Unanticipated Failed request: "+ quote +"\n" + cause.getStackTrace());
        }

        sendFailure(ctx, code, quote);
    }

    protected CorsHandler createCorsHandler() {
        return CorsHandler.create()
                .addRelativeOrigin(".*.")
                .allowedHeader("x-requested-with")
                .allowedHeader("x-group-secret")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowCredentials(true)
                .allowedHeader("origin")
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization")
                .allowedHeader("accept")
                .allowedMethod(HttpMethod.HEAD)
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.PUT);
    }

    protected void sendJson(RoutingContext ctx, int statusCode, Object response) {
        ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(statusCode)
                .end(Json.encodePrettily(response));
    }

    protected void sendFailure(RoutingContext ctx, int code, String quote) {
        sendJson(ctx, code, new JsonObject()
                .put("failure", code)
                .put("cause", quote));
    }
}
