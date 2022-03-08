package org.crue.hercules.sgi.com.config;

import java.util.List;
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
   * If set all emails are sent to this instead of real recipient list
   */
  private List<String> fakeEmailRecipients;

  /**
   * Disables the email send functionallity
   */
  private boolean disableEmailSend = false;
}
