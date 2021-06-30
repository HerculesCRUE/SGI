package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
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
@Table(name = "solicitud_proyecto_socio")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyectoSocio extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_proyecto_socio_seq")
  @SequenceGenerator(name = "solicitud_proyecto_socio_seq", sequenceName = "solicitud_proyecto_socio_seq", allocationSize = 1)
  private Long id;

  /** SolicitudProyecto Id */
  @Column(name = "solicitud_proyecto_id", nullable = false)
  @NotNull
  private Long solicitudProyectoId;

  /** Rol socio */
  @ManyToOne
  @JoinColumn(name = "rol_socio_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOSOCIO_ROLSOCIO"))
  @NotNull
  private RolSocio rolSocio;

  /** EmpresaRef */
  @Column(name = "empresa_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String empresaRef;

  /** Mes Inicio */
  @Column(name = "mes_inicio", nullable = true)
  @Min(1)
  private Integer mesInicio;

  /** Mes Fin */
  @Column(name = "mes_fin", nullable = true)
  @Min(1)
  private Integer mesFin;

  /** Número de investigadores */
  @Column(name = "num_investigadores", nullable = true)
  @Min(1)
  private Integer numInvestigadores;

  /** Importe Solicitado */
  @Column(name = "importe_solicitado", nullable = true)
  private BigDecimal importeSolicitado;

  /** Importe Presupuestado */
  @Column(name = "importe_presupuestado", nullable = true)
  private BigDecimal importePresupuestado;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTOSOCIO_SOLICITUDPROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudProyecto solicitudProyecto = null;

  @OneToMany(mappedBy = "solicitudProyectoSocio")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudProyectoSocioEquipo> equipo = null;

  @OneToMany(mappedBy = "solicitudProyectoSocio")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudProyectoSocioPeriodoJustificacion> periodosJustificacion = null;

  @OneToMany(mappedBy = "solicitudProyectoSocio")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudProyectoSocioPeriodoPago> periodosPago = null;
}
