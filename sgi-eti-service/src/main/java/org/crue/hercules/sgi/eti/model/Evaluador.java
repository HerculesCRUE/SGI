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
 * Evaluador
 */

@Entity
@Table(name = "evaluador")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Evaluador extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaluador_seq")
  @SequenceGenerator(name = "evaluador_seq", sequenceName = "evaluador_seq", allocationSize = 1)
  private Long id;

  /** Cargo Comité */
  @ManyToOne
  @JoinColumn(name = "cargo_comite_id", nullable = false, foreignKey = @ForeignKey(name = "FK_EVALUADOR_CARGOCOMITE"))
  @NotNull
  private CargoComite cargoComite;

  /** Comité */
  @ManyToOne
  @JoinColumn(name = "comite_id", nullable = false, foreignKey = @ForeignKey(name = "FK_EVALUADOR_COMITE"))
  @NotNull
  private Comite comite;

  /** Fecha Alta */
  @Column(name = "fecha_alta")
  private Instant fechaAlta;

  /** Fecha Baja */
  @Column(name = "fecha_baja")
  private Instant fechaBaja;

  /** Resumen */
  @Column(name = "resumen", length = 4000, columnDefinition = "clob")
  private String resumen;

  /** Referencia persona */
  @Column(name = "persona_ref", length = 250, nullable = false)
  private String personaRef;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  // Relations mapping, only for JPA metamodel generation
  @Column(name = "comite_id", insertable = false, updatable = false)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Long comiteId = null;

}