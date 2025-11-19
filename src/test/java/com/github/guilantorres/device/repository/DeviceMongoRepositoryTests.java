//package com.github.guilantorres.device.repository;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.github.guilantorres.device.model.Device;
//import com.github.guilantorres.device.model.DeviceState;
//import java.time.Instant;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//@DataMongoTest
//@Testcontainers
//public class DeviceMongoRepositoryTests {
//
//  @Container
//  @ServiceConnection
//  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");
//
//  @Autowired
//  private DeviceMongoRepository deviceMongoRepository;
//
//  @Test
//  void save_ShouldHandleCreationTime() {
//    Device device = new Device(
//        null,
//        "W580",
//        "Sony Ericsson",
//        DeviceState.AVAILABLE,
//        null
//    );
//
//    Device savedDevice = deviceMongoRepository.save(device);
//
//    assertThat(savedDevice.getId()).isNotNull();
//    assertThat(savedDevice.getCreationTime()).isNotNull();
//    assertThat(savedDevice.getCreationTime()).isBeforeOrEqualTo(Instant.now());
//  }
//
//  @Test
//  void findAll_WithExample_ShouldFilterCorrectly() {
//    deviceMongoRepository.saveAll(List.of(
//        new Device(null, "W580", "Sony Ericsson", DeviceState.AVAILABLE, null),
//        new Device(null, "W590", "Sony Ericsson", DeviceState.IN_USE, null),
//        new Device(null, "iPhone", "Apple", DeviceState.AVAILABLE, null)
//    ));
//
//    Device probe = new Device();
//    probe.setBrand("Sony Ericsson");
//    probe.setState(DeviceState.AVAILABLE);
//    Example<Device> example = Example.of(probe);
//
//    Pageable pageRequest = PageRequest.of(0, 10);
//
//    Page<Device> result = deviceMongoRepository.findAll(example, pageRequest);
//
//    assertThat(result.getTotalElements()).isEqualTo(1);
//    assertThat(result.getContent().get(0).getName()).isEqualTo("W580");
//  }
//
//}
