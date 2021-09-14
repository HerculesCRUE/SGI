package org.crue.hercules.sgi.csp.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProyectoConceptoGasto
 */
@Entity
@Table(name = "proyecto_concepto_gasto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoConceptoGasto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_concepto_gasto_seq")
  @SequenceGenerator(name = "proyecto_concepto_gasto_seq", sequenceName = "proyecto_concepto_gasto_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  @NotNull
  private Long proyectoId;

  /** ConceptoGasto */
  @ManyToOne
  @JoinColumn(name = "concepto_gasto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOCONCEPTOGASTO_CONCEPTOGASTO"))
  @NotNull
  private ConceptoGasto conceptoGasto;

  /** Observaciones */
  @Column(name = "observaciones", length = 250, nullable = true)
  @Size(max = 250)
  private String observaciones;

  /** Importe máximo */
  @Column(name = "importe_maximo", nullable = true)
  @Min(0)
  private Double importeMaximo;

  /** Permitido */
  @Column(name = "permitido", nullable = true)
  private Boolean permitido;

  /** Fechas inicio */
  @Column(name = "fecha_inicio", nullable = true)
  private Instant fechaInicio;

  /** Fecha fin */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  /** Convocatoria concepto gasto Id */
  @Column(name = "convocatoria_concepto_gasto_id", nullable = true)
  private Long convocatoriaConceptoGastoId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOCONCEPTOGASTO_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "proyectoConceptoGasto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @JsonIgnore
  private final List<ProyectoConceptoGastoCodigoEc> codigosEc = null;

}