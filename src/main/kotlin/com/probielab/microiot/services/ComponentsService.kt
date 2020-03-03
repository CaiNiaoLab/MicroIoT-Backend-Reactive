package com.probielab.microiot.services

import io.vertx.core.AsyncResult
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.Future
import io.vertx.reactivex.core.Promise
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.sqlclient.Row
import io.vertx.reactivex.sqlclient.RowSet
import java.util.function.Consumer

class ComponentsService(vertx: Vertx?) : BaseService(vertx) {
  val GET_RETURN_ID = "') RETURNING pid"
  fun getComponent(uid: String?, pid: String?): Future<JsonArray>? {
    val promise: Promise<JsonArray> = Promise.promise<JsonArray>()
    client.query(pid?.let {
      GET_COMPONENT
        .replace("\${pid}", it, ignoreCase = false)
    }) { pg_res: AsyncResult<RowSet<Row>> ->
      if (pg_res.succeeded()) {
        val resultArray = JsonArray()
        pg_res.result().forEach(Consumer { res_row: Row ->
          val resultItem = JsonObject()
          resultItem.put(res_row.getColumnName(0), res_row.getValue(0))
          resultItem.put(res_row.getColumnName(1), res_row.getValue(1))
          resultArray.add(resultItem)
        })
        promise.complete(resultArray)
      } else {
        promise.fail(pg_res.cause())
      }
    }
    return promise.future()
  }

  fun createComponent(uid: String?, pid: String?, json: String?): Future<String> {
    val promise = Promise.promise<String>()
    client.query(json?.let {
      if (pid != null) {
        return@let POST_COMPONENT.replace("\${json}", it, ignoreCase = false)
          .replace("\${pid}", pid, ignoreCase = false)
      } else {
        return@let "NULL"
      }
    }.toString() + GET_RETURN_ID) { res: AsyncResult<RowSet<Row?>> ->
      if (res.succeeded()) {
        promise.complete(res.result().delegate.iterator().next().toString())
      } else {
        promise.fail(res.cause())
      }
    }
    return promise.future()
  }

  fun getComponentDetail(cid: String?): Future<String> {
    val promise = Promise.promise<String>()
    client.query(cid?.let { GET_COMPONENT_DETAIL.replace("\${cid}", it, ignoreCase = false) }) { res: AsyncResult<RowSet<Row?>> ->
      if (res.succeeded()) {
        promise.complete(res.result().delegate.iterator().next().toString())
      } else {
        promise.fail(res.cause())
      }
    }
    return promise.future()
  }

  companion object {
    const val GET_COMPONENT = "SELECT * FROM mi_component WHERE pid='\${pid}'"
    const val POST_COMPONENT = "INSERT INTO mi_component VALUES (generate_uuid(32), '\${json}','\${pid}'"
    const val GET_COMPONENT_DETAIL = "SELECT * FROM mi_component WHERE cid='\${cid}'"
    private var componentsService: ComponentsService? = null
    fun getInstance(vertx: Vertx?): ComponentsService? {
      if (componentsService == null) {
        componentsService = ComponentsService(vertx)
      }
      return componentsService
    }
  }
}
