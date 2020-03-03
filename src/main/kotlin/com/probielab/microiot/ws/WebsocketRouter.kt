package com.probielab.microiot.ws

import com.probielab.microiot.redis.RedisVerticle.Companion.REDIS_EVENT_GET
import com.probielab.microiot.services.ComponentsService
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.eventbus.Message
import io.vertx.reactivex.core.http.ServerWebSocket
import io.vertx.reactivex.core.http.WebSocketFrame
import java.util.*

object WebsocketRouter {
  fun wsRoute(ws: ServerWebSocket, wsf: WebSocketFrame, map: Map<String, ServerWebSocket?>?, vertx: Vertx) {
    if (wsf.isClose || wsf.isFinal) {
      return
    }
    if (wsf.isText) { //判断消息
      val json = Json.decodeValue(wsf.textData()) as JsonObject
      if (json.getInteger("type") == 10) {
        ws.writeTextMessage("I get type 10 on " + json.getString("componentId"))
        createTimeTask(ws, json, vertx)
      }
    } else {
      ws.writeTextMessage("I get no text " + wsf.binaryData())
    }
  }

  private fun createTimeTask(ws: ServerWebSocket, json: JsonObject, vertx: Vertx) {
    ComponentsService.Companion.getInstance(vertx)
      ?.getComponentDetail(json.getString("componentId"))
      ?.onSuccess(Handler<String> { res: String? ->
        val pgJson = Json.decodeValue(res) as JsonObject
        Timer().schedule(object : TimerTask() {
          override fun run() {
            vertx.eventBus()
              .request(REDIS_EVENT_GET,
                JsonObject()
                  .put("key", pgJson.getValue("serviceRoute"))) { eb_res: AsyncResult<Message<Any?>> ->
                val resJson = JsonObject()
                val resJa = JsonArray()
                resJa.add(JsonObject().put(json.getString("componentId"), eb_res.result().body()))
                resJson.put("type", 0)
                  .put("data", resJa)
                ws.writeTextMessage(resJa.encode())
              }
          }
        }, 2000, 5000)
      })
      ?.onFailure(Handler<Throwable> { res: Throwable? -> ws.writeTextMessage("{\"type\":\"-1\",\"event\":\"" + json.getString("componentId") + " reg failed!\"}") })
  }
}
