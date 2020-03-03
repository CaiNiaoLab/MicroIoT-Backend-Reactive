package com.probielab.microiot.services

import io.vertx.core.AsyncResult
import io.vertx.reactivex.core.Future
import io.vertx.reactivex.core.Promise
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.sqlclient.Row
import io.vertx.reactivex.sqlclient.RowSet

class ProjectService(vertx: Vertx?) : BaseService(vertx) {
  val POST_APP = "INSERT INTO mi_project VALUES (generate_uuid(32), '"
  val GET_APP = "SELECT project_data FROM mi_project where pid='\${pid}'"
  val GET_APP_ALL = "SELECT * FROM mi_project where uid='\${uid}'"
  val GET_RETURN_ID = "') RETURNING pid"
  fun postProject(json: String): Future<String> {
    val result = Promise.promise<String>()
    client.query(POST_APP + json + GET_RETURN_ID) { res: AsyncResult<RowSet<Row?>> ->
      if (res.succeeded()) {
        result.complete(res.result().delegate.iterator().next().toString())
      } else {
        result.fail(res.cause())
      }
    }
    return result.future()
  }

  fun getProject(pid: String?): Future<String> {
    val promise = Promise.promise<String>()
    client.query(pid?.let { GET_APP.replace("\${pid}", it, ignoreCase = true) }) { res: AsyncResult<RowSet<Row?>> ->
      if (res.succeeded()) {
        promise.complete(res.result().delegate.iterator().next().toString())
      } else {
        promise.fail(res.cause())
      }
    }
    return promise.future()
  }

  companion object {
    private var projectService: ProjectService? = null
    fun getInstance(vertx: Vertx?): ProjectService? {
      if (projectService == null) {
        projectService = ProjectService(vertx)
      }
      return projectService
    }
  }
}
