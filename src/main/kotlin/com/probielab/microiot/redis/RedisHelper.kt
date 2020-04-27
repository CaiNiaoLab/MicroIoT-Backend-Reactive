package com.probielab.microiot.redis

import io.vertx.core.net.SocketAddress
import io.vertx.reactivex.core.Future
import io.vertx.reactivex.core.Promise
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.redis.RedisClient
import io.vertx.reactivex.redis.client.Redis
import io.vertx.redis.client.RedisOptions

class RedisHelper {
  companion object {
    private lateinit var redisHelper: RedisHelper;
    @JvmStatic
    fun getInstance(vertx: Vertx): RedisHelper {
      redisHelper = RedisHelper(vertx)
      return redisHelper
    }
  }

  private var redisClient: RedisClient

  constructor(vertx: Vertx) {
    val redisOp = RedisOptions()
      .setConnectionString("redis://106.12.196.221:6379/")
    this.redisClient = RedisClient.create(vertx, redisOp.toJson());
  }

  fun getValue(key: String, def: String): Future<String> {
    var promise = Promise.promise<String>()
    redisClient[key, {
      if (it.succeeded()) {
        if (it.result() == null) {
          promise.complete(def)
        } else {
          promise.complete(it.result())
        }
      } else {
        promise.complete(def)
      }
    }]
    return promise.future()
  }

  fun setValue(key: String, value: String): Future<Boolean> {
    val promise = Promise.promise<Boolean>()
    redisClient.set(key, value) {
      if (it.succeeded()) {
        promise.complete(true)
      } else {
        promise.fail(it.cause())
      }
    }
    return promise.future()
  }
}
