package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto_socio")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoSocio extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_socio_seq")
  @SequenceGenerator(name = "proyecto_socio_seq", sequenceName = "proyecto_socio_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  @NotNull
  private Long proyectoId;

  /** Empresa ref. */
  @Column(name = "empresa_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String empresaRef;

  /** Rol socio. */
  @ManyToOne
  @JoinColumn(name = "rol_socio_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOSOCIO_ROLSOCIO"))
  @NotNull
  private RolSocio rolSocio;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = true)
  private Instant fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  /** Numero investigadores. */
  @Column(name = "num_investigadores", nullable = true)
  @Min(0)
  private Integer numInvestigadores;

  /** Importe concedido. */
  @Column(name = "importe_concedido", nullable = true)
  private BigDecimal importeConcedido;

  /** Importe presupuesto. */
  @Column(name = "importe_presupuesto", nullable = true)
  private BigDecimal importePresupuesto;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOSOCIO_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;

  @OneToMany(mappedBy = "proyectoSocio")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoSocioEquipo> equipos = null;

  @OneToMany(mappedBy = "proyectoSocio")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoSocioPeriodoJustificacion> periodosJustificacion = null;

  @OneToMany(mappedBy = "proyectoSocio")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoSocioPeriodoPago> periodosPago = null;
}