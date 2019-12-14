package com.probielab.microiot.api.router;

import com.probielab.microiot.api.services.FrameworksService;
import com.probielab.microiot.utils.reactivex.log4vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;


public class ASimpleRouter {

  public static Router getRouter(Vertx vertx) {
    Router router = Router.router(vertx);
    EventBus eb = vertx.eventBus();

    router.get("/").handler(res -> res.response().end());
    router.get("/frameworks/:pid").handler( res -> FrameworksService.getFrameworkData(eb, res));
    router.post().handler(BodyHandler.create());
    router.post("/frameworks/").handler( res -> FrameworksService.putFrameworkData(eb, res));

    return router;
  }
}
