package com.probielab.microiot.utils

import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintStream

object log4vertx {
  fun error(eb: EventBus, message: String?, throwable: Throwable) {
    val baos = ByteArrayOutputStream()
    try {
      throwable.printStackTrace(PrintStream(baos))
      baos.close()
    } catch (e: IOException) {
      e.printStackTrace()
    } finally {
      eb.send("system", JsonObject()
        .put("code", -1)
        .put("message", message)
        .put("error", baos.toString()))
    }
  }

  fun info(eb: EventBus, message: String?) {
    eb.send("system", JsonObject()
      .put("code", 0)
      .put("message", message)
      .put("error", ""))
  }

  fun debug(eb: EventBus, message: String?) {
    eb.send("system", JsonObject()
      .put("code", -1)
      .put("message", message)
      .put("error", ""))
  }
}
