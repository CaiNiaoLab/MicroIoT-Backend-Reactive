package com.probielab.microiot.redis

import com.probielab.microiot.utils.reactivex.log4vertx
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle

class RedisVerticle : AbstractVerticle() {
  companion object {
    final val REDIS_EVENT = "microiotredis.queue"
    final val REDIS_EVENT_GET = "microiotredis.queue.get"
  }

  override fun start() {
    val redis = RedisHelper.getInstance(vertx)

    val eb = vertx.eventBus()
    eb.consumer<JsonObject>(REDIS_EVENT) {
      log4vertx.info(eb, "[REDIS SET]" + it.body().encode())
      redis.setValue(it.body().getString("id"), it.body().getString("data"))
    }

    eb.consumer<JsonObject>(REDIS_EVENT_GET) {
      redis.getValue("test", "[]")
        .onComplete { redis_res ->
          it.reply(redis_res.result())
        }
    }
  }
}
