package com.probielab.microiot.ws

import com.probielab.microiot.utils.reactivex.log4vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.eventbus.EventBus
import io.vertx.reactivex.core.http.HttpServer
import io.vertx.reactivex.core.http.HttpServerRequest
import io.vertx.reactivex.core.http.ServerWebSocket
import io.vertx.reactivex.core.http.WebSocketFrame
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import java.util.*

class WebsocketVerticle : AbstractVerticle() {
  private val connectionMap: MutableMap<String, ServerWebSocket?> = HashMap(16)
  var eb: EventBus = vertx.eventBus()
  override fun start() {
    val server = vertx.createHttpServer()
    val router = Router.router(vertx)
    router.route("/").handler {
      it.response().end()
    }
    websocketMethod(server)
    server.requestHandler {
      router.handle(it)
    }.listen(8081)
  }

  private fun websocketMethod(server: HttpServer) {
    server.websocketHandler { webSocket: ServerWebSocket ->
      val id = webSocket.binaryHandlerID()
      if (!checkID(id)) {
        connectionMap[id] = webSocket
      }
      webSocket.frameHandler { handler: WebSocketFrame ->
        val currID = webSocket.binaryHandlerID()
        for ((key) in connectionMap) {
          if (currID == key) {
            continue
          }
          //do
        }
        val res = Json.decodeValue(handler.textData()) as JsonObject
        log4vertx.info(eb, "[Websocket RES]" + res.encode())
        WebsocketRouter.wsRoute(webSocket, handler, connectionMap, vertx)
      }
      webSocket.closeHandler { connectionMap.remove(id) }
    }
  }

  private fun checkID(id: String?): Boolean {
    return connectionMap.containsKey(id)
  }
}
