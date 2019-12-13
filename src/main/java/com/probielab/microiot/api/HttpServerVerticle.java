package com.probielab.microiot.api;

import com.probielab.microiot.utils.log4vertx;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.impl.CodecManager;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class HttpServerVerticle extends AbstractVerticle {
  public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  public static final String CONFIG_MICRO_IOT_DB_QUEUE = "microiotdb.queue";
  private String DbQueue = "microiotdb.queue";

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    EventBus eb = vertx.eventBus();

    DbQueue = config().getString(CONFIG_MICRO_IOT_DB_QUEUE, "microiotdb.queue");
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/").handler(res -> res.response().end());
    router.get("/frameworks/:pid").handler(res -> {
      DeliveryOptions deliveryOptions = new DeliveryOptions();
      deliveryOptions
        .setLocalOnly(true)
        .setSendTimeout(1000);
      eb
        .request(DbQueue, new JsonObject().put("query", "SELECT * FROM mi_framework_schema"),deliveryOptions ,pg_res -> {
          if (pg_res.succeeded()) {
            log4vertx.info(eb, "Data from pg" + pg_res.result().body());
            res
              .response()
              .end("Get FWs");
          } else {
            res
              .response()
              .end("Sql error");
          }
        });
    });
    router.post("/frameworks/").handler(res -> {
      DeliveryOptions deliveryOptions = new DeliveryOptions();
      deliveryOptions
        .setLocalOnly(true)
        .setSendTimeout(1000);
      eb
        .request(DbQueue, new JsonObject().put("query", "SELECT * FROM mi_framework_schema"),deliveryOptions ,pg_res -> {
          if (pg_res.succeeded()) {
            log4vertx.info(eb, "Data from pg" + pg_res.result().body());
            res
              .response()
              .end("Get FWs");
          } else {
            res
              .response()
              .end("Sql error");
          }
        });
    });
    router.post().handler(BodyHandler.create());
    router.post("/frameworks/").handler(res -> res.response().end("Save FWs"));
    int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
    server
      .requestHandler(router::accept)
      .listen(portNumber, ar -> {
        if (ar.succeeded()) {
          log4vertx.info(eb, "Http Server running on port:" + portNumber);
        } else {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          try {
            ar.cause().printStackTrace(new PrintStream(baos));
            baos.close();
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            log4vertx.error(eb, "Cannot start http server", ar.cause());
          }
        }
      });
  }
}
