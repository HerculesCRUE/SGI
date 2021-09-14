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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
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

@Entity
@Table(name = "solicitud_proyecto_entidad_financiadora_ajena", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "solicitud_proyecto_id",
        "entidad_ref" }, name = "UK_SOLICITUDPROYECTOENTIDADFINANCIADORAAJENA_SOLICITUDPROYECTO_ENTIDAD") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoEntidadFinanciadoraAjena extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_entidad_financiadora_ajena_seq")
  @SequenceGenerator(name = "solicitud_proyecto_entidad_financiadora_ajena_seq", sequenceName = "solicitud_proyecto_entidad_financiadora_ajena_seq", allocationSize = 1)
  private Long id;

  /** SolicitudProyecto Id */
  @Column(name = "solicitud_proyecto_id", nullable = false)
  @NotNull
  private Long solicitudProyectoId;

  /** Entidad Financiadora */
  @Column(name = "entidad_ref", length = 50, nullable = false)
  @NotEmpty
  @Size(max = 50)
  private String entidadRef;

  /** FuenteFinanciacion */
  @ManyToOne
  @JoinColumn(name = "fuente_financiacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOENTIDADFINANCIADORAAJENA_FUENTEFINANCIACION"))
  private FuenteFinanciacion fuenteFinanciacion;

  /** TipoFinanciacion */
  @ManyToOne
  @JoinColumn(name = "tipo_financiacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOENTIDADFINANCIADORAAJENA_TIPOFINANCIACION"))
  private TipoFinanciacion tipoFinanciacion;

  /** PorcentajeFinanciacion */
  @Column(name = "porcentaje_financiacion", nullable = true, precision = 5, scale = 2)
  @Min(0)
  @Max(100)
  private BigDecimal porcentajeFinanciacion;

  /** Importe financiacion */
  @Column(name = "importe_financiacion", nullable = true)
  private BigDecimal importeFinanciacion;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOENTIDADFINANCIADORAAJENA_SOLICITUDPROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudProyecto solicitudProyecto = null;
}
