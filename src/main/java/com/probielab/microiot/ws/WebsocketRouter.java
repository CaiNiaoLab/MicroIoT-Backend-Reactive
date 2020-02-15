package com.probielab.microiot.ws;

import com.probielab.microiot.redis.RedisHelper;
import com.probielab.microiot.redis.RedisVerticle;
import com.probielab.microiot.services.ComponentsService;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.ServerWebSocket;
import io.vertx.reactivex.core.http.WebSocketFrame;
import io.vertx.reactivex.core.parsetools.JsonParser;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class WebsocketRouter {
  public static void wsRoute(ServerWebSocket ws, WebSocketFrame wsf, Map<String, ServerWebSocket> map, Vertx vertx) {
    if (wsf.isClose() || wsf.isFinal()) {
      return;
    }
    if (wsf.isText()) {
      //判断消息
      JsonObject json = (JsonObject) Json.decodeValue(wsf.textData());
      if (json.getInteger("type") == 10) {
        ws.writeTextMessage("I get type 10 on " + json.getString("componentId"));
        createTimeTask(ws, json, vertx);
      }
    } else {
      ws.writeTextMessage("I get no text " + wsf.binaryData());
    }
  }

  private static void createTimeTask(ServerWebSocket ws, JsonObject json, Vertx vertx) {
    ComponentsService.getInstance(vertx)
      .getComponentDetail(json.getString("componentId"))
      .onSuccess(res -> {
        JsonObject pgJson = (JsonObject) Json.decodeValue(res);
        new Timer().schedule(new TimerTask() {
          public void run() {
            vertx.eventBus()
              .request(RedisVerticle.Companion.getREDIS_EVENT_GET(),
                new JsonObject()
                  .put("key", pgJson.getValue("serviceRoute")), eb_res -> {
                  JsonObject resJson = new JsonObject();
                  JsonArray resJa = new JsonArray();
                  resJa.add(new JsonObject().put(json.getString("componentId"), eb_res.result().body()));
                  resJson.put("type", 0)
                    .put("data", resJa);
                  ws.writeTextMessage(resJa.encode());
                });
          }
        }, 2000, 5000);
      })
      .onFailure(res -> {
        ws.writeTextMessage("{\"type\":\"-1\",\"event\":\"" + json.getString("componentId") + " reg failed!\"}");
      });
  }
}
