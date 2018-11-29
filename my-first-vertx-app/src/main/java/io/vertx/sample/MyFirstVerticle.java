package io.vertx.sample;

import io.vertx.core.AbstractVerticle;

/**
 *
 */
public class MyFirstVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer()
                .requestHandler(event -> {
                    event.response()
                            .putHeader("content-type", "text/plain")
                            .end("Hello from Vert.x!");
                }).listen(8080);

        System.out.println("HTTP server started on port 8080");
    }
}
