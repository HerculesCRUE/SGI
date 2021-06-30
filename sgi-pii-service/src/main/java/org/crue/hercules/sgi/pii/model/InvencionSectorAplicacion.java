package org.crue.hercules.sgi.pii.model;

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
@Table(name = "invencion_sector_aplicacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvencionSectorAplicacion extends BaseEntity {

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invencion_sector_aplicacion_seq")
  @SequenceGenerator(name = "invencion_sector_aplicacion_seq", sequenceName = "invencion_sector_aplicacion_seq", allocationSize = 1)
  private Long id;

  /** Invencion Id */
  @Column(name = "invencion_id", nullable = false)
  private Long invencionId;

  /** Sector Aplicación. */
  @ManyToOne
  @JoinColumn(name = "sector_aplicacion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_INVENCIONSECTORAPLICACION_SECTORAPLICACION"))
  private SectorAplicacion sectorAplicacion;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "invencion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INVENCIONSECTORAPLICACION_INVENCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Invencion invencion = null;
}
