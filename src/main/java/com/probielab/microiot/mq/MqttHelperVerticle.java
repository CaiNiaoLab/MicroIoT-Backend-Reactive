package com.probielab.microiot.mq;

import com.probielab.microiot.utils.reactivex.log4vertx;
import com.sun.istack.Nullable;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.mqtt.MqttClient;

public class MqttHelperVerticle extends AbstractVerticle {
  private final static String MQTT_SERVER_HOST = "0.0.0.0";
  private final static Integer MQTT_SERVER_PORT = 10010;

  public final static String CONFIG_MICRO_IOT_MQTT_QUEUE = "microiot.mqtt.queue";

  MessageConsumer<JsonObject> mqttConsumer;

  @Override
  public void start() {
    MqttClientOptions mqttClientOptions = new MqttClientOptions();
    mqttClientOptions
      .setWillQoS(2)
      .setAutoGeneratedClientId(false)
      .setClientId("Vert.x.mqtt.client");
    MqttClient mqttClient = MqttClient.create(vertx, mqttClientOptions);

    EventBus eb = vertx.eventBus();

    mqttClient.connect(MQTT_SERVER_PORT, MQTT_SERVER_HOST, res -> {
      if (res.succeeded()) {
        log4vertx.info(eb, "MQTT connected! Host" + MQTT_SERVER_HOST + " Port:" + MQTT_SERVER_PORT + " ClientId:" + mqttClientOptions.getClientId());
      } else {
        log4vertx.error(eb, "MQTT connect failed! Host" + MQTT_SERVER_HOST + " Port:" + MQTT_SERVER_PORT + " ClientId:" + mqttClientOptions.getClientId(), res.cause());
      }
    });

    mqttConsumer = eb.consumer(CONFIG_MICRO_IOT_MQTT_QUEUE);

    mqttConsumer.completionHandler(res -> {
      if (res.succeeded()) {
        log4vertx.info(eb, "MQTT ready");
      } else {
        log4vertx.error(eb, "MQTT Consume error", res.cause());
      }
    });

    mqttConsumer.handler(res -> {
      if (!mqttClient.isConnected()) {
        res.reply(new JsonObject().put("result", "Mqtt server unconnected!"));
      } else {
        JsonObject mqttRequest = res.body();
        publishMessage(eb, mqttClient, mqttRequest.getString("topic"), mqttRequest.getString("message"))
          .setHandler(publishRes -> {
            if (publishRes.succeeded()) {
              res.reply(publishRes.result());
            } else {
              res.reply("Unknown ERROR");
            }
          });
      }
    });
  }

  private static Future<JsonObject> publishMessage(EventBus eb, MqttClient mqttClient, String topic, String message) {
    Promise<JsonObject> mqttResult = Promise.promise();

    mqttClient.rxPublish(topic, Buffer.buffer(), MqttQoS.AT_MOST_ONCE, false, false)
      .doOnSuccess(res -> successHandler(mqttResult))
      .doOnError(res -> errorHandler(mqttResult))
      .doOnTerminate( () -> errorHandler(mqttResult))
      .doOnEvent((res, throwable) -> errorHandler(mqttResult, res.toString(), throwable, eb));

    return mqttResult.future();
  }

  private static void publishMessage(MqttClient mqttClient, String topic, Integer message) {

  }

  private static void publishMessage(MqttClient mqttClient, String topic, Byte[] message) {

  }

  private static void successHandler(Promise<JsonObject> promise) {
    promise.complete(new JsonObject().put("code", 0));
  }

  private static void errorHandler(Promise<JsonObject> promise) {
    promise.complete(new JsonObject().put("code", -1));
  }

  private static void errorHandler(Promise<JsonObject> promise, @Nullable String message, @Nullable Throwable throwable, @Nullable EventBus eb) {
    promise.complete(new JsonObject().put("code", -1));
    if (throwable != null)
      log4vertx.error(eb, "MQTT Send ERROR on event:" + message, throwable);
  }
}
