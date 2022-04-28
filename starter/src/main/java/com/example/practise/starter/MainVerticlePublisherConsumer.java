package com.example.practise.starter;

import io.netty.channel.ChannelDuplexHandler;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.List;

public class MainVerticlePublisherConsumer extends AbstractVerticle {

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop METHOD of MainVerticlePublisherConsumer");
    this.stop();
    stopPromise.complete();
  }


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Overriden Start METHOD of MainVerticlePublisherConsumer");
     vertx.deployVerticle(new PublishVerticle());
    vertx.deployVerticle(new ConsumerVerticle());
    vertx.deployVerticle(new ConsumerVerticle1());
    System.out.println("Deployemt ID  of MainVerticlePublisherConsumer ::: "+deploymentID());
    List<String> args = processArgs();
    if(args!=null)
    {
      args.forEach(arg->{
        System.out.println(arg);
      });
    }else{
      System.out.println("processArgs returned  null of MainVerticlePublisherConsumer ");
    }
  }
}
