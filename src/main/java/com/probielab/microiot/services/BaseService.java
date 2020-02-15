package com.probielab.microiot.services;

import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public abstract class BaseService {

  PgConnectOptions connectOptions = new PgConnectOptions()
    .setPort(54321)
    .setHost("106.12.196.221")
    .setDatabase("postgres")
    .setUser("postgres")
    .setPassword("password");

  // Pool options
  PoolOptions poolOptions = new PoolOptions()
    .setMaxSize(10);

  // Create the pooled client
  PgPool client = PgPool.pool(Vertx.vertx(), connectOptions, poolOptions);
}
