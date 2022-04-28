package com.example.practise.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.List;

public class ConsumerVerticle1 extends AbstractVerticle {

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop METHOD of ConsumerVerticle1");
    this.stop();
    stopPromise.complete();
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Overriden Start METHOD of ConsumerVerticle1");
     vertx.eventBus().consumer("consumer",msg->{
       JsonObject body = (JsonObject) msg.body();
       System.out.println("ConsumerVerticle1 : I am consumer i will Will Sleep some time");
       try {
         Thread.sleep(1990L);
       } catch (InterruptedException e) {
         e.printStackTrace();
       }
       System.out.println("Message is Received by Consumer Verticle 1 " + body.toString());
     }).completionHandler(handler ->{

       handler.succeeded();
       System.out.println("Replying back to publisher");
       startPromise.complete();
     });


    System.out.println("Deployemt ID of ConsumerVerticle1 ::: "+deploymentID());
    List<String> args = processArgs();
    if(args!=null)
    {
      args.forEach(arg->{
        System.out.println(arg);
      });
    }else{
      System.out.println("processArgs returned  null  of ConsumerVerticle");
    }


  }
}
