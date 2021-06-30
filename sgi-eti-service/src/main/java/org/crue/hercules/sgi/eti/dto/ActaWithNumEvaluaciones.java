package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.eti.model.TipoEstadoActa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActaWithNumEvaluaciones implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** id Acta. */
  private Long id;

  /** Abreviatura comité */
  private String comite;

  /** Fecha de evaluación */
  private Instant fechaEvaluacion;

  /** Número de acta */
  private Integer numeroActa;

  /** Tipo convocatoria */
  private String convocatoria;

  /** Nº de evaluaciones (iniciales) */
  private Integer numEvaluaciones;

  /** Nº de revisiones */
  private Integer numRevisiones;

  /** Nº total */
  private Integer numTotal;

  /** Estado del acta */
  private TipoEstadoActa estadoActa;

  /** Número de evaluaciones no evaluadas. */
  private Integer numEvaluacionesNoEvaluadas;

  public ActaWithNumEvaluaciones(Long id, String comite, Instant fechaEvaluacion, Integer numeroActa,
      String convocatoria, Long numEvaluaciones, Long numRevisiones, Long numEvaluacionesNoEvaluadas,
      TipoEstadoActa estadoActa) {
    this.id = id;
    this.comite = comite;
    this.fechaEvaluacion = fechaEvaluacion;
    this.numeroActa = numeroActa;
    this.convocatoria = convocatoria;
    this.numEvaluaciones = (numEvaluaciones == null) ? 0 : numEvaluaciones.intValue();
    this.numRevisiones = (numRevisiones == null) ? 0 : numRevisiones.intValue();
    this.numTotal = (this.numEvaluaciones + this.numRevisiones);
    this.estadoActa = estadoActa;
    this.numEvaluacionesNoEvaluadas = numEvaluacionesNoEvaluadas.intValue();
  }
}