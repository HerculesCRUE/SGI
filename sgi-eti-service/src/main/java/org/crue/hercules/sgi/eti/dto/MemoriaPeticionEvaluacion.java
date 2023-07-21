package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoriaPeticionEvaluacion implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private Long id;

  private String responsableRef;

  private String numReferencia;

  private String titulo;

  private Comite comite;

  private TipoEstadoMemoria estadoActual;

  private boolean requiereRetrospectiva;

  private Retrospectiva retrospectiva;

  private Instant fechaEvaluacion;

  private Instant fechaLimite;

  private boolean isResponsable;

  private boolean activo;

  private String solicitanteRef;

  private String tutorRef;

  public MemoriaPeticionEvaluacion(Long id, String responsableRef, String numReferencia, String titulo, Comite comite,
      TipoEstadoMemoria estadoActual, Instant fechaEvaluacion, Instant fechaLimite, boolean isResponsable,
      boolean activo, boolean requiereRetrospectiva, Retrospectiva retrospectiva, String solicitanteRef,
      String tutorRef) {

    this.id = id;
    this.responsableRef = responsableRef;
    this.numReferencia = numReferencia;
    this.titulo = titulo;
    this.comite = comite;
    this.estadoActual = estadoActual;
    this.fechaEvaluacion = fechaEvaluacion;
    this.fechaLimite = fechaLimite;
    this.isResponsable = isResponsable;
    this.activo = activo;
    this.requiereRetrospectiva = requiereRetrospectiva;
    this.retrospectiva = retrospectiva;
    this.solicitanteRef = solicitanteRef;
    this.tutorRef = tutorRef;
  }

}