package com.probielab.microiot.ws;

import io.vertx.reactivex.core.http.WebSocketFrame;

public class WebsocketRouter {
  public static void wsRoute(WebSocketFrame wsf) {
    if (wsf.isClose() || wsf.isFinal()) {
      return;
    }

  }
}
