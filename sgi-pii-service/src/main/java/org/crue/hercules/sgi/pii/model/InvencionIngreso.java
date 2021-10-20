package org.crue.hercules.sgi.pii.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "invencion_ingreso")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvencionIngreso extends BaseEntity {

  public static final int ESTADO_LENGTH = 25;

  public enum Estado {
    /** Sin repartir */
    NO_REPARTIDO,
    /** Parcialmente repartido */
    REPARTIDO_PARCIALMENTE,
    /** Repartido */
    REPARTIDO
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invencion_ingreso_seq")
  @SequenceGenerator(name = "invencion_ingreso_seq", sequenceName = "invencion_ingreso_seq", allocationSize = 1)
  private Long id;

  /** Invencion Id */
  @Column(name = "invencion_id", nullable = false)
  private Long invencionId;

  /** Referencia a un Ingreso */
  @Column(name = "ingreso_ref", nullable = false)
  private String ingresoRef;

  /** Importe pendiente de repartir */
  @Column(name = "importe_pendiente_Repartir", nullable = true)
  private BigDecimal importePendienteRepartir;

  /** Estado del ingreso de la invención */
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", length = ESTADO_LENGTH, nullable = false)
  private Estado estado;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "invencion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INVENCIONINGRESO_INVENCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Invencion invencion = null;
}
