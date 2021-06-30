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
 * ConflictoInteres
 */

@Entity
@Table(name = "conflicto_interes")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ConflictoInteres extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conflicto_interes_seq")
  @SequenceGenerator(name = "conflicto_interes_seq", sequenceName = "conflicto_interes_seq", allocationSize = 1)
  private Long id;

  /** Evaluador */
  @ManyToOne
  @JoinColumn(name = "evaluador_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CONFLICTOINTERES_EVALUADOR"))
  private Evaluador evaluador;

  /** Referencia de la persona conflicto */
  @Column(name = "persona_conflicto_ref", nullable = false)
  private String personaConflictoRef;

}