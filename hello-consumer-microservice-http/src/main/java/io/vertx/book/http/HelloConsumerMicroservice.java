package io.vertx.book.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

/**
 * mvn compile vertx:run
 */
public class HelloConsumerMicroservice extends AbstractVerticle {
    private WebClient client;

    @Override
    public void start() {
        client = WebClient.create(vertx);
        Router router = Router.router(vertx);
        router.get("/").handler(event -> {
            invokeMyFirstMicroservice(event);
        });

        vertx.createHttpServer()
                .requestHandler(event -> {
                    router.accept(event);
                })
                .listen(8081);
    }

    private void invokeMyFirstMicroservice(RoutingContext rc) {
        HttpRequest<JsonObject> request = client
                .get(8080, "localhost", "/vert")
                .as(BodyCodec.jsonObject());

        request.send(event -> {
            if (event.failed()) {
                rc.fail(event.cause());
            } else {
                rc.response().end(event.result().body().encode());
            }
        });
    }

}
