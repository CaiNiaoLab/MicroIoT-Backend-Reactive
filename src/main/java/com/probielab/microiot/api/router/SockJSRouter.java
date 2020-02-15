package com.probielab.microiot.api.router;

import com.probielab.microiot.utils.reactivex.log4vertx;
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
    init(sockJSHandler);
  }

  public SockJSHandler getSockJSHandler() {
    return sockJSHandler;
  }

  private void init(SockJSHandler sh) {
    sh.socketHandler(socket -> {

    });
  }
}
