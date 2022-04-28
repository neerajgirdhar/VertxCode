package com.example.practise.starter;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RequestVerticle extends AbstractVerticle {
  String  verticleID= UUID.randomUUID().toString();
  String verticleIDLatest = verticleID + "Request Verticle" ;

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop  METHOD of RequestVerticle");
    this.stop();
    stopPromise.complete();
  }
  void executeBLockingPiece(Promise<Void> promise){
    try {

      for(int i=0;i<10000;i++)
      {
        System.out.println(i);
      }
      promise.complete();
    }catch (Exception ee){

      System.out.println(ee);
      promise.fail("Thread Sleep Disturbed");
    }
  }

  void executeBLockingResult(Promise<Void> startPromise, AsyncResult<Void> result){
    if(result.failed())
    {
      startPromise.fail("Blocking code did not completed");
    }

  }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Overriden Start METHOD of RequestVerticle");

   // Handler<AsyncResult<Void>> handler1 = asynResult -> executeBLockingResult(startPromise,asynResult);
   // vertx.executeBlocking(this::executeBLockingPiece,handler1);
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    Route handler2 = router
      .post("/request/hello")
      .consumes("*/json").handler(this::testHello);

    ConfigStoreOptions configrationOptions =new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path","config.json"));

    ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions().addStore(configrationOptions);
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,configRetrieverOptions);

    Handler<AsyncResult<JsonObject>> handler = asynResult -> this.loadConfigResults(startPromise,router,asynResult);

    configRetriever.getConfig(handler);

  }

  public void loadConfigResults(Promise<Void> startPromise, Router router,AsyncResult<JsonObject> asynResult)
  {

      if(asynResult.succeeded()){
        JsonObject config = asynResult.result();
        JsonObject httpJSON = config.getJsonObject("http");
        int httpPort = httpJSON.getInteger("port");
        vertx.createHttpServer().requestHandler(router).listen(httpPort);
       /* vertx.createHttpServer().requestHandler(router).listen(httpPort, http -> {
          if (http.succeeded()) {


            System.out.println("HTTP server started on port ---> " +httpPort);
            startPromise.complete();

          } else {
            startPromise.fail(http.cause());
          }
        });*/
        System.out.println("HTTP server started on port ---> " +httpPort);
        startPromise.complete();
      }else {
        startPromise.fail(asynResult.cause());
      }
    }

  public void testHello(RoutingContext rctx){
    Promise<Void> startPromiseLocal = Promise.promise();
    Handler<AsyncResult<Void>> handler1 = asynResult -> executeBLockingResult(startPromiseLocal,asynResult);
     vertx.executeBlocking(this::executeBLockingPiece,handler1);
    EventBusHelper.send(vertx,rctx.getBodyAsJson(),"response").onComplete(reply ->{
   // vertx.eventBus().request("response",rctx.getBodyAsJson(), reply->{
    if(reply.succeeded()) {
      System.out.println("successfully  Reply.....");
      rctx.request().response().putHeader("Content-Type", "application/json; charset=UTF8");
      JsonObject response = (JsonObject) reply.result();
      response.put("requestVerticleID",verticleIDLatest);
      rctx.request().response().end(response.encodePrettily());

    }
    if(reply.failed())
    {
      System.out.println("Failed  Reply.....");
    }

  });

  }
}
