package com.example.youtube.implementation;

import com.example.youtube.implementation.RequestVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

import java.util.List;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Override Stop METHOD of MainVerticle");
    this.stop();
    stopPromise.complete();
  }


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Override Start METHOD of Main Verticle");
    System.out.println("Deployment ID  of Main Verticle ::: "+deploymentID());
    vertx.deployVerticle(new RequestVerticle());
    DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(true).setInstances(1);
    vertx.deployVerticle("com.example.youtube.implementation.ResponseVerticle",deploymentOptions);
    List<String> args = processArgs();
    if(args!=null)
    {
      args.forEach(arg->{
        System.out.println(arg);
      });
    }else{
      System.out.println("processArgs returned  null of MainVerticle ");
    }
  }
}
