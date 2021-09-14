package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.csp.enums.TipoJustificacion;

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
public class ProyectoPeriodoJustificacionInput implements Serializable {

  private Long id;
  private Long proyectoId;
  private Long numPeriodo;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Instant fechaInicioPresentacion;
  private Instant fechaFinPresentacion;
  private TipoJustificacion tipoJustificacion;
  private String observaciones;
  private Long convocatoriaPeriodoJustificacionId;

}
