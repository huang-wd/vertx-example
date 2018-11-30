package io.vertx.book.http;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpRequest;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import rx.Single;

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
        HttpRequest<JsonObject> request1 = client
                .get(8080, "localhost", "/Luke")
                .as(BodyCodec.jsonObject());

        HttpRequest<JsonObject> request2 = client
                .get(8080, "localhost", "/Leia")
                .as(BodyCodec.jsonObject());


        Single<JsonObject> s1 = request1.rxSend()
                .map(jsonObjectHttpResponse -> jsonObjectHttpResponse.body());

        Single<JsonObject> s2 = request2.rxSend()
                .map(jsonObjectHttpResponse -> jsonObjectHttpResponse.body());

        Single.zip(s1, s2, (entries, entries2) -> {
            return new JsonObject()
                    .put("luke", entries.getString("message"))
                    .put("leia", entries2.getString("message"));
        })
                .subscribe(
                        entries -> rc.response().end(entries.encodePrettily()),
                        throwable -> {
                            throwable.printStackTrace();
                            rc.response().setStatusCode(500).end(throwable.getMessage());
                        });
    }

}
