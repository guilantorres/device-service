package com.github.guilantorres.device.service;

import com.github.guilantorres.device.dto.CreateDeviceRequestDTO;
import com.github.guilantorres.device.dto.DeviceResponseDTO;
import com.github.guilantorres.device.dto.UpdateDeviceRequestDTO;
import com.github.guilantorres.device.exceptions.DeviceInUseException;
import com.github.guilantorres.device.exceptions.DeviceNotFoundException;
import com.github.guilantorres.device.model.Device;
import com.github.guilantorres.device.model.DeviceState;
import com.github.guilantorres.device.repository.DeviceMongoRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl implements DeviceService {

  private final DeviceMongoRepository deviceMongoRepository;

  @Autowired
  public DeviceServiceImpl(DeviceMongoRepository deviceMongoRepository) {
    this.deviceMongoRepository = deviceMongoRepository;
  }

  @Override
  public DeviceResponseDTO createDevice(CreateDeviceRequestDTO request) {
    Device device = new Device();
    device.setName(request.getName());
    device.setBrand(request.getBrand());
    device.setState(DeviceState.AVAILABLE);

    Device createdDevice = deviceMongoRepository.save(device);

    return entityToDto(createdDevice);
  }

  @Override
  public DeviceResponseDTO getDeviceById(String id) {
    Optional<Device> device = deviceMongoRepository.findById(id);
    if (device.isEmpty()) {
      throw new DeviceNotFoundException(String.format("Device with id: %s not found", id));
    }
    return entityToDto(device.get());
  }

  @Override
  public List<DeviceResponseDTO> getDevices(String brand, DeviceState state) {
    Device probeDevice = new Device();
    probeDevice.setBrand(brand);
    probeDevice.setState(state);

    Example<Device> exampleDevice = Example.of(probeDevice);
    List<Device> devices = deviceMongoRepository.findAll(exampleDevice);
    return entityToDto(devices);
  }

  @Override
  public DeviceResponseDTO updateDevice(String id, UpdateDeviceRequestDTO request) {
    Optional<Device> device = deviceMongoRepository.findById(id);
    if (device.isEmpty()) {
      throw new DeviceNotFoundException((String.format("Device with id: %s not found", id)));
    }
    if (device.get().getState().equals(DeviceState.IN_USE)) {
      throw new DeviceInUseException(
          String.format("Update into device with id: %s is not allowed due to device state: %s", id,
              DeviceState.IN_USE));
    }
    Device deviceToUpdate = device.get();
    deviceToUpdate.setName(request.getName());
    deviceToUpdate.setBrand(request.getBrand());
    deviceToUpdate.setState(request.getState());
    Device updatedDevice = deviceMongoRepository.save(deviceToUpdate);
    return entityToDto(updatedDevice);
  }

  @Override
  public void deleteDevice(String id) {
    Optional<Device> device = deviceMongoRepository.findById(id);
    if (device.isEmpty()) {
      throw new DeviceNotFoundException((String.format("Device with id: %s not found", id)));
    }
    if (device.get().getState().equals(DeviceState.IN_USE)) {
      throw new DeviceInUseException(
          String.format("Deletion of device with id: %s is not allowed due to device state: %s", id,
              DeviceState.IN_USE));
    }
    deviceMongoRepository.deleteById(id);
  }

  private DeviceResponseDTO entityToDto(Device device) {
    return new DeviceResponseDTO(
        device.getId(),
        device.getName(),
        device.getBrand(),
        device.getState(),
        device.getCreationTime()
    );
  }

  private List<DeviceResponseDTO> entityToDto(List<Device> devices) {
    return devices.stream()
        .map(this::entityToDto)
        .collect(Collectors.toList());
  }
}
