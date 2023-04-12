package org.crue.hercules.sgi.cnf.config;

import java.util.TimeZone;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Propiedades de configuración.
 */
@ConfigurationProperties(prefix = "sgi")
@Data
public class SgiConfigProperties {
  /**
   * TimeZone.
   */
  private TimeZone timeZone;

  /**
   * Cache max age for resources in seconds
   */
  private Long resourcesCacheMaxAge;
}
