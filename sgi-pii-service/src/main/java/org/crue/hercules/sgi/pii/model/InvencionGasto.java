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
@Table(name = "invencion_gasto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvencionGasto extends BaseEntity {

  public static final int ESTADO_LENGTH = 25;

  public enum Estado {
    /** Sin deducir */
    NO_DEDUCIDO,
    /** Parcialmente deducido */
    DEDUCIDO_PARCIALMENTE,
    /** Deducido */
    DEDUCIDO
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invencion_gasto_seq")
  @SequenceGenerator(name = "invencion_gasto_seq", sequenceName = "invencion_gasto_seq", allocationSize = 1)
  private Long id;

  /** Invencion Id */
  @Column(name = "invencion_id", nullable = false)
  private Long invencionId;

  /** Solicitud de Protección Id */
  @Column(name = "solicitud_proteccion_id", nullable = true)
  private Long solicitudProteccionId;

  /** Referencia a un Gasto */
  @Column(name = "gasto_ref", nullable = false)
  private String gastoRef;

  /** Importe pendiente de deducir */
  @Column(name = "importe_pendiente_deducir", nullable = false)
  private BigDecimal importePendienteDeducir;

  /** Estado del gasto de la invención */
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", length = ESTADO_LENGTH, nullable = false)
  private Estado estado;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "invencion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INVENCIONGASTO_INVENCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Invencion invencion = null;

  @ManyToOne
  @JoinColumn(name = "solicitud_proteccion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INVENCIONGASTO_SOLICITUDPROTECCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudProteccion solicitudProteccion = null;
}
