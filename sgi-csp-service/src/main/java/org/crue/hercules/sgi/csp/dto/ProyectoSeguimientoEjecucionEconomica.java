package org.crue.hercules.sgi.csp.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoSeguimientoEjecucionEconomica {

  private Long id;
  private Long proyectoId;
  private String proyectoSgeRef;
  private String nombre;
  private String codigoExterno;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Instant fechaFinDefinitiva;
  private String tituloConvocatoria;
  private BigDecimal importeConcedido;
  private BigDecimal importeConcedidoCostesIndirectos;
}
