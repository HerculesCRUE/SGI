package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.time.Instant;

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
import javax.validation.Valid;

import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = RequerimientoJustificacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequerimientoJustificacion extends BaseEntity {

  protected static final String TABLE_NAME = "requerimiento_justificacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = RequerimientoJustificacion.SEQUENCE_NAME)
  @SequenceGenerator(name = RequerimientoJustificacion.SEQUENCE_NAME, sequenceName = RequerimientoJustificacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Proyecto proyecto SGE */
  @Column(name = "proyecto_proyecto_sge_id", nullable = false)
  private Long proyectoProyectoSgeId;

  /** Numero requerimiento */
  @Column(name = "num_requerimiento", nullable = false)
  private Integer numRequerimiento;

  @ManyToOne
  @JoinColumn(name = "tipo_requerimiento_id", nullable = false, foreignKey = @ForeignKey(name = "FK_REQUERIMIENTOJUSTIFICACION_TIPOREQUERIMIENTO"))
  @Valid
  @ActivableIsActivo(entityClass = TipoAmbitoGeografico.class, groups = {
      BaseEntity.Create.class, OnActualizarTipoRequerimiento.class })
  private TipoRequerimiento tipoRequerimiento;

  /** Proyecto periodo justificacion */
  @Column(name = "proyecto_periodo_justificacion_id", nullable = true)
  private Long proyectoPeriodoJustificacionId;

  /** Requerimiento previo */
  @Column(name = "requerimiento_previo_id", nullable = true)
  private Long requerimientoPrevioId;

  /** Fecha notificacion */
  @Column(name = "fecha_notificacion", nullable = true)
  private Instant fechaNotificacion;

  /** Fecha fin alegacion */
  @Column(name = "fecha_fin_alegacion", nullable = true)
  private Instant fechaFinAlegacion;

  /** Observaciones */
  @Column(name = "observaciones", nullable = true, length = DEFAULT_LONG_TEXT_LENGTH)
  private String observaciones;

  /** Importe aceptado costes directos */
  @Column(name = "importe_aceptado_cd", nullable = true)
  private BigDecimal importeAceptadoCd;

  /** Importe aceptado costes indirectos */
  @Column(name = "importe_aceptado_ci", nullable = true)
  private BigDecimal importeAceptadoCi;

  /** Importe rechazado costes directos */
  @Column(name = "importe_rechazado_cd", nullable = true)
  private BigDecimal importeRechazadoCd;

  /** Importe rechazado costes indirectos */
  @Column(name = "importe_rechazado_ci", nullable = true)
  private BigDecimal importeRechazadoCi;

  /** Importe reintegrar */
  @Column(name = "importe_reintegrar", nullable = true)
  private BigDecimal importeReintegrar;

  /** Importe reintegrar costes directos */
  @Column(name = "importe_reintegrar_cd", nullable = true)
  private BigDecimal importeReintegrarCd;

  /** Importe reintegrar costes indirectos */
  @Column(name = "importe_reintegrar_ci", nullable = true)
  private BigDecimal importeReintegrarCi;

  /** Intereses a reintegrar */
  @Column(name = "intereses_reintegrar", nullable = true)
  private BigDecimal interesesReintegrar;

  /** Importe aceptado */
  @Column(name = "importe_aceptado", nullable = true)
  private BigDecimal importeAceptado;

  /** Importe rechazado */
  @Column(name = "importe_rechazado", nullable = true)
  private BigDecimal importeRechazado;

  /** Subvencion justificada */
  @Column(name = "subvencion_justificada", nullable = true)
  private BigDecimal subvencionJustificada;

  /** Defecto subvencion */
  @Column(name = "defecto_subvencion", nullable = true)
  private BigDecimal defectoSubvencion;

  /** Anticipo justificado */
  @Column(name = "anticipo_justificado", nullable = true)
  private BigDecimal anticipoJustificado;

  /** Defecto anticipo */
  @Column(name = "defecto_anticipo", nullable = true)
  private BigDecimal defectoAnticipo;

  /** Recurso estimado */
  @Column(name = "recurso_estimado", nullable = true)
  private Boolean recursoEstimado;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_proyecto_sge_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REQUERIMIENTOJUSTIFICACION_PROYECTOPROYECTOSGE"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoProyectoSge proyectoProyectoSge = null;

  @ManyToOne
  @JoinColumn(name = "proyecto_periodo_justificacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REQUERIMIENTOJUSTIFICACION_PROYECTOPERIODOJUSTIFICACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoPeriodoJustificacion proyectoPeriodoJustificacion = null;

  @ManyToOne
  @JoinColumn(name = "requerimiento_previo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REQUERIMIENTOJUSTIFICACIONPREVIO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final RequerimientoJustificacion requerimientoPrevio = null;

  /**
   * Interfaz para marcar validaciones en la actualizacion del campo
   * TipoRequerimiento de la entidad.
   */
  public interface OnActualizarTipoRequerimiento {

  }
}
