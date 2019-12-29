package com.probielab.microiot.api.services;

import com.probielab.microiot.utils.reactivex.SQLBuilder;
import com.probielab.microiot.utils.reactivex.log4vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.RoutingContext;

public class FrameworksService extends BaseService {

  public static void getFrameworkData(EventBus eb, RoutingContext res) {
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
  }

  public static void putFrameworkData(EventBus eb, RoutingContext res) {
    DeliveryOptions deliveryOptions = new DeliveryOptions();
    deliveryOptions
      .setLocalOnly(true)
      .setSendTimeout(1000);
    JsonObject putSql = new JsonObject()
      .put("query", "INSERT INTO mi_framework_schema")
      .put("jsonData", res.getBodyAsJson());
    eb
      .request(DbQueue, SQLBuilder.InsertFromJson(putSql), deliveryOptions, pg_res -> {
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
  }
}
