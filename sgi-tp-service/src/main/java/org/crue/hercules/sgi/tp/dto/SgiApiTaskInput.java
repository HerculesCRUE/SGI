package org.crue.hercules.sgi.tp.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.tp.enums.ServiceType;
import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SgiApiTaskInput implements Serializable {
  private String description;

  private ServiceType serviceType;

  private String relativeUrl;

  private HttpMethod httpMethod;
}
