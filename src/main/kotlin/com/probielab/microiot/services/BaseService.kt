package com.probielab.microiot.services

import io.vertx.pgclient.PgConnectOptions
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.pgclient.PgPool
import io.vertx.sqlclient.PoolOptions

abstract class BaseService(vertx: Vertx?) {
  var connectOptions = PgConnectOptions()
    .setPort(54321)
    .setHost("106.12.196.221")
    .setDatabase("postgres")
    .setUser("postgres")
    .setPassword("password")
  // Pool options
  var poolOptions = PoolOptions()
    .setMaxSize(10)
  var client: PgPool

  // Create the pooled client
  init {
    client = PgPool.pool(vertx, connectOptions, poolOptions)
  }
}
