package com.probielab.microiot.services;

import com.probielab.microiot.utils.reactivex.log4vertx;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Promise;


public class ProjectService extends BaseService {
  final String POST_APP = "INSERT INTO mi_project VALUES (generate_uuid(32), '";
  final String GET_APP = "";

  final String GET_RETURN_ID = "') RETURNING pid";

  public Future<String> postProject(String json) {
    Promise<String> result = Promise.promise();
    client.query(POST_APP + json + GET_RETURN_ID, res -> {
      if (res.succeeded()) {
        result.complete(res.result().getDelegate().iterator().next().toString());
      } else {
        result.fail(res.cause());
      }
    });
    return result.future();
  }
}
