package org.crue.hercules.sgi.csp.config;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * Configuración de los clientes de acceso al API rest de otros módulos.
 */
@ConfigurationProperties(prefix = "sgi.rest.api")
@Data
@Validated
public class RestApiProperties {
  /**
   * URL base de los end-points del módulo de ETICA.
   */
  @NotNull
  private String etiUrl;
  /**
   * URL base de los end-points del módulo de INFORMES.
   */
  @NotNull
  private String repUrl;
  /**
   * URL base de los end-points del módulo de SGDOC.
   */
  @NotNull
  private String sgdocUrl;
  /**
   * URL base de los end-points del módulo de TP.
   */
  @NotNull
  private String tpUrl;
  /**
   * URL base de los end-points del módulo de COM.
   */
  @NotNull
  private String comUrl;
  /**
   * URL base de los end-points del módulo de SGP.
   */
  @NotNull
  private String sgpUrl;
  /**
   * URL base de los end-points del módulo de CNF.
   */
  @NotNull
  private String cnfUrl;
  /**
   * URL base de los end-points del módule de SGEMP
   */
  @NotNull
  private String sgempUrl;
}
