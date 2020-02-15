package com.probielab.microiot.ws;

import com.probielab.microiot.redis.RedisHelper;
import com.probielab.microiot.utils.reactivex.log4vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.ServerWebSocket;
import io.vertx.reactivex.ext.web.Router;

import java.util.HashMap;
import java.util.Map;

public class WebsocketVerticle extends AbstractVerticle {
  private Map<String, ServerWebSocket> connectionMap = new HashMap<>(16);

  EventBus eb = vertx.eventBus();
  @Override
  public void start() {
    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);

    router.route("/").handler(routingContext -> {
      routingContext.response().end();
    });
    websocketMethod(server);
    server.requestHandler(router::accept).listen(8081);
  }

  public void websocketMethod(HttpServer server) {
    server.websocketHandler(webSocket -> {
      String id = webSocket.binaryHandlerID();
      if (!checkID(id)) {
        connectionMap.put(id, webSocket);
      }

      webSocket.frameHandler(handler -> {
        String currID = webSocket.binaryHandlerID();
        for (Map.Entry<String, ServerWebSocket> entry : connectionMap.entrySet()) {
          if (currID.equals(entry.getKey())) {
            continue;
          }
          //do
        }
        JsonObject res = (JsonObject) Json.decodeValue(handler.textData());
        log4vertx.info(eb, "[Websocket RES]" + res.encode());
        WebsocketRouter.wsRoute(webSocket, handler, connectionMap);
      });

      webSocket.closeHandler(handler -> connectionMap.remove(id));
    });
  }

  public boolean checkID(String id) {
    return connectionMap.containsKey(id);
  }
}
