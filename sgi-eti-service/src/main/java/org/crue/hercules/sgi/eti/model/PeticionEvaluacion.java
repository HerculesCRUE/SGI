package org.crue.hercules.sgi.eti.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PeticionEvaluacion
 */

@Entity
@Table(name = "peticion_evaluacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PeticionEvaluacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Estados de la Financiacion */
  public enum EstadoFinanciacion {
    /** Solicitado */
    SOLICITADO,
    /** Concedido */
    CONCEDIDO,
    /** Denegado */
    DENEGADO;
  }

  /** Tipo valor social */
  public enum TipoValorSocial {
    /** INVESTIGACION_FUNDAMENTAL */
    INVESTIGACION_FUNDAMENTAL,
    /** INVESTIGACION_PREVENCION */
    INVESTIGACION_PREVENCION,
    /** INVESTIGACION_EVALUACIÓN */
    INVESTIGACION_EVALUACION,
    /** INVESTIGACION_DESARROLLO */
    INVESTIGACION_DESARROLLO,
    /** INVESTIGACION_PROTECCION */
    INVESTIGACION_PROTECCION,
    /** INVESTIGACION_BIENESTAR */
    INVESTIGACION_BIENESTAR,
    /** INVESTIGACION_CONSERVACION */
    INVESTIGACION_CONSERVACION,
    /** ENSEÑANZA_SUPERIOR */
    ENSENIANZA_SUPERIOR,
    /** INVESTIGACION_JURIDICA */
    INVESTIGACION_JURIDICA,
    /** OTRA FINALIDAD */
    OTRA_FINALIDAD
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "peticion_evaluacion_seq")
  @SequenceGenerator(name = "peticion_evaluacion_seq", sequenceName = "peticion_evaluacion_seq", allocationSize = 1)
  private Long id;

  /** Referencia solicitud convocatoria */
  @Column(name = "solicitud_convocatoria_ref", length = 250)
  private String solicitudConvocatoriaRef;

  /** Código */
  @Column(name = "codigo", length = 250, nullable = false)
  private String codigo;

  /** Título */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "peticion_evaluacion_titulo", joinColumns = @JoinColumn(name = "peticion_evaluacion_id"))
  @Valid
  private Set<PeticionEvaluacionTitulo> titulo = new HashSet<>();

  /** Tipo Actividad */
  @ManyToOne
  @JoinColumn(name = "tipo_actividad_id", foreignKey = @ForeignKey(name = "FK_PETICIONEVALUACION_TIPOACTIVIDAD"))
  private TipoActividad tipoActividad;

  /** Tipo Investigacion Tutelada */
  @ManyToOne
  @JoinColumn(name = "tipo_investigacion_tutelada_id", foreignKey = @ForeignKey(name = "FK_PETICIONEVALUACION_TIPOINVESTIGACIONTUTELADA"))
  private TipoInvestigacionTutelada tipoInvestigacionTutelada;

  /** Existe financiacion */
  @Column(name = "existe_financiacion", nullable = false)
  private Boolean existeFinanciacion;

   /** Fuente financiacion */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "peticion_evaluacion_fuente_financiacion", joinColumns = @JoinColumn(name = "peticion_evaluacion_id"))
  @Valid
  private Set<PeticionEvaluacionFuenteFinanciacion> fuenteFinanciacion = new HashSet<>();

  /** Estado Financiación */
  @Column(name = "estado_financiacion", length = 50)
  @Enumerated(EnumType.STRING)
  private EstadoFinanciacion estadoFinanciacion;

  /** Importe Financiación */
  @Column(name = "importe_financiacion")
  @Min(0)
  private BigDecimal importeFinanciacion;

  /** Fecha Inicio. */
  @Column(name = "fecha_inicio")
  private Instant fechaInicio;

  /** Fecha Fin. */
  @Column(name = "fecha_fin")
  private Instant fechaFin;

  /** Resumen */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "peticion_evaluacion_resumen", joinColumns = @JoinColumn(name = "peticion_evaluacion_id"))
  @Valid
  private Set<PeticionEvaluacionResumen> resumen = new HashSet<>();

  /** Valor social */
  @Column(name = "valor_social", length = 2000)
  @Enumerated(EnumType.STRING)
  private TipoValorSocial valorSocial;

  /** Otro valor social */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "peticion_evaluacion_otrovalorsocial", joinColumns = @JoinColumn(name = "peticion_evaluacion_id"))
  @Valid
  private Set<PeticionEvaluacionOtroValorSocial> otroValorSocial = new HashSet<>();

  /** Objetivos */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "peticion_evaluacion_objetivos", joinColumns = @JoinColumn(name = "peticion_evaluacion_id"))
  @Valid
  private Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();

  /** Diseño metodológico */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "peticion_evaluacion_dismetodologico", joinColumns = @JoinColumn(name = "peticion_evaluacion_id"))
  @Valid
  private Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();

  /** Tiene fondos propios */
  @Column(name = "tiene_fondos_propios", columnDefinition = "boolean default false")
  private Boolean tieneFondosPropios;

  /** Referencia usuario */
  @Column(name = "persona_ref", length = 250, nullable = false)
  private String personaRef;

  /** Referencia al Checklist asociado */
  @Column(name = "checklistId")
  private Long checklistId;

  /** Referencia tutor */
  @Column(name = "tutor_ref", length = 250, nullable = true)
  private String tutorRef;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  // Relations mapping, only for JPA metamodel generation
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "checklistId", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PETICIONEVALUACION_CHECKLIST"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @EqualsAndHashCode.Exclude
  private final Checklist checklist = null;
}