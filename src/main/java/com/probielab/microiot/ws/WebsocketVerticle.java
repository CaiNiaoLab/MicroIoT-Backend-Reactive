package com.probielab.microiot.ws;

import com.probielab.microiot.redis.RedisHelper;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.ServerWebSocket;
import io.vertx.reactivex.ext.web.Router;

import java.util.HashMap;
import java.util.Map;

public class WebsocketVerticle extends AbstractVerticle {
  private Map<String, ServerWebSocket> connectionMap = new HashMap<>(16);

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
        String textData = handler.textData();
        String currID = webSocket.binaryHandlerID();
        for (Map.Entry<String, ServerWebSocket> entry : connectionMap.entrySet()) {
          if (currID.equals(entry.getKey())) {
            continue;
          }
          RedisHelper.getInstance(vertx).getValue("test", "[]")
            .onSuccess(res -> {
              entry.getValue().writeTextMessage(res);
            });

        }
      });

      webSocket.closeHandler(handler -> connectionMap.remove(id));
    });
  }

  public boolean checkID(String id) {
    return connectionMap.containsKey(id);
  }
}
