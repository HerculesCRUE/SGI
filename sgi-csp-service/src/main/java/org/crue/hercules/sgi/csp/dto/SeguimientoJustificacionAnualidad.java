package org.crue.hercules.sgi.csp.dto;

import java.time.Instant;

import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeguimientoJustificacionAnualidad {
  /** {@link ProyectoPeriodoJustificacion#id} */
  private Long proyectoPeriodoJustificacionId;
  /** {@link ProyectoPeriodoJustificacionSeguimiento#id} */
  private Long proyectoPeriodoJustificacionSeguimientoId;
  /** {@link ProyectoProyectoSge#proyectoId} */
  private Long proyectoId;
  /** {@link ProyectoPeriodoJustificacion#identificadorJustificacion} */
  private String identificadorJustificacion;
  /** {@link ProyectoPeriodoJustificacion#fechaPresentacionJustificacion} */
  private Instant fechaPresentacionJustificacion;
  /** {@link ProyectoAnualidad#anio} */
  private Integer anio;
}
