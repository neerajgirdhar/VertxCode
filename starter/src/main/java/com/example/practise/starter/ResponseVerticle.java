package com.example.practise.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.List;
import java.util.UUID;

public class ResponseVerticle extends AbstractVerticle {
  String  verticleID= UUID.randomUUID().toString();
  String verticleIDLatest = verticleID + "Response Verticle" ;

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop METHOD of ResponseVerticle");
    this.stop();
    stopPromise.complete();
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Overriden Start METHOD of ResponseVerticle");
     vertx.eventBus().consumer("response",msg->{
       JsonObject body = (JsonObject) msg.body();
       try {
         Thread.sleep(1000);
       } catch (InterruptedException e) {
         e.printStackTrace();
       }
       String nameInPost = body.getString("name");
       String genderInPost = body.getString("gender");
       body.put("name",nameInPost+" : From ResponseVerticle ");
       body.put("gender",genderInPost+" : From ResponseVerticle ");
       body.put("responseVerticleID",verticleIDLatest);
       String greeting = "Hello "+nameInPost+" Hope you are doing good.Enjoy your Day.";
       body.put("greeting",greeting);
       msg.reply(body);
     }).completionHandler(handler ->{
       System.out.println("Message is succefully set in reply...");
       handler.succeeded();
       startPromise.complete();
     });


    System.out.println("Deployemt ID of ResponseVerticle ::: "+deploymentID());
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
