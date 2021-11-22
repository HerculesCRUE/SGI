package org.crue.hercules.sgi.pii.model;

import java.math.BigDecimal;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reparto_equipo_inventor")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepartoEquipoInventor extends BaseEntity {
  public static final int REF_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reparto_equipo_inventor_seq")
  @SequenceGenerator(name = "reparto_equipo_inventor_seq", sequenceName = "reparto_equipo_inventor_seq", allocationSize = 1)
  private Long id;

  /** Reparto Id */
  @Column(name = "reparto_id", nullable = false)
  private Long repartoId;

  /** Referencia a un InvencionInventor */
  @Column(name = "invencion_inventor_id", nullable = false)
  private Long invencionInventorId;

  /** Proyecto ref */
  @Column(name = "proyecto_ref", length = REF_LENGTH, nullable = true)
  private String proyectoRef;

  /** Importe Nomina */
  @Column(name = "importe_nomina", nullable = false)
  private BigDecimal importeNomina;

  /** Importe Proyecto */
  @Column(name = "importe_proyecto", nullable = false)
  private BigDecimal importeProyecto;

  /** Importe Otros */
  @Column(name = "importe_otros", nullable = false)
  private BigDecimal importeOtros;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "reparto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REPARTOEQUIPOINVENTOR_REPARTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Reparto reparto = null;

  @ManyToOne
  @JoinColumn(name = "invencion_inventor_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REPARTOEQUIPOINVENTOR_INVENCIONINVENTOR"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final InvencionInventor invencionInventor = null;
}
