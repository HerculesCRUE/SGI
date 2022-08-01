package org.crue.hercules.sgi.eti.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Configuración de los clientes de acceso al API rest de otros módulos.
 */
@ConfigurationProperties(prefix = "sgi.blockchain")
@Data
public class BlockchainApiProperties {
  /**
   * URL base de los end-points del servicio de blockchain.
   */
  private String url;
  /**
   * Password servicio blockchain.
   */
  private String password;
}
