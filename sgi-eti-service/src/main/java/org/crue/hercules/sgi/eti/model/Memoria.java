package org.crue.hercules.sgi.eti.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Memoria
 */

@Entity
@Table(name = "memoria")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Memoria extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memoria_seq")
  @SequenceGenerator(name = "memoria_seq", sequenceName = "memoria_seq", allocationSize = 1)
  @NotNull(groups = { Update.class })
  private Long id;

  /** Referencia memoria */
  @Column(name = "num_referencia", length = 250, nullable = false)
  @NotNull(groups = { Update.class })
  private String numReferencia;

  /** Petición evaluación */
  @ManyToOne
  @JoinColumn(name = "peticion_evaluacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MEMORIA_PETICIONEVALUACION"))
  @NotNull
  private PeticionEvaluacion peticionEvaluacion;

  /** Comité */
  @ManyToOne
  @JoinColumn(name = "comite_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MEMORIA_COMITE"))
  @NotNull
  private Comite comite;

  /** Título */
  @Column(name = "titulo", length = 2000, nullable = true)
  private String titulo;

  /** Referencia usuario */
  @Column(name = "persona_ref", length = 250, nullable = true)
  private String personaRef;

  /** Tipo Memoria */
  @ManyToOne
  @JoinColumn(name = "tipo_memoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MEMORIA_TIPOMEMORIA"))
  @NotNull
  private TipoMemoria tipoMemoria;

  /** Estado Memoria Actual */
  @OneToOne
  @JoinColumn(name = "estado_actual_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MEMORIA_ESTADOACTUAL"))
  @NotNull(groups = { Update.class })
  private TipoEstadoMemoria estadoActual;

  /** Fecha envio secretaria. */
  @Column(name = "fecha_envio_secretaria", nullable = true)
  private Instant fechaEnvioSecretaria;

  /** Indicador require retrospectiva */
  @Column(name = "requiere_retrospectiva", columnDefinition = "boolean default false", nullable = false)
  @NotNull(groups = { Update.class })
  private Boolean requiereRetrospectiva;

  /** Retrospectiva. */
  @OneToOne
  @JoinColumn(name = "retrospectiva_id", nullable = true, foreignKey = @ForeignKey(name = "FK_MEMORIA_RETROSPECTIVA"))
  private Retrospectiva retrospectiva;

  /** Version */
  @Column(name = "version", nullable = false)
  @NotNull(groups = { Update.class })
  private Integer version;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  /** Memoria original */
  @OneToOne
  @JoinColumn(name = "memoria_original_id", nullable = true, foreignKey = @ForeignKey(name = "FK_MEMORIA_MEMORIAORIGINAL"))
  private Memoria memoriaOriginal;

  // Relations mapping, only for JPA metamodel generation
  @Column(name = "peticion_evaluacion_id", insertable = false, updatable = false)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Long peticionEvaluacionId = null;

}