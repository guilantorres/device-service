package com.github.guilantorres.device.service;

import com.github.guilantorres.device.dto.CreateDeviceRequestDTO;
import com.github.guilantorres.device.dto.DeviceResponseDTO;
import com.github.guilantorres.device.dto.UpdateDeviceRequestDTO;
import com.github.guilantorres.device.model.DeviceState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeviceService {

  DeviceResponseDTO createDevice(CreateDeviceRequestDTO request);

  DeviceResponseDTO getDeviceById(String id);

  Page<DeviceResponseDTO> getDevices(String brand, DeviceState state, Pageable pageable);

  DeviceResponseDTO updateDevice(String id, UpdateDeviceRequestDTO request);

  void deleteDevice(String id);

}
