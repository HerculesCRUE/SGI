package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.enums.ClasificacionCVN;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proyecto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Tipo de horas anuales. */
  public enum TipoHorasAnuales {
    /** Valor fijo */
    FIJO,
    /** Reales (TS) */
    REAL,
    /** Por categoría */
    CATEGORIA;
  }

  /** Causas de exención. */
  public enum CausaExencion {
    /** Sujeto y Exento */
    SUJETO_EXENTO,
    /** No sujeto a articulos 7, 14 y otros */
    NO_SUJETO,
    /** No sujeto por reglas de localización. Sin derecho a deducción */
    NO_SUJETO_SIN_DEDUCCION,
    /** No sujeto por reglas de localización. Con derecho a deducción */
    NO_SUJETO_CON_DEDUCCION;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_seq")
  @SequenceGenerator(name = "proyecto_seq", sequenceName = "proyecto_seq", allocationSize = 1)
  private Long id;

  /** Convocatoria Id */
  @Column(name = "convocatoria_id", nullable = true)
  private Long convocatoriaId;

  /** Solicitud Id */
  @Column(name = "solicitud_id", nullable = true)
  private Long solicitudId;

  /** Estado proyecto */
  @ManyToOne
  @JoinColumn(name = "estado_proyecto_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTO_ESTADOPROYECTO"))
  private EstadoProyecto estado;

  /** Titulo */
  @Column(name = "titulo", length = 250, nullable = false)
  @Size(max = 250)
  @NotNull
  private String titulo;

  /** Acrónimo */
  @Column(name = "acronimo", length = 50, nullable = true)
  @Size(max = 50)
  private String acronimo;

  /** Codigo externo */
  @Column(name = "codigo_externo", length = 50, nullable = true)
  @Size(max = 50)
  private String codigoExterno;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private Instant fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin", nullable = false)
  @NotNull
  private Instant fechaFin;

  /** Fecha Fin definitiva */
  @Column(name = "fecha_fin_definitiva", nullable = true)
  private Instant fechaFinDefinitiva;

  /** Unidad gestion ref */
  @Column(name = "unidad_gestion_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String unidadGestionRef;

  /** Modelo ejecución. */
  @ManyToOne
  @JoinColumn(name = "modelo_ejecucion_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_MODELOEJECUCION"))
  @NotNull
  private ModeloEjecucion modeloEjecucion;

  /** Tipo Finalidad */
  @ManyToOne
  @JoinColumn(name = "tipo_finalidad_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTO_FINALIDAD"))
  private TipoFinalidad finalidad;

  /** Codigo externo */
  @Column(name = "convocatoria_externa", length = 50, nullable = true)
  @Size(max = 50)
  private String convocatoriaExterna;

  /** Ambito Geografico */
  @ManyToOne
  @JoinColumn(name = "tipo_ambito_geografico_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTO_AMBITOGEOGRAFICO"))
  private TipoAmbitoGeografico ambitoGeografico;

  /** Confidencial */
  @Column(name = "confidencial", nullable = true)
  private Boolean confidencial;

  /** Clasificacion CVN */
  @Column(name = "clasificacion_cvn", length = 50, nullable = true)
  @Enumerated(EnumType.STRING)
  private ClasificacionCVN clasificacionCVN;

  /** Coordinado */
  @Column(name = "coordinado", nullable = true)
  private Boolean coordinado;

  /** Colaborativo */
  @Column(name = "colaborativo", nullable = true)
  private Boolean colaborativo;

  /** Coordinador Externo */
  @Column(name = "coordinador_externo", nullable = true)
  private Boolean coordinadorExterno;

  /** TimeSheet */
  @Column(name = "timesheet", nullable = true)
  private Boolean timesheet;

  /** Permite paquetes de trabajo */
  @Column(name = "paquetes_trabajo", nullable = true)
  private Boolean permitePaquetesTrabajo;

  /** Coste hora */
  @Column(name = "coste_hora", nullable = true)
  private Boolean costeHora;

  /** Tipo horas anuales */
  @Column(name = "tipo_horas_anuales", length = 50, nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoHorasAnuales tipoHorasAnuales;

  /** Iva */
  @ManyToOne
  @JoinColumn(name = "proyecto_iva_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTO_PROYECTOIVA"))
  private ProyectoIVA iva;

  /** Causa exención */
  @Column(name = "causa_exencion", length = 50, nullable = true)
  @Enumerated(EnumType.STRING)
  private CausaExencion causaExencion;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  @Size(max = 2000)
  private String observaciones;

  /** Anualidades */
  @Column(name = "anualidades", nullable = true)
  private Boolean anualidades;

  /** Importe Presupuesto */
  @Column(name = "importe_presupuesto", nullable = true)
  private BigDecimal importePresupuesto;

  /** Importe presupuesto Costes Indirectos */
  @Column(name = "importe_presupuesto_costes_indirectos", nullable = true)
  private BigDecimal importePresupuestoCostesIndirectos;

  /** Importe concedido */
  @Column(name = "importe_concedido", nullable = true)
  private BigDecimal importeConcedido;

  /** Importe concedido Costes Indirectos */
  @Column(name = "importe_concedido_costes_indirectos", nullable = true)
  private BigDecimal importeConcedidoCostesIndirectos;

  /** Importe presupuesta socios */
  @Column(name = "importe_presupuesto_socios", nullable = true)
  private BigDecimal importePresupuestoSocios;

  /** Importe concedido socios */
  @Column(name = "importe_concedido_socios", nullable = true)
  private BigDecimal importeConcedidoSocios;

  /** Total importe presupuesto */
  @Column(name = "total_importe_presupuesto", nullable = true)
  private BigDecimal totalImportePresupuesto;

  /** Total importe concedido */
  @Column(name = "total_importe_concedido", nullable = true)
  private BigDecimal totalImporteConcedido;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull
  private Boolean activo;

  // Relation mappings for JPA metamodel generation only
  @OneToOne(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ContextoProyecto contexto = null;

  @ManyToOne
  @JoinColumn(name = "convocatoria_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_CONVOCATORIA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Convocatoria convocatoria = null;

  @ManyToOne
  @JoinColumn(name = "solicitud_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTO_SOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Solicitud solicitud = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoDocumento> documentos = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoEntidadConvocante> entidadesConvocantes = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoEntidadFinanciadora> entidadesFinanciadoras = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoEntidadGestora> entidadesGestoras = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<EstadoProyecto> estados = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoEquipo> equipo = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoFase> fases = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoHito> hitos = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoPaqueteTrabajo> paquetesTrabajo = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoPeriodoSeguimiento> periodosSeguimiento = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoProrroga> prorrogas = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoSocio> socios = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoIVA> ivas = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoProyectoSge> identificadoresSge = null;

  @OneToMany(mappedBy = "proyecto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoAnualidad> proyectosAnualidad = null;

}