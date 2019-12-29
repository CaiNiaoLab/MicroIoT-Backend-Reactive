package com.probielab.microiot;

import com.probielab.microiot.api.HttpServerVerticle;
import com.probielab.microiot.orm.SqlHelperVerticle;
import com.probielab.microiot.mq.MqttHelperVerticle;
import com.probielab.microiot.ws.WebsocketServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


public class MicroIoTLauncher extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MicroIoTLauncher.class);

  @Override
  public void start(Promise<Void> startPromise) {
//    DeploymentOptions httpServerOptions = new DeploymentOptions();
//    httpServerOptions.setInstances(1);
//    vertx.deployVerticle(HttpServerVerticle.class, httpServerOptions);

    DeploymentOptions sqlOptions = new DeploymentOptions();
    sqlOptions.setInstances(1);
    vertx.deployVerticle(SqlHelperVerticle.class, sqlOptions);

//    DeploymentOptions mqttOptions = new DeploymentOptions();
//    sqlOptions.setInstances(1);
//    vertx.deployVerticle(MqttHelperVerticle.class, mqttOptions);

    DeploymentOptions wsOptions = new DeploymentOptions();
    wsOptions.setInstances(1);
    vertx.deployVerticle(WebsocketServer.class, wsOptions);

    EventBus eb = vertx.eventBus();

    MessageConsumer<JsonObject> system = eb.consumer("system");

    system.handler(res -> {
      JsonObject systemEvent = res.body();
      switch (systemEvent.getInteger("code")) {
        case 0:
          LOGGER.info("[INFO]: " + systemEvent.getString("message"));
          break;
        case 999:
          LOGGER.debug("[DEBUG]: " + systemEvent.getString("message"));
          break;
        case -1:
          LOGGER.error("[ERROR]: "+systemEvent.getString("message") + "\nStack Tree:\n" + systemEvent.getString("error"));
          break;
        default:
          LOGGER.info("[UNKNOWN]: " + systemEvent.getInteger("code") + systemEvent.getString("message") + "\nStack Tree:\n" + systemEvent.getString("error"));
      }
    });
  }
}
