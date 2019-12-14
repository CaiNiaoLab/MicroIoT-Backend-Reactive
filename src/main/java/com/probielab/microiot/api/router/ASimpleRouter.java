package com.probielab.microiot.api.router;

import com.probielab.microiot.utils.reactivex.log4vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

import static com.probielab.microiot.api.HttpServerVerticle.CONFIG_MICRO_IOT_DB_QUEUE;

public class ASimpleRouter {
  private static String DbQueue = "microiotdb.queue";

  public static Router getRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    EventBus eb = vertx.eventBus();

    DbQueue = CONFIG_MICRO_IOT_DB_QUEUE;


    router.get("/").handler(res -> res.response().end());
    router.get("/frameworks/:pid").handler(res -> {
      DeliveryOptions deliveryOptions = new DeliveryOptions();
      deliveryOptions
        .setLocalOnly(true)
        .setSendTimeout(1000);
      eb
        .request(DbQueue, new JsonObject().put("query", "SELECT * FROM mi_framework_schema"), deliveryOptions, pg_res -> {
          if (pg_res.succeeded()) {
            log4vertx.info(eb, "Data from pg" + pg_res.result().body());
            res
              .response()
              .end(pg_res.result().body().toString());
          } else {
            res
              .response()
              .end("Sql error");
          }
        });
    });
    router.post().handler(BodyHandler.create());
    router.post("/frameworks/").handler(res -> {
      DeliveryOptions deliveryOptions = new DeliveryOptions();
      deliveryOptions
        .setLocalOnly(true)
        .setSendTimeout(1000);
      eb
        .request(DbQueue,
          new JsonObject()
            .put("query", "INSERT INTO mi_framework_schema")
            .put("data", res.getBodyAsJson()),
          deliveryOptions
          , pg_res -> {
            if (pg_res.succeeded()) {
              log4vertx.info(eb, "Data from pg" + pg_res.result().body());
              res
                .response()
                .end("success");
            } else {
              res
                .response()
                .end("Sql error");
            }
          });
    });

    return router;
  }
}
