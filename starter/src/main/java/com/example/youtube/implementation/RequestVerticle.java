package com.example.youtube.implementation;

import com.example.youtube.implementation.EventBusHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.UUID;

public class RequestVerticle extends AbstractVerticle {
  String  verticleID= UUID.randomUUID().toString();
  String verticleIDLatest = verticleID + " Request Verticle" ;

  final JsonObject finalConfigJSON =  new JsonObject();

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop  METHOD of Request Verticle");
    this.stop();
    stopPromise.complete();
  }

  void executeBLockingPiece(Promise<Void> promise){
    try {

      for(int i=0;i<100;i++)
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
    System.out.println("Overriden Start METHOD of Request Verticle");


      loadConfiguration()
        .compose(this::saveConfig)
        .compose(this::configureRouter)
        .compose(this::startHTTPServer)
        .compose(this::deployOtherVerticles);

    startPromise.complete();
    System.out.println("Deployemt ID of RequestVerticle ::: "+deploymentID());
  }




  public Future<JsonObject> loadConfiguration()
  {
    System.out.println("loadConfiguration");
    ConfigStoreOptions configrationStoreOptions =new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path","config.json"));
    ConfigStoreOptions commandLineConfigs =new ConfigStoreOptions().setType("json").setConfig(config());
    ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions().addStore(configrationStoreOptions).addStore(commandLineConfigs);
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,configRetrieverOptions);
    //Handler<AsyncResult<JsonObject>> handler = asynResult -> this.loadConfigResults(startPromise,asynResult);
    //configRetriever.getConfig(handler);
    Handler<Promise<JsonObject>> handler = promise -> configRetriever.getConfig(promise) ;

    return Future.future(handler);

  }

  public Future<Void> saveConfig(JsonObject config){
    System.out.println("saveConfig");
    finalConfigJSON.mergeIn(config);
    System.out.println("Returning value");
    return Future.<Void>succeededFuture();


  }

  public Future<Router> configureRouter(Void unused){
    System.out.println("configureRouter");
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.post("/request/hello").consumes("*/json").handler(this::testHello);
    return Future.<Router>succeededFuture(router);

  }


  public Future<HttpServer> startHTTPServer(Router router){
    System.out.println("startHTTPServer");
    JsonObject httpJSON = finalConfigJSON.getJsonObject("http");
    int httpPort = httpJSON.getInteger("port");
   HttpServer server = vertx.createHttpServer().requestHandler(router);

    return Future.<HttpServer>future( promise->server.listen(httpPort,promise));
  }

  public Future<Void> deployOtherVerticles(HttpServer server){
    System.out.println("deployOtherVerticles");
    return Promise.<Void>promise().future();
  }


  public void executeBlockingCode(Promise<Void> startPromise){
    Handler<AsyncResult<Void>> handler1 = asynResult -> executeBLockingResult(startPromise,asynResult);
    vertx.executeBlocking(this::executeBLockingPiece,handler1);
  }

  public void loadConfigResults(Promise<Void> startPromise,AsyncResult<JsonObject> asynResult)
  {
    if(asynResult.succeeded()){
      JsonObject config = asynResult.result();
      JsonObject httpJSON = config.getJsonObject("http");

      int httpPort = httpJSON.getInteger("port");
      JsonObject dbproprerties = config.getJsonObject("dbproprerties");
      int dbport = dbproprerties.getInteger("port");
      String dburl = dbproprerties.getString("url");
      String dbusername = dbproprerties.getString("username");
      String dbpassword = dbproprerties.getString("password");

      System.out.println("dbport "+dbport);
      System.out.println("dburl "+ dburl);
      System.out.println("dbusername "+ dbusername);
      System.out.println("dbpassword "+ dbpassword);
 /*
      int httpPort =8093;
       try {
          httpPort = Integer.parseInt(System.getProperty("http.port"));
       }catch (NumberFormatException numberFormatException){
         httpPort = 8093;
       }

      //  vertx.createHttpServer().requestHandler(router).listen(httpPort);
       vertx.createHttpServer().requestHandler(router).listen(httpPort, http -> {
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

    EventBusHelper.send(vertx,rctx.getBodyAsJson(),"responseYT").onComplete(reply ->{
      // vertx.eventBus().request("response",rctx.getBodyAsJson(), reply->{
      if(reply.succeeded()) {
        System.out.println("successfully  Reply.....");
        rctx.request().response().putHeader("Content-Type", "application/json; charset=UTF8");
        JsonObject response = (JsonObject) reply.result();
        response.put("request Verticle ID",verticleIDLatest);
        rctx.request().response().end(response.encodePrettily());

      }
      if(reply.failed())
      {
        System.out.println("Failed  Reply.....");
      }

    });

  }


}
