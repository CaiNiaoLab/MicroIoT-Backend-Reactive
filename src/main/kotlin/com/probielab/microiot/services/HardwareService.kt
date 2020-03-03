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

class HardwareService(vertx: Vertx?) : BaseService(vertx) {
  val GET_RETURN_ID = "') RETURNING pid"
  val GET_HARDWARE_LIST = "SELECT * FROM mi_hardware WHERE uid='\${uid}'"
  val POST_HARDWARE_LIST = "INSERT INTO mi_hardware VALUES (generate_uuid(32), '\${json}','\${uid}'"
  val GET_HARDWARE_LIST_TEST = "SELECT * FROM mi_hardware"
  fun createHardware(json: String?, uid: String?): Future<String> {
    val promise = Promise.promise<String>()
    client.query(json?.let { uid?.let { POST_HARDWARE_LIST.replace("\${json}", it, ignoreCase = false).replace("\${uid}", uid, ignoreCase = false) } }) { res: AsyncResult<RowSet<Row?>> ->
      if (res.succeeded()) {
        promise.complete(res.result().delegate.iterator().next().toString())
      } else {
        promise.fail(res.cause())
      }
    }
    return promise.future()
  }

  fun getHardwareList(uid: String?, token: String?): Future<JsonArray> {
    val promise = Promise.promise<JsonArray>()
    //GET_HARDWARE_LIST.replace("${uid}", uid);
    client.query(GET_HARDWARE_LIST_TEST) { res: AsyncResult<RowSet<Row>> ->
      if (res.succeeded()) {
        val resultArray = JsonArray()
        res.result().forEach(Consumer { res_row: Row ->
          val resultItem = JsonObject()
          resultItem.put(res_row.getColumnName(0), res_row.getValue(0))
          resultItem.put(res_row.getColumnName(1), res_row.getValue(1))
          resultArray.add(resultItem)
        })
        promise.complete(resultArray)
      } else {
        promise.fail(res.cause())
      }
    }
    return promise.future()
  }

  companion object {
    private var hardwareService: HardwareService? = null
    fun getInstance(vertx: Vertx?): HardwareService? {
      if (hardwareService == null) {
        hardwareService = HardwareService(vertx)
      }
      return hardwareService
    }
  }
}
