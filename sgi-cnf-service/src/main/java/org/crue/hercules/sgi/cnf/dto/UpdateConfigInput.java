package org.crue.hercules.sgi.cnf.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * This DTO represent a update configuration value received by HTTP request.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UpdateConfigInput implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Description */
  private String description;

  /** Value */
  private String value;
}