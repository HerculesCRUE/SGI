package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.MemoriaTitulo;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.repository.custom.CustomMemoriaRepositoryImpl;

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

  private Collection<MemoriaTitulo> titulo;

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

  private Integer version;

  /**
   * Constructor especializado para las queries
   * {@link CustomMemoriaRepositoryImpl#findMemoriasEvaluacion(Long, String)} y
   * {@link CustomMemoriaRepositoryImpl#findAllMemoriasEvaluaciones(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Pageable, String)}
   * 
   * @param memoria
   * @param fechaEvaluacion
   * @param fechaLimite
   * @param isResponsable
   * @param retrospectiva
   * @param solicitanteRef
   * @param tutorRef
   */
  public MemoriaPeticionEvaluacion(Memoria memoria,
      Instant fechaEvaluacion, Instant fechaLimite, boolean isResponsable,
      Retrospectiva retrospectiva, String solicitanteRef,
      String tutorRef) {

    this.id = memoria.getId();
    this.responsableRef = memoria.getPersonaRef();
    this.numReferencia = memoria.getNumReferencia();
    this.titulo = memoria.getTitulo();
    this.comite = memoria.getComite();
    this.estadoActual = memoria.getEstadoActual();
    this.fechaEvaluacion = fechaEvaluacion;
    this.fechaLimite = fechaLimite;
    this.isResponsable = isResponsable;
    this.activo = memoria.getActivo();
    this.requiereRetrospectiva = memoria.getRequiereRetrospectiva();
    this.retrospectiva = retrospectiva;
    this.solicitanteRef = solicitanteRef;
    this.tutorRef = tutorRef;
    this.version = memoria.getVersion();
  }

}