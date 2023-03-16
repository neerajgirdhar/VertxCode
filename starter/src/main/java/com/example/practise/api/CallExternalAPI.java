package com.example.practise.api;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.UUID;

public class CallExternalAPI extends AbstractVerticle {
  String verticleID = UUID.randomUUID().toString();
  String verticleIDLatest = verticleID + " CallExternalAPI Verticle";

  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("In START METHOD");
    WebClient vertxClient = WebClient.create(vertx);
    Router router = Router.router(vertx);

    Future<String> future = createServer(vertx, router);

    future.onComplete(result -> {
      if (result.result().equals("Failed")) {
        startPromise.fail("Verticle Loading Failed....");
      } else {
        router.route().handler(BodyHandler.create());
        router.route().path("/api/nonblocking/numberTrivia").produces("application/json")
          .handler(han -> fetchNumberDetails(han,vertxClient));
      }
    });





  }


  public Future<String> createServer(Vertx vertx , Router router) throws Exception {
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
        JsonObject httpJSON = config.result().getJsonObject("externalApi");
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

  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("In STOP METHOD");
  }

  public void fetchNumberDetails(RoutingContext rctx,WebClient vertxClient){
    long startTime = System.currentTimeMillis();

    System.out.println(rctx.request().getParam("number"));

    HttpRequest<Buffer> request = vertxClient.get(80, "numbersapi.com", "/" + rctx.request().getParam("number") + "/trivia");
    Future<HttpResponse<Buffer>> future = request.send();


    future.onComplete(resul ->{
      JsonObject finalJson = new JsonObject();
      HttpResponse<Buffer> response = resul.result();
      System.out.println( future.result().body().toString());
      if(resul.succeeded()) {
        finalJson.put(rctx.request().getParam("number"),response.bodyAsString());

        rctx.response().end(finalJson.toString());
      } else {

        finalJson.put("Error","Error fetching Response");
        rctx.response().end(finalJson.toString());
      }
    });

  }
}
