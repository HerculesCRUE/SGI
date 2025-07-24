package org.crue.hercules.sgi.eti.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Asistentes
 */

@Entity
@Table(name = "asistentes")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Asistentes extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asistentes_seq")
  @SequenceGenerator(name = "asistentes_seq", sequenceName = "asistentes_seq", allocationSize = 1)
  private Long id;

  /** Evaluador */
  @ManyToOne
  @JoinColumn(name = "evaluador_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ASISTENTES_EVALUADOR"))
  @NotNull
  private Evaluador evaluador;

  /** Convocatoria reunión */
  @ManyToOne
  @JoinColumn(name = "convocatoria_reunion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ASISTENTES_CONVOCATORIAREUNION"))
  @NotNull
  private ConvocatoriaReunion convocatoriaReunion;

  /** Asistencia */
  @Column(name = "asistencia", nullable = false)
  private Boolean asistencia;

  /** Motivo */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "asistentes_motivo", joinColumns = @JoinColumn(name = "asistentes_id"))
  @Valid
  private Set<AsistentesMotivo> motivo = new HashSet<>();

}