package com.probielab.microiot.utils;

import com.probielab.microiot.api.HttpServerVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.HashMap;

public class SqlHelperVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(SqlHelperVerticle.class);

  private enum SqlQuery {
    CREATE_TABLE,
    ALL,
    SELECT,
    UPDATE,
    INSERT,
    DELETE
  }

  private final HashMap<SqlQuery, String> sqlQueries = new HashMap<>();

  private MessageConsumer<HashMap<SqlQuery, String>> sqlConsumer;

  @Override
  public void start() throws Exception {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("microiot")
      .setUser("microiot")
      .setPassword("123456");

    // Pool options
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(10);

    // Create the pooled client
    PgPool client = PgPool.pool(Vertx.vertx(), connectOptions, poolOptions);

    sqlConsumer = vertx.eventBus().consumer(HttpServerVerticle.CONFIG_MICRO_IOT_DB_QUEUE);
    sqlConsumer.completionHandler( res -> {

    });

    LOGGER.info("Postgresql loader");
  }
}
