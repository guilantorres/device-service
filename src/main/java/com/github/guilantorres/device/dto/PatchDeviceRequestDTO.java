package com.github.guilantorres.device.dto;

import com.github.guilantorres.device.model.DeviceState;
import lombok.Getter;

@Getter
public class PatchDeviceRequestDTO {

  String name;
  String brand;
  DeviceState state;

}
