package com.probielab.microiot.api

import com.probielab.microiot.api.router.ASimpleRouter
import com.probielab.microiot.utils.reactivex.log4vertx
import io.vertx.core.AsyncResult
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.http.HttpServer
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintStream

class HttpServerVerticle : AbstractVerticle() {
  override fun start() {
    val eb = vertx.eventBus()
    val server = vertx.createHttpServer()
    val router = ASimpleRouter.getRouter(vertx)
    val portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080)
    server
      .requestHandler(router)
      .listen(portNumber) { ar: AsyncResult<HttpServer?> ->
        if (ar.succeeded()) {
          log4vertx.info(eb, "Http Server running on port:$portNumber")
        } else {
          val baos = ByteArrayOutputStream()
          try {
            ar.cause().printStackTrace(PrintStream(baos))
            baos.close()
          } catch (e: IOException) {
            e.printStackTrace()
          } finally {
            log4vertx.error(eb, "Cannot start http server", ar.cause())
          }
        }
      }
  }

  companion object {
    const val CONFIG_HTTP_SERVER_PORT = "http.server.port"
    const val CONFIG_MICRO_IOT_DB_QUEUE = "microiotdb.queue"
  }
}
