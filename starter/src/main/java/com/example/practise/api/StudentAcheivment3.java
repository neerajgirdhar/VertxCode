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

public class StudentAcheivment3  extends AbstractVerticle {
  String verticleID = UUID.randomUUID().toString();
  String verticleIDLatest = verticleID + " Student Acheivement Verticle";

  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("In START METHOD");
    Router router = Router.router(vertx);
    Future<String> future = createServer(vertx, router);
    future.onComplete(result -> {
      if (result.result().equals("Failed")) {
        startPromise.fail("Verticle Loading Failed....");
      } else {
        router.route().handler(BodyHandler.create());
        Route handler2 = router
          .post("/fetch/StudentResult3")
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
        JsonObject httpJSON = config.result().getJsonObject("studentResult3");
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
    long startTime = System.currentTimeMillis();
    System.out.println("In fetchStudentDetails METHOD");

    JsonObject request = rctx.getBodyAsJson();
    JsonObject response = new JsonObject();
    if(request.getString("name").equals("Neeraj"))
    {
      Future<JsonObject> future = academics();
      System.out.println("Outside 0 ");
      Future<JsonObject> future1 = sports();
      System.out.println("Outside 1");
      Future<JsonObject> future2 = extra();
      System.out.println("Outside 2");
      List<Future> list =  new ArrayList<>();
      list.add(future);
      list.add(future1);
      list.add(future2);
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

            if(json.getString("Dance")!=null){
              response.put("ExtraCurriculamActivities",json);
            }

          });
        }
      });
      System.out.println(compositeFuture.isComplete());

    }

    rctx.request().response().putHeader("Content-Type", "application/json; charset=UTF8");
    long endTime = System.currentTimeMillis();
    long timeTaken = endTime-startTime;
    response.put("timeTaken",timeTaken);
    rctx.request().response().end(response.encodePrettily());
  }
  public Future<JsonObject> academics()
  {
    System.out.println("In academics METHOD.......");
    Promise<JsonObject> promise =  Promise.promise();
    Future<JsonObject> future = haveSomeRest1();
    Future<JsonObject> composedFuture= future.compose(res ->fetchAcademics(res,"YOYO"));
    composedFuture.onComplete(resposne ->{
      promise.complete(resposne.result());
    });
    return promise.future();
  }

  public Future<JsonObject> sports()
  {
    System.out.println("In academics METHOD.......");
    Promise<JsonObject> promise =  Promise.promise();
    Future<JsonObject> future = haveSomeRest1();
    Future<JsonObject> composedFuture= future.compose(this::fetchSports);
    composedFuture.onComplete(resposne ->{
      promise.complete(resposne.result());
    });
    return promise.future();
  }

  public Future<JsonObject> extra()
  {
    System.out.println("In academics METHOD.......");
    Promise<JsonObject> promise =  Promise.promise();
    Future<JsonObject> future = haveSomeRest1();
    Future<JsonObject> composedFuture= future.compose(this::fetchExtraCurriculam);
    composedFuture.onComplete(resposne ->{
      promise.complete(resposne.result());
    });
    return promise.future();
  }
  public Future<JsonObject> fetchAcademics(JsonObject result,String name)
  {
    System.out.println("In fetchAcademics METHOD......."+result.toString());
    System.out.println("In fetchAcademics METHOD......."+name);
    Promise<JsonObject> promise =  Promise.promise();
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("Physics","A");
    jsonObject.put("Chemistry","A");
    jsonObject.put("Biology","B");
    jsonObject.put("Math","A+");
    jsonObject.put("English","A");
    promise.complete(jsonObject);
    System.out.println("Exiting out fetchAcademics METHOD.......");
    return promise.future();
  }

  public Future<JsonObject> fetchExtraCurriculam(JsonObject result)
  {

    System.out.println("In fetchExtraCurriculam METHOD......."+result.toString());
    Promise<JsonObject> promise =  Promise.promise();
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("Art & Craft", "A");
    jsonObject.put("Dance", "A");
    jsonObject.put("Singing", "B");
    jsonObject.put("Painting", "A+");
    jsonObject.put("CSR", "A");
    promise.complete(jsonObject);
    System.out.println("Exiting out fetchExtraCurriculam METHOD.......");
    return promise.future();
  }

  public Future<JsonObject> fetchSports(JsonObject result)
  {
    Promise<JsonObject> promise =  Promise.promise();
    System.out.println("In fetchSports METHOD......."+result.toString());
    JsonObject jsonObject = new JsonObject();
    jsonObject.put("Cricket", "A");
    jsonObject.put("Tennis", "A");
    jsonObject.put("TT", "B");
    jsonObject.put("Badminton", "A+");
    jsonObject.put("Soccer", "A");
    promise.complete(jsonObject);
    System.out.println("Exiting out fetchSports METHOD.......");
    return promise.future();
  }

  public void haveSomeRest(){
    System.out.println("Sleeping");
    vertx.setTimer(5000, l -> {
      System.out.println("Out of Sleep");
    });
  }

  /*  public Future<String> haveSomeRest1() {
  Promise<String> promise = Promise.promise();
    System.out.println("Sleeping");
    vertx.setTimer(5000, l -> {
      System.out.println("Out of Sleep");
      promise.complete("Success");

    });
  return promise.future();
  }*/

  public Future<JsonObject> haveSomeRest1() {
    Promise<JsonObject> promise = Promise.promise();
    System.out.println("Sleeping");
    try {
      Thread.sleep(2000l);
      System.out.println("Out of Sleep");
      JsonObject json = new JsonObject();
      json.put("Sleep","success");
      promise.complete(json);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return promise.future();
  }
}