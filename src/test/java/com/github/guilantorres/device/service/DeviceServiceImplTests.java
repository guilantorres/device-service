package com.github.guilantorres.device.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.guilantorres.device.dto.CreateDeviceRequestDTO;
import com.github.guilantorres.device.dto.DeviceResponseDTO;
import com.github.guilantorres.device.dto.UpdateDeviceRequestDTO;
import com.github.guilantorres.device.exceptions.DeviceInUseException;
import com.github.guilantorres.device.exceptions.DeviceNotFoundException;
import com.github.guilantorres.device.model.Device;
import com.github.guilantorres.device.model.DeviceState;
import com.github.guilantorres.device.repository.DeviceMongoRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceImplTests {

  @Mock
  private DeviceMongoRepository deviceMongoRepository;
  @InjectMocks
  private DeviceServiceImpl deviceService;
  @Captor
  private ArgumentCaptor<Device> deviceArgumentCaptor;

  @Test
  public void createDevice_ShouldSetDefaultStateAndReturnDTO() {
    CreateDeviceRequestDTO request = new CreateDeviceRequestDTO();
    request.setName("W580");
    request.setBrand("Sony Ericsson");

    when(deviceMongoRepository.save(any(Device.class)))
        .thenAnswer(AdditionalAnswers.returnsFirstArg());
    DeviceResponseDTO response = deviceService.createDevice(request);
    verify(deviceMongoRepository).save(deviceArgumentCaptor.capture());

    Device savedDevice = deviceArgumentCaptor.getValue();

    assertThat(savedDevice.getState()).isEqualTo(DeviceState.AVAILABLE);
    assertThat(savedDevice.getName()).isEqualTo("W580");
    assertThat(savedDevice.getBrand()).isEqualTo("Sony Ericsson");

    assertThat(response.getState()).isEqualTo(DeviceState.AVAILABLE);
    assertThat(response.getName()).isEqualTo("W580");
    assertThat(response.getBrand()).isEqualTo("Sony Ericsson");
  }

  @Test
  public void getDeviceById_WhenDeviceExists_ShouldSucceed() {
    String deviceId = "abcdef";
    Instant instant = Instant.now();
    Device device = new Device(
        deviceId,
        "W580",
        "Sony Ericsson",
        DeviceState.AVAILABLE,
        instant
    );

    DeviceResponseDTO expectedResponse = new DeviceResponseDTO(
        deviceId,
        "W580",
        "Sony Ericsson",
        DeviceState.AVAILABLE,
        instant
    );

    when(deviceMongoRepository.findById(deviceId)).thenReturn(Optional.of(device));
    DeviceResponseDTO actualResponse = deviceService.getDeviceById(deviceId);

    assertThat(actualResponse)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @Test
  public void getDeviceById_WhenDeviceNotFound_ShouldThrowException() {
    when(deviceMongoRepository.findById("deviceId")).thenReturn(Optional.empty());
    assertThrows(DeviceNotFoundException.class, () -> {
      deviceService.getDeviceById("deviceId");
    });
  }

  @Test
  public void updateDevice_WhenDeviceAvailable_ShouldSucceed() {
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
  public void updateDevice_WhenDeviceInUse_ShouldThrowException() {
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

  @Test
  public void updateDevice_WhenDeviceNotFound_ShouldThrowException() {
    when(deviceMongoRepository.findById("deviceId")).thenReturn(Optional.empty());
    assertThrows(DeviceNotFoundException.class, () -> {
      deviceService.getDeviceById("deviceId");
    });
  }

  @Test
  public void deleteDevice_WhenDeviceAvailable_ShouldSucceed() {
    String deviceId = "abcdef";
    Device device = new Device(
        deviceId,
        "W580",
        "Sony Ericsson",
        DeviceState.AVAILABLE,
        Instant.now()
    );

    when(deviceMongoRepository.findById(deviceId)).thenReturn(Optional.of(device));

    deviceService.deleteDevice(deviceId);

    verify(deviceMongoRepository, times(1)).deleteById(deviceId);
  }

  @Test
  public void deleteDevice_WhenDeviceInUse_ShouldThrowException() {
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
  public void deleteDevice_WhenDeviceNotFound_ShouldThrowException() {
    when(deviceMongoRepository.findById("deviceId")).thenReturn(Optional.empty());
    assertThrows(DeviceNotFoundException.class, () -> {
      deviceService.getDeviceById("deviceId");
    });
  }
}
