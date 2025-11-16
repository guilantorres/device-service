package com.github.guilantorres.device;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class DeviceServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(DeviceServiceApplication.class, args);
  }

}
