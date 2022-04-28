package com.example.practise.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.List;

public class CalculatorVerticle extends AbstractVerticle {

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Overriden Stop  METHOD of FactorialVerticle");
    this.stop();
    stopPromise.complete();
  }

 private int result( String firstNumberInPost,String secondNumberInPost,String operationInPost )
 {
   int result = 0;
   if("add".equalsIgnoreCase(operationInPost))
   {
    result = Integer.parseInt(firstNumberInPost) + Integer.parseInt(secondNumberInPost);
   }
   if("minus".equalsIgnoreCase(operationInPost))
   {
     result = Integer.parseInt(firstNumberInPost) - Integer.parseInt(secondNumberInPost);
   }
   if("multiply".equalsIgnoreCase(operationInPost))
   {
     result = Integer.parseInt(firstNumberInPost) * Integer.parseInt(secondNumberInPost);
   }
   if("divide".equalsIgnoreCase(operationInPost))
   {
     result = Integer.parseInt(firstNumberInPost) / Integer.parseInt(secondNumberInPost);
   }
   return result;
 }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.out.println("Overriden Start METHOD of ResponseVerticle");
    vertx.eventBus().consumer("calculator",msg->{
      try {
        Thread.sleep(5000);
      }catch (InterruptedException ee)
      {

      }
      JsonObject body = (JsonObject) msg.body();

      String firstNumberInPost = body.getString("firstNumber");
      String secondNumberInPost = body.getString("secondNumber");
      String operationInPost = body.getString("operation");
      body.put("firstNumber",firstNumberInPost);
      body.put("secondNumber",secondNumberInPost);
      body.put("operation",operationInPost);
      body.put("result",result(firstNumberInPost,secondNumberInPost,operationInPost));


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
