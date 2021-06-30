package org.crue.hercules.sgi.csp.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
@Table(name = "convocatoria")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Convocatoria extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Estados de la convocatoria */
  public enum Estado {
    /** Borrador */
    BORRADOR,
    /** Registrada */
    REGISTRADA;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_seq")
  @SequenceGenerator(name = "convocatoria_seq", sequenceName = "convocatoria_seq", allocationSize = 1)
  private Long id;

  /** Unidad Gestion */
  @Column(name = "unidad_gestion_ref", nullable = false)
  @NotBlank
  private String unidadGestionRef;

  /** Modelo Ejecucion */
  @ManyToOne
  @JoinColumn(name = "modelo_ejecucion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIA_MODELOEJECUCION"))
  private ModeloEjecucion modeloEjecucion;

  /** Codigo */
  @Column(name = "codigo", length = 50, nullable = true)
  @Size(max = 50)
  private String codigo;

  /** Fecha Publicación */
  @Column(name = "fecha_publicacion", nullable = false)
  @NotNull
  private Instant fechaPublicacion;

  /** Fecha Provisional */
  @Column(name = "fecha_provisional", nullable = true)
  private Instant fechaProvisional;

  /** Fecha Concesión */
  @Column(name = "fecha_concesion", nullable = true)
  private Instant fechaConcesion;

  /** Titulo */
  @Column(name = "titulo", length = 250, nullable = false)
  @NotBlank
  @Size(max = 250)
  private String titulo;

  /** Objeto */
  @Column(name = "objeto", length = 2000, nullable = true)
  @Size(max = 2000)
  private String objeto;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  @Size(max = 2000)
  private String observaciones;

  /** Tipo Finalidad */
  @ManyToOne
  @JoinColumn(name = "tipo_finalidad_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIA_FINALIDAD"))
  private TipoFinalidad finalidad;

  /** Regimen Concurrencia */
  @ManyToOne
  @JoinColumn(name = "tipo_regimen_concurrencia_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIA_REGIMENCONCURRENCIA"))
  private TipoRegimenConcurrencia regimenConcurrencia;

  /** Colaborativos */
  @Column(name = "colaborativos", nullable = true)
  private Boolean colaborativos;

  /** Estado */
  @Column(name = "estado", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Estado estado;

  /** Duracion */
  @Column(name = "duracion", nullable = true)
  @Min(1)
  @Max(9999)
  @Digits(fraction = 0, integer = 4)
  private Integer duracion;

  /** Ambito Geografico */
  @ManyToOne
  @JoinColumn(name = "tipo_ambito_geografico_id", nullable = true, foreignKey = @ForeignKey(name = "FK_CONVOCATORIA_AMBITOGEOGRAFICO"))
  private TipoAmbitoGeografico ambitoGeografico;

  /** Clasificacion CVN */
  @Column(name = "clasificacion_cvn", length = 50, nullable = true)
  @Enumerated(EnumType.STRING)
  private ClasificacionCVN clasificacionCVN;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  // Relations mapping, only for JPA metamodel generation
  @OneToOne(mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConfiguracionSolicitud configuracionSolicitud = null;

  @OneToOne(mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RequisitoEquipo requisitoEquipo = null;

  @OneToOne(mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RequisitoIP requisitoIP = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaAreaTematica> areasTematicas = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaConceptoGasto> conceptosGasto = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaDocumento> documentos = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaEnlace> enlaces = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaEntidadConvocante> entidadesConvocantes = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaEntidadFinanciadora> entidadesFinanciadoras = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaEntidadGestora> entidadesGestoras = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaFase> fases = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaHito> hitos = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaPeriodoJustificacion> periodosJustificacion = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ConvocatoriaPeriodoSeguimientoCientifico> periodosSeguimiento = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Proyecto> proyectos = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "convocatoria")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Solicitud> solicitudes = null;
}