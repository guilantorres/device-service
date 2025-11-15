package com.github.guilantorres.device.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Device {

  @Id
  private String id;
  @Setter
  private String name;
  @Setter
  private String brand;
  @Setter
  private DeviceState state;
  @CreatedDate
  private Instant creationTime;
}
