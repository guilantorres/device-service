package com.github.guilantorres.device.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.guilantorres.device.dto.CreateDeviceRequestDTO;
import com.github.guilantorres.device.dto.DeviceResponseDTO;
import com.github.guilantorres.device.dto.PatchDeviceRequestDTO;
import com.github.guilantorres.device.dto.UpdateDeviceRequestDTO;
import com.github.guilantorres.device.exceptions.DeviceInUseException;
import com.github.guilantorres.device.exceptions.DeviceNotFoundException;
import com.github.guilantorres.device.model.Device;
import com.github.guilantorres.device.model.DeviceState;
import com.github.guilantorres.device.repository.DeviceMongoRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceImplTests {

  @Mock
  private DeviceMongoRepository deviceMongoRepository;
  @InjectMocks
  private DeviceServiceImpl deviceService;
  @Captor
  private ArgumentCaptor<Device> deviceArgumentCaptor;
  @Captor
  private ArgumentCaptor<Example<Device>> exampleArgumentCaptor;

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
  public void getDevices_ShouldBuildExampleAndReturnPageableSuccessfully() {
    String brand = "Sony Ericsson";
    Pageable pageRequest = PageRequest.of(0, 5);

    List<Device> devices = List.of(
        new Device("1", "W580", brand, DeviceState.AVAILABLE, Instant.now()),
        new Device("2", "W581", brand, DeviceState.IN_USE, Instant.now()),
        new Device("3", "W582", brand, DeviceState.INACTIVE, Instant.now()),
        new Device("4", "W583", brand, DeviceState.IN_USE, Instant.now()),
        new Device("5", "W584", brand, DeviceState.AVAILABLE, Instant.now())
    );

    Page<Device> devicePage = new PageImpl<>(
        devices,
        pageRequest,
        devices.size()
    );

    when(deviceMongoRepository.findAll(any(Example.class), any(Pageable.class)))
        .thenReturn(devicePage);

    Page<DeviceResponseDTO> responsePage = deviceService.getDevices(brand, null, pageRequest);

    assertThat(responsePage.getTotalElements()).isEqualTo(5);
    assertThat(responsePage.getTotalPages()).isEqualTo(1);
    assertThat(responsePage.getContent())
        .extracting(DeviceResponseDTO::getBrand)
        .containsOnly(brand);

    verify(deviceMongoRepository).findAll(exampleArgumentCaptor.capture(), any(Pageable.class));
    assertThat(exampleArgumentCaptor.getValue().getProbe().getBrand()).isEqualTo(brand);
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
    String deviceId = "abcdef";
    UpdateDeviceRequestDTO request = new UpdateDeviceRequestDTO();
    request.setName("W580");
    request.setBrand("Sony Ericsson");
    request.setState(DeviceState.AVAILABLE);
    when(deviceMongoRepository.findById(deviceId)).thenReturn(Optional.empty());
    assertThrows(DeviceNotFoundException.class, () -> {
      deviceService.updateDevice(deviceId, request);
    });
  }

  @Test
  public void patchDevice_WhenDeviceAvailable_ShouldSucceed() {
    String deviceId = "abcdef";
    Instant instant = Instant.now();
    Device device = new Device(
        deviceId,
        "W580",
        "Sony Ericsson",
        DeviceState.AVAILABLE,
        instant
    );

    PatchDeviceRequestDTO request = new PatchDeviceRequestDTO();
    request.setName("W590");

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
    DeviceResponseDTO actualResponse = deviceService.patchDevice(deviceId, request);

    assertThat(actualResponse)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @Test
  public void patchDevice_WhenUpdatingStateOfInUseDevice_ShouldSucceed() {
    String deviceId = "abcdef";
    Instant instant = Instant.now();
    Device device = new Device(
        deviceId,
        "W580",
        "Sony Ericsson",
        DeviceState.IN_USE,
        instant
    );

    PatchDeviceRequestDTO request = new PatchDeviceRequestDTO();
    request.setState(DeviceState.INACTIVE);

    DeviceResponseDTO expectedResponse = new DeviceResponseDTO(
        deviceId,
        "W580",
        "Sony Ericsson",
        DeviceState.INACTIVE,
        instant
    );

    when(deviceMongoRepository.findById(deviceId)).thenReturn(Optional.of(device));
    when(deviceMongoRepository.save(any(Device.class)))
        .thenAnswer(AdditionalAnswers.returnsFirstArg());
    DeviceResponseDTO actualResponse = deviceService.patchDevice(deviceId, request);

    assertThat(actualResponse)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @Test
  public void patchDevice_WhenUpdatingNameOfInUseDevice_ShouldThrowException() {
    String deviceId = "abcdef";
    Instant instant = Instant.now();
    Device device = new Device(
        deviceId,
        "W580",
        "Sony Ericsson",
        DeviceState.IN_USE,
        instant
    );

    PatchDeviceRequestDTO request = new PatchDeviceRequestDTO();
    request.setName("W590");

    when(deviceMongoRepository.findById(deviceId)).thenReturn(Optional.of(device));

    assertThrows(DeviceInUseException.class, () -> {
      deviceService.patchDevice(deviceId, request);
    });

    verify(deviceMongoRepository, never()).save(device);
  }

  @Test
  public void patchDevice_WhenDeviceNotFound_ShouldThrowException() {
    String deviceId = "abcdef";
    PatchDeviceRequestDTO request = new PatchDeviceRequestDTO();
    request.setState(DeviceState.IN_USE);
    when(deviceMongoRepository.findById(deviceId)).thenReturn(Optional.empty());
    assertThrows(DeviceNotFoundException.class, () -> {
      deviceService.patchDevice(deviceId, request);
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
      deviceService.deleteDevice("deviceId");
    });
  }
}
