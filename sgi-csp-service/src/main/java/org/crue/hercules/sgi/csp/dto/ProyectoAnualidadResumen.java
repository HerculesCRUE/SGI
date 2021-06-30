package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

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
public class ProyectoAnualidadResumen implements Serializable {
  private Long id;
  private Integer anio;
  private Instant fechaInicio;
  private Instant fechaFin;
  private BigDecimal totalGastosPresupuesto;
  private BigDecimal totalGastosConcedido;
  private BigDecimal totalIngresos;
  private Boolean presupuestar;
  private Boolean enviadoSge;
}
