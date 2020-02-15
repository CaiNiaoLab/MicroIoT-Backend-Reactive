package com.probielab.microiot.services;

import com.probielab.microiot.utils.reactivex.log4vertx;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Promise;


public class ProjectService extends BaseService {
  final String POST_APP = "INSERT INTO mi_project VALUES (generate_uuid(32), '";
  final String GET_APP = "SELECT project_data FROM mi_project where pid='${pid}'";

  final String GET_RETURN_ID = "') RETURNING pid";

  private static ProjectService projectService;

  public static ProjectService getInstance() {
    if (projectService == null) {
      projectService = new ProjectService();
    }
    return projectService;
  }

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

  public Future<String> getProject(String pid) {
    Promise<String> promise = Promise.promise();
    client.query(GET_APP.replace("${pid}", pid), res -> {
      if (res.succeeded()) {
        promise.complete(res.result().getDelegate().iterator().next().toString());
      } else {
        promise.fail(res.cause());
      }
    });
    return promise.future();
  }
}
