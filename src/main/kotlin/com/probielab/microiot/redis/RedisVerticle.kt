package com.probielab.microiot.redis

import io.vertx.reactivex.core.AbstractVerticle

class RedisVerticle : AbstractVerticle() {
  override fun start() {
    RedisHelper.getInstance(vertx);
  }
}
