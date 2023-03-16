package com.example.practise.api;

import io.vertx.core.AbstractVerticle;


public class MainVerticle extends AbstractVerticle {

  @Override
  public void stop() throws Exception {
    System.out.println("Stop METHOD of MainVerticle");
    this.stop();

  }


  @Override
  public void start() throws Exception {
    System.out.println("Overriden Start METHOD of MainVerticle");
    vertx.deployVerticle(new StudentAcheivment());
    vertx.deployVerticle(new StudentAcheivment1());
    vertx.deployVerticle(new StudentAcheivment2());
    vertx.deployVerticle(new StudentAcheivment3());
    vertx.deployVerticle(new CallExternalAPI());

  }
}
