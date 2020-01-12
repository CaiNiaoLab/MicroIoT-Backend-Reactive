package com.probielab.microiot.mq;

import com.probielab.microiot.utils.reactivex.log4vertx;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.mqtt.MqttClient;

public class MqttVerticle extends AbstractVerticle {

  MqttClient connect;
  @Override
  public void start() {
    JsonObject entries = new JsonObject();
    entries.put("identifier", "MicroIoTData")
      .put("name", "Control")
      .put("desc", "Downlink")
      .put("type", "bool")
      .put("data", "0");
    connect = MqttClient.create(vertx)
      .connect(1883, "tcp://112.74.165.209", res -> {
        if (res.succeeded()) {
          log4vertx.info(vertx.eventBus(), res.result().toString());
          connect
            .publish("/cc3200/ToggleLEDCmdL2", Buffer.buffer(entries.toString()), MqttQoS.EXACTLY_ONCE, true, true);
        } else {
          log4vertx.error(vertx.eventBus(), "mqtt error", res.cause());
        }
      });
  }
}
