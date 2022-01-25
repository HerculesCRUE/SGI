package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = ProyectoPeriodoAmortizacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoPeriodoAmortizacion extends BaseEntity {

  protected static final String TABLE_NAME = "proyecto_periodo_amortizacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Importe */
  @Column(name = "importe", nullable = false)
  private BigDecimal importe;

  /** Fecha límite amortización */
  @Column(name = "fecha_limite_amortizacion", nullable = false)
  private Instant fechaLimiteAmortizacion;

  /** Proyecto SGE */
  @Column(name = "proyecto_sge_ref", nullable = false)
  private String proyectoSGERef;

  /** Proyecto Entidad Financiadora id */
  @NotNull
  @Column(name = "proyecto_entidad_financiadora_id", nullable = false)
  private Long proyectoEntidadFinanciadoraId;

  /** Proyecto Anualidad Id */
  @NotNull
  @Column(name = "proyecto_anualidad_id", nullable = false)
  private Long proyectoAnualidadId;

  @ManyToOne
  @JoinColumn(name = "proyecto_entidad_financiadora_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOPERIODOAMORTIZACION_PROYECTOENTIDADFINANCIADORA"), nullable = false)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoEntidadFinanciadora proyectoEntidadFinanciadora = null;

  @ManyToOne
  @JoinColumn(name = "proyecto_anualidad_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOPERIODOAMORTIZACION_PROYECTOANUALIDAD"), nullable = false)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoAnualidad proyectoAnualidad = null;

}
