package com.github.guilantorres.device.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.guilantorres.device.dto.DeviceResponseDTO;
import com.github.guilantorres.device.dto.UpdateDeviceRequestDTO;
import com.github.guilantorres.device.exceptions.DeviceInUseException;
import com.github.guilantorres.device.model.Device;
import com.github.guilantorres.device.model.DeviceState;
import com.github.guilantorres.device.repository.DeviceMongoRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceImplTests {

  @Mock
  private DeviceMongoRepository deviceMongoRepository;
  @InjectMocks
  private DeviceServiceImpl deviceService;

  @Test
  public void deleteDeviceShouldThrowExceptionWithInUseStateDevice() {
    String deviceId = "abcdef";
    Device deviceInUse = new Device(deviceId, "W580", "Sony Ericsson", DeviceState.IN_USE,
        Instant.now());
    when(deviceMongoRepository.findById(deviceId)).thenReturn(Optional.of(deviceInUse));

    assertThrows(DeviceInUseException.class, () -> {
      deviceService.deleteDevice(deviceId);
    });

    verify(deviceMongoRepository, never()).deleteById(deviceId);
  }

  @Test
  public void updateDeviceShouldProcessSuccessfully() {
    String deviceId = "abcdef";
    Instant instant = Instant.now();
    Device device = new Device(
        deviceId,
        "W580",
        "Sony Ericsson",
        DeviceState.AVAILABLE,
        instant
    );

    UpdateDeviceRequestDTO request = new UpdateDeviceRequestDTO();
    request.setName("W590");
    request.setBrand("Sony Ericsson");
    request.setState(DeviceState.AVAILABLE);

    DeviceResponseDTO expectedResponse = new DeviceResponseDTO(
        deviceId,
        "W590",
        "Sony Ericsson",
        DeviceState.AVAILABLE,
        instant
    );

    when(deviceMongoRepository.findById(deviceId)).thenReturn(Optional.of(device));
    when(deviceMongoRepository.save(any(Device.class)))
        .thenAnswer(AdditionalAnswers.returnsFirstArg());
    DeviceResponseDTO actualResponse = deviceService.updateDevice(deviceId, request);

    assertThat(actualResponse)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @Test
  public void updateDeviceShouldThrowExceptionWithInUseStateDevice() {
    String deviceId = "abcdef";
    Device deviceInUse = new Device(deviceId, "W580", "Sony Ericsson", DeviceState.IN_USE,
        Instant.now());
    UpdateDeviceRequestDTO request = new UpdateDeviceRequestDTO();
    request.setName("W590");
    when(deviceMongoRepository.findById(deviceId)).thenReturn(Optional.of(deviceInUse));

    assertThrows(DeviceInUseException.class, () -> {
      deviceService.updateDevice(deviceId, request);
    });

    verify(deviceMongoRepository, never()).save(deviceInUse);
  }
}
