package com.github.guilantorres.device.service;

import com.github.guilantorres.device.dto.CreateDeviceRequestDTO;
import com.github.guilantorres.device.dto.DeviceResponseDTO;
import com.github.guilantorres.device.dto.UpdateDeviceRequestDTO;
import com.github.guilantorres.device.model.DeviceState;
import java.util.List;

public interface DeviceService {

  DeviceResponseDTO createDevice(CreateDeviceRequestDTO request);

  DeviceResponseDTO getDeviceById(String id);

  List<DeviceResponseDTO> getDevices(String brand, DeviceState state);

  DeviceResponseDTO updateDevice(String id, UpdateDeviceRequestDTO request);

  void deleteDevice(String id);

}
