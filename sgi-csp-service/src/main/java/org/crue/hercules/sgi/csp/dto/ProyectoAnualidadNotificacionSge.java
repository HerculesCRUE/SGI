package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;

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
public class ProyectoAnualidadNotificacionSge implements Serializable {
  private Long id;
  private Integer anio;
  private Instant proyectoFechaInicio;
  private Instant proyectoFechaFin;
  private BigDecimal totalGastos;
  private BigDecimal totalIngresos;
  private Long proyectoId;
  private Collection<ProyectoTitulo> proyectoTitulo;
  private String proyectoAcronimo;
  private EstadoProyecto proyectoEstado;
  private String proyectoSgeRef;
  private Boolean enviadoSge;
}
