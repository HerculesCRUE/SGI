package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Configuracion
 */

@Entity
@Table(name = "configuracion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Configuracion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuracion_seq")
  @SequenceGenerator(name = "configuracion_seq", sequenceName = "configuracion_seq", allocationSize = 1)
  private Long id;

  /** diasArchivadaInactivo. */
  @Column(name = "dias_archivada_inactivo", nullable = false, unique = true)
  @NotNull
  private Integer diasArchivadaInactivo;

  /** mesesArchivadaPendienteCorrecciones. */
  @Column(name = "meses_archivada_pendiente_correcciones", nullable = false, unique = true)
  @NotNull
  private Integer mesesArchivadaPendienteCorrecciones;

  /** diasLimiteEvaluador. */
  @Column(name = "dias_limite_evaluador", nullable = false, unique = true)
  @NotNull
  private Integer diasLimiteEvaluador;

  /** diasAvisoRetrospectiva. */
  @Column(name = "dias_aviso_retrospectiva", nullable = false, unique = true)
  @NotNull
  private Integer diasAvisoRetrospectiva;

  /** Duración en años de los proyectos de evaluación */
  @Column(name = "duracion_proyecto_evaluacion", nullable = false, unique = true)
  @NotNull
  private Integer duracionProyectoEvaluacion;

}