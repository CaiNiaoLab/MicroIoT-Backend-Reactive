package com.probielab.microiot.services;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;

public class HardwareService extends BaseService {
  final String GET_RETURN_ID = "') RETURNING pid";
  final String GET_HARDWARE_LIST = "SELECT * FROM mi_hardware WHERE uid='${uid}'";
  final String POST_HARDWARE_LIST = "INSERT INTO mi_hardware VALUES (generate_uuid(32), '${json}','${uid}'";

  public Future<String> createHardware(String json, String uid) {
    Promise<String> promise = Promise.promise();
    client.query(POST_HARDWARE_LIST.replace("${json}", json).replace("${uid}", uid), res -> {
      if (res.succeeded()) {
        promise.complete(res.result().getDelegate().iterator().next().toString());
      } else {
        promise.fail(res.cause());
      }
    });
    return promise.future();
  }

  public Future<JsonArray> getHardwareList(String uid, String token) {
    Promise<JsonArray> promise = Promise.promise();
    client.query(GET_HARDWARE_LIST.replace("${uid}", uid), res -> {
      if (res.succeeded()) {
        RowIterator iterator = res.result().getDelegate().iterator();
        JsonArray resultArray = new JsonArray();
        while (iterator.hasNext()) {
          Row next = (Row) iterator.next();
          JsonObject resultItem = new JsonObject();
          resultItem.put(next.getColumnName(0), next.getValue(0));
          resultArray.add(resultItem);
        }
        promise.complete(resultArray);
      } else {
        promise.fail(res.cause());
      }
    });
    return promise.future();
  }
}
