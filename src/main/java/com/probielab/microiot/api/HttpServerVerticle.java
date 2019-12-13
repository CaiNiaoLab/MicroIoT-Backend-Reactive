package com.probielab.microiot.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
  public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  public static final String CONFIG_MICRO_IOT_DB_QUEUE = "microiotdb.queue";
  private String DbQueue = "microiotdb.queue";

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    DbQueue = config().getString(CONFIG_MICRO_IOT_DB_QUEUE, "microiotdb.queue");
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/").handler(res -> res.response().end());
    router.get("/frameworks/:pid").handler(res -> res.response().end("Get FWs"));
    router.post().handler(BodyHandler.create());
    router.post("/frameworks/").handler(res -> res.response().end("Save FWs"));
    int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
    server
      .requestHandler(router::accept)
      .listen(portNumber, ar -> {
        if (ar.succeeded()) {
          LOGGER.info("HTTP server running on port " + portNumber);
          startFuture.complete();
        } else {
          LOGGER.error("Could not start a HTTP server", ar.cause());
          startFuture.fail(ar.cause());
        }
      });
  }
}
