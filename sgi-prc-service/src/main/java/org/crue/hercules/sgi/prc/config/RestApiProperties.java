package org.crue.hercules.sgi.prc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Configuración de los clientes de acceso al API rest de otros módulos.
 */
@ConfigurationProperties(prefix = "sgi.rest.api")
@Data
public class RestApiProperties {
  /**
   * URL base de los end-points del módulo de CNF.
   */
  private String cnfUrl;

  /**
   * URL base de los end-points del módulo de COM.
   */
  private String comUrl;

  /**
   * URL base de los end-points del módulo de CSP.
   */
  private String cspUrl;

  /**
   * URL base de los end-points del módulo de PII.
   */
  private String piiUrl;

  /**
   * URL base de los end-points del módulo de REL.
   */
  private String relUrl;

  /**
   * URL base de los end-points del módulo de REP.
   */
  private String repUrl;

  /**
   * URL base de los end-points de SGEPII.
   */
  private String sgepiiUrl;

  /**
   * URL base de los end-points de SGO.
   */
  private String sgoUrl;

  /**
   * URL base de los end-points de SGP.
   */
  private String sgpUrl;

  /**
   * URL base de los end-points del módulo de TP.
   */
  private String tpUrl;

}
