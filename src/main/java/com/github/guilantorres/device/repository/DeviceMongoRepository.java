package com.github.guilantorres.device.repository;

import com.github.guilantorres.device.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceMongoRepository extends MongoRepository<Device, String> {

}
