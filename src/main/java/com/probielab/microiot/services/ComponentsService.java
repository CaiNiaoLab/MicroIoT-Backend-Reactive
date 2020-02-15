package com.probielab.microiot.services;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;

public class ComponentsService extends BaseService {
  final static String GET_COMPONENT = "SELECT * FROM mi_component WHERE pid='${pid}'";
  final static String POST_COMPONENT = "INSERT INTO mi_component VALUES (generate_uuid(32), '${json}','${pid}'";
  final String GET_RETURN_ID = "') RETURNING pid";


  public ComponentsService(Vertx vertx) {
    super(vertx);
  }

  private static ComponentsService componentsService;

  public static ComponentsService getInstance(Vertx vertx) {
    if (componentsService == null) {
      componentsService = new ComponentsService(vertx);
    }
    return componentsService;
  }

  public Future<String> getComponent(String uid, String pid) {
    Promise promise = Promise.promise();
    client.query(GET_COMPONENT
      .replace("${pid}", pid), pg_res -> {
      if (pg_res.succeeded()) {
        JsonArray resultArray = new JsonArray();
        pg_res.result().forEach(res_row -> {
          JsonObject resultItem = new JsonObject();
          resultItem.put(res_row.getColumnName(0), res_row.getValue(0));
          resultItem.put(res_row.getColumnName(1), res_row.getValue(1));
          resultArray.add(resultItem);
        });
        promise.complete(resultArray);
      } else {
        promise.fail(pg_res.cause());
      }
    });
    return promise.future();
  }

  public Future<String> createComponent(String uid, String pid, String json) {
    Promise<String> promise = Promise.promise();
    client.query(POST_COMPONENT.replace("${json}", json)
      .replace("${pid}", pid) + GET_RETURN_ID, res -> {
      if (res.succeeded()) {
        promise.complete(res.result().getDelegate().iterator().next().toString());
      } else {
        promise.fail(res.cause());
      }
    });
    return promise.future();
  }
}
