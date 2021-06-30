package org.crue.hercules.sgi.csp.model;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ConvocatoriaConceptoGastoCodigoEc
 */

@Entity
@Table(name = "convocatoria_concepto_gasto_codigo_ec")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ConvocatoriaConceptoGastoCodigoEc extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_concepto_gasto_codigo_ec_seq")
  @SequenceGenerator(name = "convocatoria_concepto_gasto_codigo_ec_seq", sequenceName = "convocatoria_concepto_gasto_codigo_ec_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria Id */
  @Column(name = "convocatoria_concepto_gasto_id", nullable = false)
  @NotNull
  private Long convocatoriaConceptoGastoId;

  /** Ref código económico. */
  @Column(name = "codigo_economico_ref", length = 50, nullable = false)
  @NotBlank
  @Size(max = 50)
  private String codigoEconomicoRef;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = true)
  private Instant fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  /** Observaciones */
  @Column(name = "observaciones", length = 250, nullable = true)
  @Size(max = 250)
  private String observaciones;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_concepto_gasto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIACONCEPTOGASTOCODIGOEC_CONVOCATORIACONCEPTOGASTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConvocatoriaConceptoGasto convocatoriaConceptoGasto = null;
}