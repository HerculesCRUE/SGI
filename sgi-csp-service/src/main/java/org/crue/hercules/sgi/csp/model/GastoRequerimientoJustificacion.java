package org.crue.hercules.sgi.csp.model;

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
@Table(name = GastoRequerimientoJustificacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoRequerimientoJustificacion extends BaseEntity {

  protected static final String TABLE_NAME = "gasto_requerimiento_justificacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GastoRequerimientoJustificacion.SEQUENCE_NAME)
  @SequenceGenerator(name = GastoRequerimientoJustificacion.SEQUENCE_NAME, sequenceName = GastoRequerimientoJustificacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Gasto ref */
  @Column(name = "gasto_ref", nullable = false, length = EXTERNAL_REF_LENGTH)
  private String gastoRef;

  /** Requierimiento justificacion */
  @Column(name = "requerimiento_justificacion_id", nullable = true)
  private Long requerimientoJustificacionId;

  /** Importe aceptado */
  @Column(name = "importe_aceptado", nullable = true)
  private BigDecimal importeAceptado;

  /** Importe rechazado */
  @Column(name = "importe_rechazado", nullable = true)
  private BigDecimal importeRechazado;

  /** Importe alegado */
  @Column(name = "importe_alegado", nullable = true)
  private BigDecimal importeAlegado;

  /** Aceptado */
  @Column(name = "aceptado", nullable = true)
  private Boolean aceptado;

  /** Incidencia */
  @Column(name = "incidencia", length = DEFAULT_LONG_TEXT_LENGTH, nullable = true)
  private String incidencia;

  /** Identificador justificacion */
  @Column(name = "alegacion", length = DEFAULT_LONG_TEXT_LENGTH, nullable = true)
  private String alegacion;

  /** Identificador justificacion */
  @Column(name = "identificador_justificacion", length = EXTERNAL_REF_LENGTH, nullable = true)
  private String identificadorJustificacion;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "requerimiento_justificacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GASTOREQUERIMIENTOJUSTIFICACION_REQUERIMIENTOJUSTIFICACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private RequerimientoJustificacion requerimientoJustificacion;
}
