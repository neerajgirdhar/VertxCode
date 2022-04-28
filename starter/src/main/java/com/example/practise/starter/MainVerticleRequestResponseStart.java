package com.example.practise.starter;

import io.netty.channel.ChannelDuplexHandler;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.List;

public class MainVerticleRequestResponseStart extends AbstractVerticle {

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop METHOD of MainVerticleRequestResponseStart");
    this.stop();
    stopPromise.complete();
  }


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Overriden Start METHOD of MainVerticleRequestResponseStart");

    vertx.deployVerticle(new RequestVerticle());
    DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(true).setInstances(4);
    vertx.deployVerticle("com.example.practise.starter.ResponseVerticle",deploymentOptions);
     System.out.println("Deployemt ID  of MainVerticleRequestResponseStart ::: "+deploymentID());
    List<String> args = processArgs();
    if(args!=null)
    {
      args.forEach(arg->{
        System.out.println(arg);
      });
    }else{
      System.out.println("processArgs returned  null of MainVerticleRequestResponseStart ");
    }
  }
}
