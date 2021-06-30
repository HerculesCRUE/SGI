package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.crue.hercules.sgi.csp.enums.TipoPartida;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnualidadResumen implements Serializable {
  @Enumerated(EnumType.STRING)
  private TipoPartida tipo;
  private String codigoPartidaPresupuestaria;
  private BigDecimal importePresupuesto;
  private BigDecimal importeConcedido;

  public AnualidadResumen(String codigoPartidaPresupuestaria, BigDecimal importePresupuesto,
      BigDecimal importeConcedido) {
    this.codigoPartidaPresupuestaria = codigoPartidaPresupuestaria;
    this.importePresupuesto = importePresupuesto;
    this.importeConcedido = importeConcedido;
  }

  public AnualidadResumen(String codigoPartidaPresupuestaria, BigDecimal importeConcedido) {
    this.codigoPartidaPresupuestaria = codigoPartidaPresupuestaria;
    this.importeConcedido = importeConcedido;
  }
}
