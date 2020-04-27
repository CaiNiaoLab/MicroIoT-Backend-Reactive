package com.probielab.microiot.orm

import com.probielab.microiot.api.HttpServerVerticle
import com.probielab.microiot.utils.reactivex.log4vertx
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.PgConnectOptions
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.core.eventbus.MessageConsumer
import io.vertx.reactivex.pgclient.PgPool
import io.vertx.reactivex.sqlclient.Row
import io.vertx.reactivex.sqlclient.RowSet
import io.vertx.sqlclient.PoolOptions

class SqlHelperVerticle : AbstractVerticle() {
  private enum class SqlQuery {
    CREATE_TABLE, ALL, SELECT, UPDATE, INSERT, DELETE
  }

  private var sqlConsumer: MessageConsumer<JsonObject>? = null
  override fun start() {
    val connectOptions = PgConnectOptions()
      .setPort(54321)
      .setHost("106.12.85.54")
      .setDatabase("postgres")
      .setUser("postgres")
      .setPassword("password")
    // Pool options
    val poolOptions = PoolOptions()
      .setMaxSize(10)
    // Create the pooled client
    val client = PgPool.pool(vertx, connectOptions, poolOptions)
    val sqlEventBus = vertx.eventBus()
    sqlConsumer = sqlEventBus.consumer(HttpServerVerticle.Companion.CONFIG_MICRO_IOT_DB_QUEUE)
    sqlConsumer?.handler { res: Message<JsonObject> ->
      log4vertx.info(sqlEventBus, "Postgres SQL handle a request from " + res.body().getString("query"))
      client.query(res.body().getString("query")).execute { pg_res: AsyncResult<RowSet<Row>> ->
        if (pg_res.failed()) {
          log4vertx.error(sqlEventBus, "Postgres SQL SELECT error", pg_res.cause())
        } else {
          val result = JsonObject()
          result
            .put("version", -1)
            .put("code", 0)
          val resultSet = JsonArray()
          val rowRowIterator = pg_res.result().iterator()
          rowRowIterator.forEachRemaining { v: Row ->
            val framework_data = v.getValue(1).toString()
            resultSet.add(framework_data)
          }
          result.put("data", resultSet)
          val deliveryOptions = DeliveryOptions()
          deliveryOptions
            .setSendTimeout(1000).isLocalOnly = true
          res.reply(result, deliveryOptions)
        }
      }
    }
    sqlConsumer?.completionHandler { log4vertx.info(sqlEventBus, "Postgres SQL ready") }
  }
}
