package com.example.practise.starter;

import io.netty.channel.ChannelDuplexHandler;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.List;

public class MainVerticleAllVerticleStart extends AbstractVerticle {

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop METHOD of MainVerticleAllVerticleStart");
    this.stop();
    stopPromise.complete();
  }


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Overriden Start METHOD of MainVerticleAllVerticleStart");
     vertx.deployVerticle(new PublishVerticle());


    vertx.deployVerticle(new ConsumerVerticle());
    vertx.deployVerticle(new ConsumerVerticle1());
    vertx.deployVerticle(new RequestVerticle());
    DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(true).setInstances(4);
    vertx.deployVerticle("com.example.practise.starter.ResponseVerticle",deploymentOptions);
   // vertx.deployVerticle(new ResponseVerticle());
    vertx.deployVerticle(new CalculatorClientVerticle());
    vertx.deployVerticle(new CalculatorVerticle());
    vertx.deployVerticle(new RestVerticle());

    System.out.println("Deployemt ID  of MainVerticle ::: "+deploymentID());
    List<String> args = processArgs();
    if(args!=null)
    {
      args.forEach(arg->{
        System.out.println(arg);
      });
    }else{
      System.out.println("processArgs returned  null of MainVerticleAllVerticleStart ");
    }
  }
}
