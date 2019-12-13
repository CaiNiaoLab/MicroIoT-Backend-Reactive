package com.probielab.microiot;

import com.probielab.microiot.api.HttpServerVerticle;
import com.probielab.microiot.utils.SqlHelperVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    DeploymentOptions httpServerOptions = new DeploymentOptions();
    httpServerOptions.setInstances(2);
    vertx.deployVerticle(HttpServerVerticle.class, httpServerOptions);

    DeploymentOptions sqlOptions = new DeploymentOptions();
    vertx.deployVerticle(SqlHelperVerticle.class, sqlOptions);
  }
}
