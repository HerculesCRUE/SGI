package org.crue.hercules.sgi.tp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Configuración de los clientes de acceso al API rest de otros módulos.
 */
@ConfigurationProperties(prefix = "sgi.rest.api")
@Data
public class RestApiProperties {
  /**
   * URL base de los end-points del módulo de ETICA.
   */
  private String etiUrl;

  /**
   * URL base de los end-points del módulo de CSP.
   */
  private String cspUrl;

  /**
   * URL base de los end-points del módulo de PII.
   */
  private String piiUrl;

  /**
   * URL base de los end-points del módulo de PRC.
   */
  private String prcUrl;

  /**
   * URL base de los end-points del módulo de REL.
   */
  private String relUrl;

  /**
   * URL base de los end-points del módulo de COM.
   */
  private String comUrl;

  /**
   * URL base de los end-points del módulo de USR.
   */
  private String usrUrl;

  /**
   * URL base de los end-points del módulo de REP.
   */
  private String repUrl;
}
