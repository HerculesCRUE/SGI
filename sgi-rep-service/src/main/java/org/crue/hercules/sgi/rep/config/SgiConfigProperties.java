package org.crue.hercules.sgi.rep.config;

import java.util.TimeZone;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * Propiedades de configuración.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "sgi")
public class SgiConfigProperties implements InitializingBean {

  private static SgiConfigProperties instance;

  @Override
  public void afterPropertiesSet() throws Exception {
    instance = this;
  }

  /**
   * Get's the singleton instance
   * 
   * @return the instance
   */
  public static SgiConfigProperties get() {
    return instance;
  }

  /**
   * TimeZone.
   */
  @NotNull
  private TimeZone timeZone;

  /**
   * I18n config refresh interval in seconds. Default 300
   */
  @NotNull
  private Long i18nConfigRefreshInterval = 300L;
}
