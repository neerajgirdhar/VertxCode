package com.example.practise.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

public class PublishVerticle extends AbstractVerticle {

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop  METHOD of PublisherVerticle");
    this.stop();
    stopPromise.complete();
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Overriden Start METHOD of PublisherVerticle");


    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    Route handler2 = router
      .post("/publish")
      .consumes("*/json").handler(this::testHello);



    vertx.createHttpServer().requestHandler(router).listen(8094, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8094");
      } else {
        startPromise.fail(http.cause());
      }
    });

  }
  public void testHello(RoutingContext rctx){

/*    rctx.request().bodyHandler(bodyHandler -> {
      final JsonObject body = bodyHandler.toJsonObject();
      String nameInPost = body.getString("name");
      String genderInPost = body.getString("gender");
      String greeting = "Hello "+nameInPost+" Hope you are doing good.Enjoy your Day.";
      System.out.println(nameInPost);
    });*/

System.out.println("You are about to call Consumer Verticle");
    vertx.eventBus().publish("consumer",rctx.getBodyAsJson());
    for(int k=0;k<1000;k++)
    {
      System.out.println("Another task started in publisher while response is awaited from consumer"+k);
    }
    rctx.request().response().putHeader("Content-Type", "application/json; charset=UTF8");
    JsonObject response = new JsonObject();

    response.put("Status","Message Sent to all the consumer Verticles.");
    rctx.request().response().end(response.encodePrettily());
  }
}
