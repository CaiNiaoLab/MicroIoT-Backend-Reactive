package com.probielab.microiot.mq

import com.probielab.microiot.services.HardwareService
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.mqtt.messages.MqttPublishMessage

class MqttRouter {
  companion object {
    @JvmStatic
    @Throws
    fun mqttEventSwitcher(it: MqttPublishMessage) {
      when (it.topicName()) {
        "硬件服务注册" -> {
          //硬件服务更新
          val hardwareInfo = JsonObject(Buffer.buffer(it.toString()))
          HardwareService.getInstance().createHardware(hardwareInfo.encode(), "")
        }
        "硬件数据到达" -> {
          //存到redis 硬件id_服务名
        }
      }
    }
  }
}
