package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
@Table(name = AlegacionRequerimiento.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlegacionRequerimiento extends BaseEntity {

  protected static final String TABLE_NAME = "alegacion_requerimiento";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = AlegacionRequerimiento.SEQUENCE_NAME)
  @SequenceGenerator(name = AlegacionRequerimiento.SEQUENCE_NAME, sequenceName = AlegacionRequerimiento.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "requerimiento_justificacion_id", nullable = false, unique = true)
  private Long requerimientoJustificacionId;

  /** Fecha alegacion */
  @Column(name = "fecha_alegacion", nullable = true)
  private Instant fechaAlegacion;

  /** Importe total alegado */
  @Column(name = "importe_alegado", nullable = true)
  private BigDecimal importeAlegado;

  /** Importe alegado costes directos */
  @Column(name = "importe_alegado_cd", nullable = true)
  private BigDecimal importeAlegadoCd;

  /** Importe alegado costes indirectos */
  @Column(name = "importe_alegado_ci", nullable = true)
  private BigDecimal importeAlegadoCi;

  /** Importe total reintegrado */
  @Column(name = "importe_reintegrado", nullable = true)
  private BigDecimal importeReintegrado;

  /** Importe reintegrado costes directos */
  @Column(name = "importe_reintegrado_cd", nullable = true)
  private BigDecimal importeReintegradoCd;

  /** Importe reintegrado costes indirectos */
  @Column(name = "importe_reintegrado_ci", nullable = true)
  private BigDecimal importeReintegradoCi;

  /** Intereses reintegrados */
  @Column(name = "intereses_reintegrados", nullable = true)
  private BigDecimal interesesReintegrados;

  /** Fecha reintegro */
  @Column(name = "fecha_reintegro", nullable = true)
  private Instant fechaReintegro;

  /** Justificante pago del reintegro */
  @Column(name = "justificante_reintegro", nullable = true, length = DEFAULT_TEXT_LENGTH)
  private String justificanteReintegro;

  /** Observaciones */
  @Column(name = "observaciones", nullable = true, length = DEFAULT_LONG_TEXT_LENGTH)
  private String observaciones;

  // Relation mappings for JPA metamodel generation only
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requerimiento_justificacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ALEGACIONREQUERIMIENTO_REQUERIMIENTOJUSTIFICACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RequerimientoJustificacion requerimientoJustificacion = null;
}
