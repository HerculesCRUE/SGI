package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;

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
public class ProyectoPeriodoSeguimientoOutput implements Serializable {
  private Long id;
  private Long proyectoId;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Integer numPeriodo;
  private Instant fechaInicioPresentacion;
  private Instant fechaFinPresentacion;
  private TipoSeguimiento tipoSeguimiento;
  private String observaciones;
  private Long convocatoriaPeriodoSeguimientoId;
  private Instant fechaPresentacionDocumentacion;
}
