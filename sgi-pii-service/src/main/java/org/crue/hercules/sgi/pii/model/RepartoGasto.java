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
@Table(name = "reparto_gasto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepartoGasto extends BaseEntity {

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reparto_gasto_seq")
  @SequenceGenerator(name = "reparto_gasto_seq", sequenceName = "reparto_gasto_seq", allocationSize = 1)
  private Long id;

  /** Reparto Id */
  @Column(name = "reparto_id", nullable = false)
  private Long repartoId;

  /** InvencionGasto Id */
  @Column(name = "invencion_gasto_id", nullable = false)
  private Long invencionGastoId;

  /** Importe a Deducir */
  @Column(name = "importe_a_deducir", nullable = false)
  private BigDecimal importeADeducir;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "reparto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REPARTOGASTO_REPARTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Reparto reparto = null;

  @ManyToOne
  @JoinColumn(name = "invencion_gasto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REPARTOGASTO_INVENCIONGASTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final InvencionGasto invencionGasto = null;
}
