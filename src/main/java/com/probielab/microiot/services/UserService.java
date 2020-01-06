package com.probielab.microiot.services;

public class UserService extends BaseService {
  final String POST_USER = "INSERT INTO mi_user VALUES (generate_uuid(32), '";
  final String GET_USER = "";

  final String GET_RETURN_ID = "') RETURNING pid";
}
