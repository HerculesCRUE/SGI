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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "convocatoria_entidad_financiadora")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ConvocatoriaEntidadFinanciadora extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_entidad_financiadora_seq")
  @SequenceGenerator(name = "convocatoria_entidad_financiadora_seq", sequenceName = "convocatoria_entidad_financiadora_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria Id */
  @Column(name = "convocatoria_id", nullable = false)
  @NotNull
  private Long convocatoriaId;

  /** Entidad Financiadora */
  @Column(name = "entidad_ref", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String entidadRef;

  /** FuenteFinanciacion */
  @ManyToOne
  @JoinColumn(name = "fuente_financiacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAENTIDADFINANCIADORA_FUENTEFINANCIACION"))
  private FuenteFinanciacion fuenteFinanciacion;

  /** TipoFinanciacion */
  @ManyToOne
  @JoinColumn(name = "tipo_financiacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAENTIDADFINANCIADORA_TIPOFINANCIACION"))
  private TipoFinanciacion tipoFinanciacion;

  /** PorcentajeFinanciacion */
  @Column(name = "porcentaje_financiacion", nullable = true)
  @Min(0)
  private Integer porcentajeFinanciacion;

  /** Importe financiacion */
  @Column(name = "importe_financiacion", nullable = true)
  private BigDecimal importeFinanciacion;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CONVOCATORIAENTIDADFINANCIADORA_CONVOCATORIA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Convocatoria convocatoria = null;
}
