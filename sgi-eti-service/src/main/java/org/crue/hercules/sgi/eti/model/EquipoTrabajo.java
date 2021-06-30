package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * EquipoTrabajo
 */

@Entity
@Table(name = "equipo_trabajo")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class EquipoTrabajo extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipo_trabajo_seq")
  @SequenceGenerator(name = "equipo_trabajo_seq", sequenceName = "equipo_trabajo_seq", allocationSize = 1)
  private Long id;

  /** Referencia usuario */
  @Column(name = "persona_ref", length = 250, nullable = false)
  private String personaRef;

  /** Peticion Evaluación */
  @ManyToOne
  @JoinColumn(name = "peticion_evaluacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_EQUIPOTRABAJO_PETICIONEVALUACION"))
  private PeticionEvaluacion peticionEvaluacion;

}