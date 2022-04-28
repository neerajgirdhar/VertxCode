package com.example.practise.starter;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class EventBusHelper {

  public static Future<JsonObject> send(Vertx vertx, JsonObject message, String serviceName)
  {
    Promise<JsonObject> future = Promise.promise();
    vertx.eventBus().request(serviceName,message ,asyncResult->{
      if(asyncResult.succeeded()){
        System.out.println("asyncResult is success ");
        future.complete((JsonObject) asyncResult.result().body());
      }else{
        System.out.println("asyncResult is fail ");
        future.fail(asyncResult.cause());
      }

    });
  return future.future();
  }
}
