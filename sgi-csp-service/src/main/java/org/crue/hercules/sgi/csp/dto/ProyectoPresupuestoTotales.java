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

  private BigDecimal importeTotalConcedidoSocios;
  private BigDecimal importeTotalConcedidoUniversidadCostesIndirectos;
  private BigDecimal importeTotalConcedidoUniversidadSinCosteIndirecto;
  private BigDecimal importeTotalConcedidoUniversidad;
  private BigDecimal importeTotalConcedido;

  private BigDecimal importeTotalPresupuestoSocios;
  private BigDecimal importeTotalPresupuestoUniversidadCostesIndirectos;
  private BigDecimal importeTotalPresupuestoUniversidadSinCosteIndirecto;
  private BigDecimal importeTotalPresupuestoUniversidad;
  private BigDecimal importeTotalPresupuesto;

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

    this.importeTotalConcedidoUniversidad = importeTotalConcedidoUniversidadSinCosteIndirecto
        .add(importeTotalConcedidoUniversidadCostesIndirectos);
    this.importeTotalPresupuestoUniversidad = importeTotalPresupuestoUniversidadSinCosteIndirecto
        .add(importeTotalPresupuestoUniversidadCostesIndirectos);

    this.importeTotalConcedido = importeTotalConcedidoUniversidad.add(importeTotalConcedidoSocios);
    this.importeTotalPresupuesto = importeTotalPresupuestoUniversidad.add(importeTotalPresupuestoSocios);
  }
}
