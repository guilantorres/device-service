package com.github.guilantorres.device.dto;

import com.github.guilantorres.device.model.DeviceState;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceResponseDTO {

  private String id;
  private String name;
  private String brand;
  private DeviceState state;
  private Instant creationTime;
}
