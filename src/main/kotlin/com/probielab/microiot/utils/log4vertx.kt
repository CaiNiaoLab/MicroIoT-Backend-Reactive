package com.probielab.microiot.utils;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class log4vertx {
  public static void error(EventBus eb, String message, Throwable throwable) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      throwable.printStackTrace(new PrintStream(baos));
      baos.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      eb.send("system", new JsonObject()
        .put("code", -1)
        .put("message", message)
        .put("error", baos.toString()));
    }
  }

  public static void info(EventBus eb, String message) {
    eb.send("system", new JsonObject()
      .put("code", 0)
      .put("message", message)
      .put("error", ""));
  }

  public static void debug(EventBus eb, String message) {
    eb.send("system", new JsonObject()
      .put("code", -1)
      .put("message", message)
      .put("error", ""));
  }
}
