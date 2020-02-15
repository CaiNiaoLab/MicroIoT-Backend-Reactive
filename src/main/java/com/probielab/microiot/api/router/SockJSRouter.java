package com.probielab.microiot.api.router;

import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;

public class SockJSRouter {
  public static SockJSRouter sockJSRouter;
  private SockJSHandler sockJSHandler;

  public static SockJSRouter getInstance(Vertx vertx) {
    if (sockJSRouter == null) {
      sockJSRouter = new SockJSRouter(vertx);
    }
    return sockJSRouter;
  }

  private SockJSRouter(Vertx vertx) {
    SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);
    sockJSHandler = SockJSHandler.create(vertx, options);
  }

  public SockJSHandler getSockJSHandler() {
    return sockJSHandler;
  }

  private void init(SockJSHandler sh) {
    sh.socketHandler(socket -> {

    });
  }
}
