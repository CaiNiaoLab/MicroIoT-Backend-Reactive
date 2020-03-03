package com.probielab.microiot.api.router

import com.probielab.microiot.services.ComponentsService
import com.probielab.microiot.services.HardwareService
import com.probielab.microiot.services.ProjectService
import com.probielab.microiot.utils.reactivex.log4vertx
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.CorsHandler
import java.util.*

object ASimpleRouter {
  private const val DbQueue = "microiotdb.queue"
  fun getRouter(vertx: Vertx): Router {
    val router = Router.router(vertx)
    val eb = vertx.eventBus()
    val allow: HashSet<HttpMethod> = HashSet<HttpMethod>()
    allow.add(HttpMethod.GET)
    allow.add(HttpMethod.POST)
    router.route().handler(CorsHandler.create("*")
      .allowedMethods(allow))
    router.route("/saveApplication").handler { res: RoutingContext ->
      ProjectService.Companion.getInstance(vertx)!!.postProject(res.bodyAsString)
        .onSuccess(Handler<String> { pg_res: String? -> res.response().end(pg_res) })
    }
    router.route("/loadApplication").handler { res: RoutingContext ->
      ProjectService.Companion.getInstance(vertx)!!.getProject(res.bodyAsJson.getString("applicationId"))
        .onSuccess(Handler<String> { pg_res: String? -> res.response().end(pg_res) })
    }
    router.route("/loadHardwareInfo").handler { res: RoutingContext ->
      HardwareService.Companion.getInstance(vertx)!!.getHardwareList("", "")
        .onSuccess(Handler<JsonArray> { pg_res: JsonArray? -> res.response().end(JsonObject().put("data", pg_res).encode()) })
        .onFailure(Handler { pg_res: Throwable ->
          res.response().end(JsonObject().put("data", "").encode())
          log4vertx.error(eb, "hard msg fail", pg_res.cause)
        })
    }
    router.route("/loadComponent").handler { res: RoutingContext ->
      ComponentsService.Companion.getInstance(vertx)!!.getComponent("", "be28f4f0d11242d09f061b4d3e876b93")
        ?.onSuccess(Handler<JsonArray> { pg_res: JsonArray? -> res.response().end(JsonObject().put("data", pg_res).encode()) })
        ?.onFailure(Handler { pg_res: Throwable ->
          res.response().end(JsonObject().put("data", "").encode())
          log4vertx.error(eb, "hard msg fail", pg_res.cause)
        })
    }
    router.route("/saveComponent").handler { res: RoutingContext ->
      ComponentsService.Companion.getInstance(vertx)!!.createComponent("MncWP0jkui3SJIlifUFbYho4Olv0pioC", "be28f4f0d11242d09f061b4d3e876b93", res.bodyAsJson.encode())
        .onSuccess(Handler<String> { pg_res: String? -> res.response().end(pg_res) })
    }
    return router
  }
}
