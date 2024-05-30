package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
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
@Table(name = "solicitud_proyecto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudProyecto extends BaseEntity {

  /**
   * Enumerado tipo estado de las solicitudes.
   *
   */
  public enum TipoPresupuesto {
    /** Global */
    GLOBAL,
    /** Mixto */
    MIXTO,
    /** Por Entidad */
    POR_ENTIDAD,
  }

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id de la Solicitud */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /** Acrónimo */
  @Column(name = "acronimo", length = 50, nullable = true)
  @Size(max = 50)
  private String acronimo;

  /** Código externo */
  @Column(name = "codigo_externo", length = 250, nullable = true)
  @Size(max = 50)
  private String codExterno;

  /** Duracion */
  @Column(name = "duracion", nullable = true)
  @Min(1)
  private Integer duracion;

  /** Colaborativo */
  @Column(name = "colaborativo", nullable = true)
  private Boolean colaborativo;

  /** Coordinador externo */
  @Column(name = "rol_universidad", nullable = true)
  private Long rolUniversidadId;

  /** Coordinado */
  @Column(name = "coordinado", nullable = true)
  private Boolean coordinado;

  /** Objetivos */
  @Column(name = "objetivos", length = 2000, nullable = true)
  @Size(max = 2000)
  private String objetivos;

  /** Intereses */
  @Column(name = "intereses", length = 2000, nullable = true)
  @Size(max = 2000)
  private String intereses;

  /** Resultados previstos */
  @Column(name = "resultados_previstos", length = 2000, nullable = true)
  @Size(max = 2000)
  private String resultadosPrevistos;

  /** Área temática */
  @ManyToOne
  @JoinColumn(name = "area_tematica_id", nullable = true, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTO_AREATEMATICA"))
  private AreaTematica areaTematica;

  /** Referencia a las respuesta del Checklist de ETICA */
  @Column(name = "checklist_ref", nullable = true)
  private String checklistRef;

  /** Referencia a la petición de evaluación de ETICA */
  @Column(name = "peticion_evaluacion_ref", nullable = true)
  private String peticionEvaluacionRef;

  /** Tipo presupuesto */
  @Column(name = "tipo_presupuesto", length = 50, nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoPresupuesto tipoPresupuesto;

  /** Importe Solicitado */
  @Column(name = "importe_solicitado", nullable = true)
  private BigDecimal importeSolicitado;

  /** Importe solicitado Costes Indirectos */
  @Column(name = "importe_solicitado_costes_indirectos", nullable = true)
  private BigDecimal importeSolicitadoCostesIndirectos;

  /** Importe presupuestado */
  @Column(name = "importe_presupuestado", nullable = true)
  private BigDecimal importePresupuestado;

  /** Importe presupuestado Costes Indirectos */
  @Column(name = "importe_presupuestado_costes_indirectos", nullable = true)
  private BigDecimal importePresupuestadoCostesIndirectos;

  /** Importe Solicitado socios */
  @Column(name = "importe_solicitado_socios", nullable = true)
  private BigDecimal importeSolicitadoSocios;

  /** Importe presupuestado socios */
  @Column(name = "importe_presupuestado_socios", nullable = true)
  private BigDecimal importePresupuestadoSocios;

  /** Total Importe Solicitado */
  @Column(name = "total_importe_solicitado", nullable = true)
  private BigDecimal totalImporteSolicitado;

  /** Total Importe presupuestado */
  @Column(name = "total_importe_presupuestado", nullable = true)
  private BigDecimal totalImportePresupuestado;

  // Relation mappings for JPA metamodel generation only
  @OneToOne
  @JoinColumn(name = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTO_SOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Solicitud solicitud = null;

  @ManyToOne
  @JoinColumn(name = "rol_universidad", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDPROYECTO_ROLSOCIO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RolSocio rolUniversidad = null;

  @OneToMany(mappedBy = "solicitudProyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudProyectoEquipo> equipo = null;

  @OneToMany(mappedBy = "solicitudProyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudProyectoEntidadFinanciadoraAjena> entidadesFinanciadorasAjenas = null;

  @OneToMany(mappedBy = "solicitudProyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudProyectoPresupuesto> presupuesto = null;
}
