package com.probielab.microiot

import com.probielab.microiot.api.HttpServerVerticle
import com.probielab.microiot.mq.MqttVerticle
import com.probielab.microiot.orm.SqlHelperVerticle
import com.probielab.microiot.redis.RedisVerticle
import com.probielab.microiot.ws.WebsocketVerticle
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory

class MainVerticle : AbstractVerticle() {
  @Throws(Exception::class)
  override fun start(startPromise: Promise<Void>) {
    val httpServerOptions = DeploymentOptions()
    httpServerOptions.instances = 1
    vertx.deployVerticle(HttpServerVerticle::class.java, httpServerOptions)
    vertx.deployVerticle(WebsocketVerticle::class.java, httpServerOptions)
    //模拟分布式
    val mqttOptions = DeploymentOptions()
    mqttOptions.instances = 1
    vertx.deployVerticle(MqttVerticle::class.java, mqttOptions)
    vertx.deployVerticle(RedisVerticle::class.java, mqttOptions)
    //启动SQL源
    //vertx.deployVerticle(SqlHelperVerticle::class.java, mqttOptions)
    val eb = getVertx().eventBus()
    val system = eb.consumer<JsonObject>("system")
    system.handler { res: Message<JsonObject> ->
      val systemEvent = res.body()
      when (systemEvent.getInteger("code")) {
        0 -> LOGGER.info("[INFO]: " + systemEvent.getString("message"))
        999 -> LOGGER.debug("[DEBUG]: " + systemEvent.getString("message"))
        -1 -> LOGGER.error("[ERROR]: " + systemEvent.getString("message") + "\nStack Tree:\n" + systemEvent.getString("error"))
        else -> LOGGER.info("[UNKNOWN]: " + systemEvent.getInteger("code") + systemEvent.getString("message") + "\nStack Tree:\n" + systemEvent.getString("error"))
      }
    }
  }

  companion object {
    private val LOGGER = LoggerFactory.getLogger(MainVerticle::class.java)
  }
}
