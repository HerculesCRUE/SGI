package org.crue.hercules.sgi.prc.config;

import java.util.TimeZone;

import javax.validation.constraints.NotNull;

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
   * Web Url.
   */
  @NotNull
  private String webUrl;

  /**
   * I18n config refresh interval in seconds. Default 300
   */
  @NotNull
  private Long i18nConfigRefreshInterval = 300L;
}
