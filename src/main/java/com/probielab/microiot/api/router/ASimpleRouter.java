package com.probielab.microiot.api.router;

import com.probielab.microiot.services.ProjectService;
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

    router.route("/data").handler(res -> {
      ProjectService projectService = new ProjectService();
      projectService.postProject("{\"name\":\"test\"}")
        .onSuccess(pg_res -> {
          res.response().end(pg_res);
        });
    });

    return router;
  }
}
