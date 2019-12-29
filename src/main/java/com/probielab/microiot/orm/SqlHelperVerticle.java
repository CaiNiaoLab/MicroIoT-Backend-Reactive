package com.probielab.microiot.orm;

import com.probielab.microiot.utils.reactivex.log4vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Promise;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import io.vertx.reactivex.pgclient.PgPool;
import io.vertx.reactivex.sqlclient.Row;
import io.vertx.reactivex.sqlclient.RowIterator;
import io.vertx.sqlclient.PoolOptions;

public class SqlHelperVerticle extends AbstractVerticle {
  public static final String CONFIG_MICRO_IOT_DB_QUEUE = "microiot.db.queue";

  private enum SqlQuery {
    CREATE_TABLE,
    ALL,
    SELECT,
    UPDATE,
    INSERT,
    DELETE
  }

  private MessageConsumer<JsonObject> sqlConsumer;

  private static JsonObject SQL_ERROR = new JsonObject().put("code", -1);

  @Override
  public void start() {
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

    EventBus sqlEventBus = vertx.eventBus();

    sqlConsumer = sqlEventBus.consumer(CONFIG_MICRO_IOT_DB_QUEUE);

    sqlConsumer.handler(res -> {
      log4vertx.info(sqlEventBus, "Postgres SQL handle a request from " + res.body().getString("query"));
      query(sqlEventBus, client, res.body().getString("query"))
        .setHandler( pg_res -> {
          if (pg_res.succeeded()) {
            DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions
              .setSendTimeout(1000)
              .setLocalOnly(true);
            res.reply(pg_res.result(), deliveryOptions);
          } else {
            DeliveryOptions deliveryOptions = new DeliveryOptions();
            deliveryOptions
              .setSendTimeout(1000)
              .setLocalOnly(true);
            res.reply(SQL_ERROR, deliveryOptions);
          }
        });
    });

    sqlConsumer.completionHandler(res -> log4vertx.info(sqlEventBus, "Postgres SQL ready"));
  }

  private Future<JsonObject> query(EventBus sqlEventBus, PgPool pgPool, String sql) {
    Promise<JsonObject> promise = Promise.promise();

    pgPool.query(sql, res -> {
      if (res.succeeded()) {
        JsonObject result = new JsonObject();
        result
          .put("version", -1)
          .put("code", 0);
        JsonArray resultSet = new JsonArray();
        RowIterator<Row> rowRowIterator = res.result().iterator();
        rowRowIterator.forEachRemaining(v -> {
          JsonObject framework_data = (JsonObject) v.getValue("framework_data");
          resultSet.add(framework_data);
        });
        result.put("data", resultSet);

        promise.complete(result);
      } else {
        log4vertx.error(sqlEventBus, "Postgres SQL SELECT error", res.cause());
        promise.fail("Query failed!");
      }
    });

    return promise.future();
  }
}
