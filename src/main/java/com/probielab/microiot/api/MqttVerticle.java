package com.probielab.microiot.api;

import io.vertx.mqtt.MqttClientOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.mqtt.MqttClient;

public class MqttVerticle extends AbstractVerticle {
  final static MqttClientOptions mqttClientOptions = (MqttClientOptions) new MqttClientOptions()
    .setConnectTimeout(30000)
    .setTcpKeepAlive(true)
    .setTcpFastOpen(true);

  @Override
  public void start() {
    MqttClient.create(vertx)
      .rxConnect(10010, "localhost")
      .doOnSuccess((res) -> {

      });
  }
}
