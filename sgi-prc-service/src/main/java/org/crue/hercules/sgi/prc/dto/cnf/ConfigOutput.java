package org.crue.hercules.sgi.prc.dto.cnf;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This DTO represent a configuration value returned by HTTP response.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ConfigOutput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Name */
  private String name;
  /** Description */
  private String description;
  /** Value */
  private String value;

}
