package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import org.crue.hercules.sgi.csp.model.EstadoProyecto;

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
  private String proyectoTitulo;
  private String proyectoAcronimo;
  private EstadoProyecto proyectoEstado;
  private String proyectoSgeRef;
  private Boolean enviadoSge;

  public ProyectoAnualidadNotificacionSge(Long id, Integer anio, Instant proyectoFechaInicio, Instant proyectoFechaFin,
      BigDecimal totalGastos, Long proyectoId, String proyectoTitulo, String proyectoAcronimo,
      EstadoProyecto proyectoEstado, String proyectoSgeRef, Boolean enviadoSge) {
    this.id = id;
    this.anio = anio;
    this.proyectoFechaInicio = proyectoFechaInicio;
    this.proyectoFechaFin = proyectoFechaFin;
    this.totalGastos = totalGastos;
    this.proyectoId = proyectoId;
    this.proyectoTitulo = proyectoTitulo;
    this.proyectoAcronimo = proyectoAcronimo;
    this.proyectoEstado = proyectoEstado;
    this.proyectoSgeRef = proyectoSgeRef;
    this.enviadoSge = enviadoSge;
  }
}
