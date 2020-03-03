package com.probielab.microiot.api.router

import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler

class SockJSRouter private constructor(vertx: Vertx) {
  private val sockJSHandler: SockJSHandler

  private fun init(sh: SockJSHandler) {
    sh.socketHandler { }
  }

  companion object {
    private var sockJSRouter: SockJSRouter? = null
    fun getInstance(vertx: Vertx): SockJSRouter? {
      if (sockJSRouter == null) {
        sockJSRouter = SockJSRouter(vertx)
      }
      return sockJSRouter
    }
  }

  init {
    val options = SockJSHandlerOptions().setHeartbeatInterval(2000)
    sockJSHandler = SockJSHandler.create(vertx, options)
    init(sockJSHandler)
  }
}
