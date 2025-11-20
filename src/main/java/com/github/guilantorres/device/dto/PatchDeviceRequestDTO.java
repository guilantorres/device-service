package com.github.guilantorres.device.dto;

import com.github.guilantorres.device.model.DeviceState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchDeviceRequestDTO {

  String name;
  String brand;
  DeviceState state;

}
