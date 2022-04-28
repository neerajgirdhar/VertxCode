package com.example.practise.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

public class CalculatorClientVerticle extends AbstractVerticle {

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop  METHOD of CalculatorClientVerticle");
    this.stop();
    stopPromise.complete();
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Overriden Start METHOD of CalculatorClientVerticle");
     Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    Route handler2 = router
      .post("/calculate")
      .consumes("*/json").handler(this::callAnotherVertcile);


    vertx.createHttpServer().requestHandler(router).listen(8095, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8095");
      } else {
        startPromise.fail(http.cause());
      }
    });

    System.out.println("Deployemt ID of CalculatorClientVerticle::: "+deploymentID());
    List<String> args = processArgs();
    if(args!=null)
    {
      args.forEach(arg->{
        System.out.println(arg);
      });
    }else{
      System.out.println("processArgs returned  null of RestVerticle ");
    }
  }
  public void callAnotherVertcile(RoutingContext rctx){

    EventBusHelper.send(vertx,rctx.getBodyAsJson(),"calculator").onComplete(reply ->{
      // vertx.eventBus().request("response",rctx.getBodyAsJson(), reply->{
      if(reply.succeeded()) {
        System.out.println("successfully  Reply.....");
        rctx.request().response().putHeader("Content-Type", "application/json; charset=UTF8");
        JsonObject response = (JsonObject) reply.result();
        rctx.request().response().end(response.encodePrettily());

      }
      if(reply.failed())
      {
        System.out.println("Failed  Reply.....");
      }

    });

  }
}
