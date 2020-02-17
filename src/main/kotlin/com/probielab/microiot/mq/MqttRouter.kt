package com.probielab.microiot.mq

import com.probielab.microiot.redis.RedisVerticle
import com.probielab.microiot.services.HardwareService
import com.probielab.microiot.utils.reactivex.log4vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.eventbus.EventBus
import io.vertx.reactivex.mqtt.messages.MqttPublishMessage

class MqttRouter {
  companion object {
    @JvmStatic
    @Throws
    fun mqttEventSwitcher(it: MqttPublishMessage, vertx: Vertx) {
      val eb = vertx.eventBus()
      when (it.topicName()) {
        "/vertex/0" -> {
          //硬件服务更新
          val hardwareInfo = JsonObject(Buffer.buffer(it.toString()))
          HardwareService.getInstance(vertx).createHardware(hardwareInfo.encode(), "")
        }
        //测试
        "/cc3200/ToggleLEDCmdL2" -> {
          log4vertx.info(eb, "[MQTT RES]" + it.payload().toString())
          eb.publish(RedisVerticle.REDIS_EVENT,
            JsonObject()
              .put("id", "test")
              .put("data", it.payload().toString()))
          //存到redis 硬件id_服务名
        }
      }
    }
  }
}
