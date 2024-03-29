package org.crue.hercules.sgi.eti.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Configuración de los clientes de acceso al API rest de otros módulos.
 */
@ConfigurationProperties(prefix = "sgi.rest.api")
@Data
public class RestApiProperties {
  /**
   * URL base de los end-points del módulo de INFORMES.
   */
  private String repUrl;
  /**
   * URL base de los end-points del módulo de SGDOC.
   */
  private String sgdocUrl;
  /**
   * URL base de los end-points del módulo de SGP.
   */
  private String sgpUrl;
  /**
   * URL base de los end-points del módulo de COM.
   */
  private String comUrl;
  /**
   * URL base de los end-points del módulo de CNF.
   */
  private String cnfUrl;
}
