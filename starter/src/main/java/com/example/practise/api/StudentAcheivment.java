package com.example.practise.api;


import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StudentAcheivment  extends AbstractVerticle {
  String verticleID = UUID.randomUUID().toString();
  String verticleIDLatest = verticleID + " Student Acheivement Verticle";

  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("In START METHOD");
    Router router = Router.router(vertx);
    Future<String> future = createServer(vertx,router);
    future.onComplete(result -> {
      if(result.result().equals("Failed")){
        startPromise.fail("Verticle Loading Failed....");
      }else{
        router.route().handler(BodyHandler.create());
        Route handler2 = router
          .post("/fetch/StudentResult")
          .consumes("*/json").handler(this::fetchStudentDetails);
      }
    });

  }

  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("In STOP METHOD");
  }

  public Future<String> createServer(Vertx vertx ,Router router) throws Exception {
    System.out.println("In createServer METHOD");
    Promise<String> promise = Promise.promise();
    ConfigStoreOptions configrationOptions =new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path","config.json"));

    ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions().addStore(configrationOptions);
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,configRetrieverOptions);
    Future<JsonObject> config =configRetriever.getConfig();
    config.onComplete(result ->{
      if(config.succeeded()){
        JsonObject httpJSON = config.result().getJsonObject("student");
        if(httpJSON == null){
          promise.fail("Failed To Fetch the Config");
        }
        int httpPort = httpJSON.getInteger("port");
        vertx.createHttpServer().requestHandler(router).listen(httpPort);
        promise.complete("Success");
      }
      if(config.failed()){
       promise.fail("Failed To Fetch the Config");
      }
    });
   return promise.future();
  }


  public void fetchStudentDetails(RoutingContext rctx){
    System.out.println("In fetchStudentDetails METHOD");

    JsonObject request = rctx.getBodyAsJson();
    JsonObject response = new JsonObject();
    if(request.getString("name").equals("Neeraj"))
    {
      Future<JsonObject> future = fetchAcademics();
      Future<JsonObject> future1 = fetchSports();
      List<Future> list =  new ArrayList<>();
      list.add(future);
      list.add(future1);
     CompositeFuture compositeFuture = CompositeFuture.all(list);
      compositeFuture.onComplete(compositeFutureAsyncResult -> {
        if(compositeFutureAsyncResult.succeeded())
        {
          list.forEach(item ->{
            JsonObject json = (JsonObject)item.result();
            if(json.getString("Physics")!=null){
              response.put("Academics",json);
            }
            if(json.getString("Cricket")!=null){
              response.put("Sports",json);
            }
                      });
        }
      });

    }
    rctx.request().response().putHeader("Content-Type", "application/json; charset=UTF8");

    rctx.request().response().end(response.encodePrettily());
  }

  public Future<JsonObject> fetchAcademics()
  {
    Promise<JsonObject> promise =  Promise.promise();
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("Physics","A");
    jsonObject.put("Chemistry","A");
    jsonObject.put("Biology","B");
    jsonObject.put("Math","A+");
    jsonObject.put("English","A");
    promise.complete(jsonObject);
    return promise.future();

  }

  public Future<JsonObject> fetchSports()
  {
    Promise<JsonObject> promise =  Promise.promise();
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("Cricket","A");
    jsonObject.put("Tennis","A");
    jsonObject.put("TT","B");
    jsonObject.put("Badminton","A+");
    jsonObject.put("Soccer","A");
    promise.complete(jsonObject);
    return promise.future();


  }

}

