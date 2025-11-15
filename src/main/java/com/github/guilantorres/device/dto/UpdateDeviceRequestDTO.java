package com.github.guilantorres.device.dto;

import com.github.guilantorres.device.model.DeviceState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDeviceRequestDTO {

  private String name;
  private String brand;
  private DeviceState state;
}
