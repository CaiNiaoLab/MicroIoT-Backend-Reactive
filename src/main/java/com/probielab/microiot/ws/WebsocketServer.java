package com.probielab.microiot.ws;

import com.probielab.microiot.utils.reactivex.log4vertx;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.eventbus.bridge.tcp.TcpEventBusBridge;

public class WebsocketServer extends AbstractVerticle {
  @Override
  public void start() {
    EventBus eb = vertx.eventBus();

    PermittedOptions permittedOutOptions = new PermittedOptions()
      .setAddressRegex("microiot\\..+");
    PermittedOptions permittedInOptions = new PermittedOptions()
      .setAddressRegex("microiot\\..+");

    TcpEventBusBridge webSocket = TcpEventBusBridge
      .create(vertx, new BridgeOptions()
        .addOutboundPermitted(permittedOutOptions)
        .addInboundPermitted(permittedInOptions))
      .listen(8088, res -> {
        if (res.succeeded()) {
          log4vertx.info(eb, "WebSocket listening on port: 20000");
        } else {
          log4vertx.error(eb, "WebSocket create error", res.cause());
        }
      });
  }
}
