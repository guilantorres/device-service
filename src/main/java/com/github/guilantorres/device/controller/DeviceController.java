package com.github.guilantorres.device.controller;

import com.github.guilantorres.device.dto.CreateDeviceRequestDTO;
import com.github.guilantorres.device.dto.DeviceResponseDTO;
import com.github.guilantorres.device.dto.PatchDeviceRequestDTO;
import com.github.guilantorres.device.dto.UpdateDeviceRequestDTO;
import com.github.guilantorres.device.model.DeviceState;
import com.github.guilantorres.device.service.DeviceService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/devices")
public class DeviceController {

  private final DeviceService deviceService;

  @Autowired
  public DeviceController(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  @PostMapping
  public ResponseEntity<DeviceResponseDTO> createDevice(
      @Valid @RequestBody CreateDeviceRequestDTO request) {
    DeviceResponseDTO response = deviceService.createDevice(request);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.getId())
        .toUri();

    return ResponseEntity.created(location).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DeviceResponseDTO> getDeviceById(@PathVariable String id) {
    DeviceResponseDTO response = deviceService.getDeviceById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<Page<DeviceResponseDTO>> getDevicesByBrandOrName(
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) DeviceState state,
      Pageable pageable) {
    Page<DeviceResponseDTO> devices = deviceService.getDevices(brand, state, pageable);
    return ResponseEntity.ok(devices);
  }

  @PutMapping("/{id}")
  public ResponseEntity<DeviceResponseDTO> updateDevice(@PathVariable String id,
      @Valid @RequestBody UpdateDeviceRequestDTO request) {
    DeviceResponseDTO response = deviceService.updateDevice(id, request);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<DeviceResponseDTO> patchDevice(
      @PathVariable String id,
      @RequestBody PatchDeviceRequestDTO request
  ) {
    DeviceResponseDTO response = deviceService.patchDevice(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity deleteDevice(@PathVariable String id) {
    deviceService.deleteDevice(id);
    return ResponseEntity.noContent().build();
  }
}
