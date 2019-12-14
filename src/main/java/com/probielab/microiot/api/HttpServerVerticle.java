package com.probielab.microiot.api;

import com.probielab.microiot.api.router.ASimpleRouter;
import com.probielab.microiot.utils.reactivex.log4vertx;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class HttpServerVerticle extends AbstractVerticle {
  public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  public static final String CONFIG_MICRO_IOT_DB_QUEUE = "microiotdb.queue";


  @Override
  public void start() {
    EventBus eb = vertx.eventBus();

    HttpServer server = vertx.createHttpServer();
    Router router = ASimpleRouter.getRouter(vertx);

    int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
    server
      .requestHandler(router)
      .listen(portNumber, ar -> {
        if (ar.succeeded()) {
          log4vertx.info(eb, "Http Server running on port:" + portNumber);
        } else {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          try {
            ar.cause().printStackTrace(new PrintStream(baos));
            baos.close();
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            log4vertx.error(eb, "Cannot start http server", ar.cause());
          }
        }
      });
  }
}
