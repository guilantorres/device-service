package com.github.guilantorres.device.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDeviceRequestDTO {

  @NotEmpty(message = "Device name cannot be empty")
  private String name;
  @NotEmpty(message = "Device brand cannot be empty")
  private String brand;
}
