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
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Evaluacion
 */

@Entity
@Table(name = "evaluacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Evaluacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaluacion_seq")
  @SequenceGenerator(name = "evaluacion_seq", sequenceName = "evaluacion_seq", allocationSize = 1)
  private Long id;

  /** Memoria */
  @ManyToOne
  @JoinColumn(name = "memoria_id", nullable = false, foreignKey = @ForeignKey(name = "FK_EVALUACION_MEMORIA"))
  @NotNull
  private Memoria memoria;

  /** Convocatoria reunión */
  @ManyToOne
  @JoinColumn(name = "convocatoria_reunion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_EVALUACION_CONVOCATORIAREUNION"))
  @NotNull
  private ConvocatoriaReunion convocatoriaReunion;

  /** Tipo evaluacion */
  @OneToOne
  @JoinColumn(name = "tipo_evaluacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_EVALUACION_TIPOEVALUACION"))
  @NotNull
  private TipoEvaluacion tipoEvaluacion;

  /** Dictamen */
  @ManyToOne
  @JoinColumn(name = "dictamen_id", nullable = true, foreignKey = @ForeignKey(name = "FK_EVALUACION_DICTAMEN"))
  private Dictamen dictamen;

  /** Evaluador 1 */
  @ManyToOne
  @JoinColumn(name = "evaluador1_id", nullable = false, foreignKey = @ForeignKey(name = "FK_EVALUACION_EVALUADOR1"))
  @NotNull
  private Evaluador evaluador1;

  /** Evaluador 2 */
  @ManyToOne
  @JoinColumn(name = "evaluador2_id", nullable = false, foreignKey = @ForeignKey(name = "FK_EVALUACION_EVALUADOR2"))
  @NotNull
  private Evaluador evaluador2;

  /** Fecha Dictamen */
  @Column(name = "fecha_dictamen")
  private Instant fechaDictamen;

  /** Version */
  @Column(name = "version", nullable = false)
  @NotNull
  private Integer version;

  /** Es revisión mínima */
  @Column(name = "es_rev_minima", columnDefinition = "boolean default false", nullable = false)
  private Boolean esRevMinima;

  /** Comentario */
  @Column(name = "comentario", length = 2000, nullable = true)
  @Size(max = 2000)
  private String comentario;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

}