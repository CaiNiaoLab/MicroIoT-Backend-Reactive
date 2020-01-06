package com.probielab.microiot.services;

import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Promise;

public class HardwareService extends BaseService {
  final String GET_RETURN_ID = "') RETURNING pid";
  final String GET_HARDWARE_LIST = "SELECT * FROM mi_hardware WHERE uid='${uid}'";
  final String POST_HARDWARE_LIST = "INSERT INTO mi_hardware VALUES (generate_uuid(32), '${json}','${uid}'";

  public Future<String> createUser(String json, String uid) {
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
}
