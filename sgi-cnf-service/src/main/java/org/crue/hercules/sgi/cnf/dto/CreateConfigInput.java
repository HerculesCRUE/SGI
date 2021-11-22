package org.crue.hercules.sgi.cnf.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * This DTO represent a creation configuration value received by HTTP request.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreateConfigInput extends UpdateConfigInput {
  /** Name */
  @NotBlank
  private String name;
}