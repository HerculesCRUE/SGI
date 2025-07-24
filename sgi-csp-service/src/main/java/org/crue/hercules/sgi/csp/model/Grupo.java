package org.crue.hercules.sgi.csp.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
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
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.validation.UniqueCodigoGrupoActivo;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = Grupo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ActivableIsActivo(entityClass = Grupo.class, groups = { BaseEntity.Update.class })
@UniqueCodigoGrupoActivo(groups = { BaseEntity.Update.class, BaseEntity.Create.class,
    BaseActivableEntity.OnActivar.class })
public class Grupo extends BaseActivableEntity {

  protected static final String TABLE_NAME = "grupo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int PROYECTO_SGE_REF_LENGTH = 50;
  public static final int DEPARTAMENTO_ORIGEN_REF_LENGTH = 50;
  public static final int CODIGO_LENGTH = 50;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = Grupo.SEQUENCE_NAME)
  @SequenceGenerator(name = Grupo.SEQUENCE_NAME, sequenceName = Grupo.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "grupo_nombre", joinColumns = @JoinColumn(name = "grupo_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<GrupoNombre> nombre = new HashSet<>();

  /** Fecha inicio */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private Instant fechaInicio;

  /** Fecha fin */
  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  /** Proyecto SGE */
  @Column(name = "proyecto_sge_ref", length = Grupo.PROYECTO_SGE_REF_LENGTH, nullable = true)
  @Size(max = Grupo.PROYECTO_SGE_REF_LENGTH)
  private String proyectoSgeRef;

  /** Departamento origen */
  @Column(name = "departamento_origen_ref", length = Grupo.DEPARTAMENTO_ORIGEN_REF_LENGTH, nullable = true)
  @Size(max = Grupo.DEPARTAMENTO_ORIGEN_REF_LENGTH)
  private String departamentoOrigenRef;

  /** Solicitud */
  @Column(name = "solicitud_id", nullable = true)
  private Long solicitudId;

  /** Codigo externo */
  @Column(name = "codigo", length = Grupo.CODIGO_LENGTH, nullable = false)
  @Size(max = Grupo.CODIGO_LENGTH)
  private String codigo;

  /** Tipo actual */
  @OneToOne()
  @JoinColumn(name = "grupo_tipo_id", nullable = true, foreignKey = @ForeignKey(name = "FK_GRUPO_GRUPOTIPO"))
  private GrupoTipo tipo;

  /** Especial investigacion actual */
  @OneToOne()
  @JoinColumn(name = "grupo_especial_investigacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_GRUPO_GRUPOESPECIALINVESTIGACION"))
  private GrupoEspecialInvestigacion especialInvestigacion;

  /** Resumen */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "grupo_resumen", joinColumns = @JoinColumn(name = "grupo_id"))
  @Valid
  @Builder.Default
  private Set<GrupoResumen> resumen = new HashSet<>();

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GRUPO_SOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Solicitud solicitud = null;

  @OneToMany(mappedBy = "grupo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<GrupoEquipo> miembrosEquipo = null;

  @OneToMany(mappedBy = "grupo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SolicitudGrupo> solicitudesGrupo = null;

  @OneToMany(mappedBy = "grupo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<GrupoPersonaAutorizada> personasAutorizadas = null;

  @OneToMany(mappedBy = "grupo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<GrupoPalabraClave> palabrasClave = null;

  @OneToMany(mappedBy = "grupo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<GrupoLineaInvestigacion> lineasInvestigacion = null;

  @OneToMany(mappedBy = "grupo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<GrupoResponsableEconomico> responsableEconomicos = null;

  @OneToMany(mappedBy = "grupo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<GrupoEnlace> enlaces = null;

  @OneToMany(mappedBy = "grupo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<GrupoEquipoInstrumental> equipoInstrumental = null;

}
