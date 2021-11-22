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
@Table(name = "reparto_ingreso")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepartoIngreso extends BaseEntity {

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reparto_ingreso_seq")
  @SequenceGenerator(name = "reparto_ingreso_seq", sequenceName = "reparto_ingreso_seq", allocationSize = 1)
  private Long id;

  /** Reparto Id */
  @Column(name = "reparto_id", nullable = false)
  private Long repartoId;

  /** InvencionIngreso Id */
  @Column(name = "invencion_ingreso_id", nullable = false)
  private Long invencionIngresoId;

  /** Importe a Repartir */
  @Column(name = "importe_a_repartir", nullable = false)
  private BigDecimal importeARepartir;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "reparto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REPARTOINGRESO_REPARTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Reparto reparto = null;

  @ManyToOne
  @JoinColumn(name = "invencion_ingreso_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REPARTOINGRESO_INVENCIONINGRESO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final InvencionIngreso invencionIngreso = null;
}
