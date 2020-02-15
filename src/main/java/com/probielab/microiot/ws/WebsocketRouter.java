package com.probielab.microiot.ws;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.http.ServerWebSocket;
import io.vertx.reactivex.core.http.WebSocketFrame;

import java.util.Map;

public class WebsocketRouter {
  public static void wsRoute(ServerWebSocket ws, WebSocketFrame wsf, Map<String, ServerWebSocket> map) {
    if (wsf.isClose() || wsf.isFinal()) {
      return;
    }
    if (wsf.isText()) {
      //判断消息
      JsonObject json = (JsonObject) Json.decodeValue(wsf.textData());
      if (json.getInteger("type") == 10) {
        ws.writeTextMessage("I get type 10 on " + json.getString("componentId"));
      }
    } else {

    }
  }
}
