package com.probielab.microiot.services;

import io.vertx.reactivex.core.Vertx;

public class UserService extends BaseService {
  final String POST_USER = "INSERT INTO mi_user VALUES (generate_uuid(32), '";
  final String GET_USER = "";

  final String GET_RETURN_ID = "') RETURNING pid";


  public UserService(Vertx vertx) {
    super(vertx);
  }
}
