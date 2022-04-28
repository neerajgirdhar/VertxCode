package com.example.practise.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.List;

public class RestVerticle extends AbstractVerticle {

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop  METHOD of RestVerticle");
    this.stop();
    stopPromise.complete();
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Overriden Start METHOD of RestVerticle");
     Router router = Router.router(vertx);

    Route handler1 = router
      .get("/hello/:name")
      .handler(routingContext -> {
        String name = routingContext.request().getParam("name");
        System.out.println("came to hello: " + name);
        HttpServerResponse response = routingContext.response();
        response.setChunked(true);
        response.write("Hi " + name + "\n");
        response.end();
      });
    Route handler2 = router
      .post("/delayed/hello")
      .consumes("*/json")
      .handler(routingContext -> {

        System.out.println("came to post");
        routingContext.request().bodyHandler(bodyHandler -> {
          final JsonObject body = bodyHandler.toJsonObject();
          String nameInPost = body.getString("name");
          String genderInPost = body.getString("gender");
          String greeting = "Hello "+nameInPost+" Hope you are doing good.Enjoy your Day.";
          body.put("greeting",greeting);
          HttpServerResponse response = routingContext.response();
          response.setChunked(true);
          try {
            Thread.sleep(10000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
         // response.write("Hi Reply from post : Name :: "+nameInPost+ ", Gender :: "+ genderInPost);

          response.putHeader("Content-Type", "application/json; charset=UTF8").end(body.encodePrettily());
          response.end();
        });


      });
    Route handler4 = router
      .post("/fast/hello")
      .consumes("*/json")
      .handler(routingContext -> {

        System.out.println("came to post");
        routingContext.request().bodyHandler(bodyHandler -> {
          final JsonObject body = bodyHandler.toJsonObject();
          String nameInPost = body.getString("name");
          String genderInPost = body.getString("gender");
          String greeting = "Hello "+nameInPost+" Hope you are doing good.Enjoy your Day.";
          HttpServerResponse response = routingContext.response();
          response.setChunked(true);
          body.put("greeting",greeting);
          // response.write("Hi Reply from post : Name :: "+nameInPost+ ", Gender :: "+ genderInPost);

          response.putHeader("Content-Type", "application/json; charset=UTF8").end(body.encodePrettily());
          response.end();
        });


      });
    Route handler3 = router
      .get("/factorial/:number1/:number2")
      .handler(routingContext -> {
        String startNumber = routingContext.request().getParam("number1");
        String endNumber = routingContext.request().getParam("number2");
        int factNumberStart =Integer.parseInt(startNumber);
        int factNumberEnd =Integer.parseInt(endNumber);
        try {
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if(factNumberEnd>factNumberStart) {
          System.out.println("Calculating Factorial");
          HttpServerResponse response = routingContext.response();
          response.setChunked(true);
          response.write("Right Inputs First Number is smaller than Second Number" + "\n");
          response.end();
        }else{
          System.out.println("Wrong Inputs");
          HttpServerResponse response = routingContext.response();
          response.setChunked(true);
          response.write("Wrong Inputs First Number should be smaller than Second Number" + "\n");
          response.end();
        }
      });

    vertx.createHttpServer().requestHandler(router).listen(8091, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8091");
      } else {
        startPromise.fail(http.cause());
      }
    });

    System.out.println("Deployemt ID of RestVerticle::: "+deploymentID());
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
}
