package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class ProyectoPresupuestoTotales implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private BigDecimal importeTotalPresupuestoUniversidad;
  private BigDecimal importeTotalPresupuestoSocios;
  private BigDecimal importeTotalConcedidoUniversidad;
  private BigDecimal importeTotalConcedidoSocios;
  private BigDecimal importeTotalPresupuesto;
  private BigDecimal importeTotalConcedido;

  public ProyectoPresupuestoTotales(BigDecimal importeTotalPresupuestoUniversidad,
      BigDecimal importeTotalPresupuestoSocios, BigDecimal importeTotalConcedidoUniversidad,
      BigDecimal importeTotalConcedidoSocios) {
    this.importeTotalPresupuestoUniversidad = importeTotalPresupuestoUniversidad;
    this.importeTotalPresupuestoSocios = importeTotalPresupuestoSocios;
    this.importeTotalConcedidoUniversidad = importeTotalConcedidoUniversidad;
    this.importeTotalConcedidoSocios = importeTotalConcedidoSocios;
  }
}
