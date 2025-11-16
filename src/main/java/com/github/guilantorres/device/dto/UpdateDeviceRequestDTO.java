package com.github.guilantorres.device.dto;

import com.github.guilantorres.device.model.DeviceState;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDeviceRequestDTO {

  @NotEmpty(message = "Device name cannot be empty")
  private String name;
  @NotEmpty(message = "Device brand cannot be empty")
  private String brand;
  @NotNull(message = "Device state cannot be null")
  private DeviceState state;
}
