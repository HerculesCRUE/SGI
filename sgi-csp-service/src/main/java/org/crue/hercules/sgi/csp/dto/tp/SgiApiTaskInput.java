package org.crue.hercules.sgi.csp.dto.tp;

import java.io.Serializable;

import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class SgiApiTaskInput implements Serializable {
  private String description;

  private ServiceType serviceType;

  private String relativeUrl;

  private HttpMethod httpMethod;
}
