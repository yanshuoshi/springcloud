package com.yss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MyAuthApplication {

  public static void main(String[] args) {
    SpringApplication.run(MyAuthApplication.class, args);
  }

}
