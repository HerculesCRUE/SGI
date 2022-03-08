package org.crue.hercules.sgi.tp.config;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.i18n.LocaleContextHolder;

import lombok.Data;

/**
 * Propiedades de configuración.
 */
@ConfigurationProperties(prefix = "sgi")
@Data
public class SgiConfigProperties {
  /**
   * TimeZone
   */
  private TimeZone timeZone;

  /**
   * Locale
   */
  private Locale locale;

  public void setLocale(Locale locale) {
    this.locale = locale;
    LocaleContextHolder.setDefaultLocale(locale);
  }
}
