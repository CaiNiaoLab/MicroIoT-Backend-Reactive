package com.probielab.microiot.mq

import com.probielab.microiot.redis.RedisVerticle
import com.probielab.microiot.services.HardwareService
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.eventbus.EventBus
import io.vertx.reactivex.mqtt.messages.MqttPublishMessage

class MqttRouter {
  companion object {
    @JvmStatic
    @Throws
    fun mqttEventSwitcher(it: MqttPublishMessage, eb: EventBus) {
      when (it.topicName()) {
        "/vertex/0" -> {
          //硬件服务更新
          val hardwareInfo = JsonObject(Buffer.buffer(it.toString()))
          HardwareService.getInstance().createHardware(hardwareInfo.encode(), "")
        }
        //测试
        "/cc3200/ToggleLEDCmdL2" -> {
          eb.publish(RedisVerticle.REDIS_EVENT, JsonObject().put("id", "test")
            .put("data", it.payload()))
          //存到redis 硬件id_服务名
        }
      }
    }
  }
}
