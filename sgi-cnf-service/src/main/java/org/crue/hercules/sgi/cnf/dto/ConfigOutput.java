package org.crue.hercules.sgi.cnf.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * This DTO represent a configuration value returned by HTTP response.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class ConfigOutput extends CreateConfigInput {
}