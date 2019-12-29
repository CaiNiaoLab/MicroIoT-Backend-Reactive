package com.probielab.microiot.utils.reactivex;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class SQLBuilder {
  public static Future<String> InsertFromJson(JsonObject jsonObject) {

    Promise<String> promise = Promise.promise();

    StringBuilder queryStringBuilder = new StringBuilder();
    queryStringBuilder.append(jsonObject.getString("query"));

    StringBuilder setStringBuilder = new StringBuilder();
    setStringBuilder.append("(");

    StringBuilder valuesStringBuilder = new StringBuilder();
    valuesStringBuilder.append(" VALUES (");

    jsonObject.forEach( kv -> {
      setStringBuilder.append(kv.getKey());
      setStringBuilder.append(", ");
      valuesStringBuilder.append(JsonObject.mapFrom(kv.getValue()).toString());
      valuesStringBuilder.append(", ");
    });

    setStringBuilder.delete(-1, -3);
    setStringBuilder.append(")");
    valuesStringBuilder.delete(-1, -3);
    valuesStringBuilder.append(")");

    queryStringBuilder.append(setStringBuilder).append(valuesStringBuilder);
    promise.complete(queryStringBuilder.toString());
    return promise.future();
  }
}
