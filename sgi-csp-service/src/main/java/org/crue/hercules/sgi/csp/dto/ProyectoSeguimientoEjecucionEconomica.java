package org.crue.hercules.sgi.csp.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import org.crue.hercules.sgi.csp.model.ConvocatoriaTitulo;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;

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
  private Set<ProyectoTitulo> nombre;
  private String codigoExterno;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Instant fechaFinDefinitiva;
  private Set<ConvocatoriaTitulo> tituloConvocatoria;
  private BigDecimal importeConcedido;
  private BigDecimal importeConcedidoCostesIndirectos;
}
