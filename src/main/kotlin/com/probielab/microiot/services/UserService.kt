package com.probielab.microiot.services

import io.vertx.reactivex.core.Vertx

class UserService(vertx: Vertx?) : BaseService(vertx) {
  val POST_USER = "INSERT INTO mi_user VALUES (generate_uuid(32), '"
  val GET_USER = ""
  val GET_RETURN_ID = "') RETURNING pid"
}
