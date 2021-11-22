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

  private BigDecimal importeTotalPresupuestoUniversidadSinCosteIndirecto;
  private BigDecimal importeTotalPresupuestoSocios;
  private BigDecimal importeTotalConcedidoUniversidadSinCosteIndirecto;
  private BigDecimal importeTotalConcedidoSocios;
  private BigDecimal importeTotalPresupuesto;
  private BigDecimal importeTotalConcedido;
  private BigDecimal importeTotalPresupuestoUniversidadCostesIndirectos;
  private BigDecimal importeTotalConcedidoUniversidadCostesIndirectos;

  public ProyectoPresupuestoTotales(BigDecimal importeTotalPresupuestoUniversidadSinCosteIndirecto,
      BigDecimal importeTotalPresupuestoUniversidadCostesIndirectos, BigDecimal importeTotalPresupuestoSocios,
      BigDecimal importeTotalConcedidoUniversidadSinCosteIndirecto,
      BigDecimal importeTotalConcedidoUniversidadCostesIndirectos, BigDecimal importeTotalConcedidoSocios) {
    this.importeTotalPresupuestoUniversidadSinCosteIndirecto = importeTotalPresupuestoUniversidadSinCosteIndirecto;
    this.importeTotalPresupuestoSocios = importeTotalPresupuestoSocios;
    this.importeTotalConcedidoUniversidadSinCosteIndirecto = importeTotalConcedidoUniversidadSinCosteIndirecto;
    this.importeTotalConcedidoSocios = importeTotalConcedidoSocios;
    this.importeTotalPresupuestoUniversidadCostesIndirectos = importeTotalPresupuestoUniversidadCostesIndirectos;
    this.importeTotalConcedidoUniversidadCostesIndirectos = importeTotalConcedidoUniversidadCostesIndirectos;
  }
}
