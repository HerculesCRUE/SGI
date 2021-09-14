package org.crue.hercules.sgi.csp.model;

import java.time.Instant;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.csp.enums.TipoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion.OnActualizar;
import org.crue.hercules.sgi.csp.validation.OrderFechasProyectoPeriodoJustificacion;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto_periodo_justificacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@OrderFechasProyectoPeriodoJustificacion(groups = { OnActualizar.class })
public class ProyectoPeriodoJustificacion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_periodo_justificacion_seq")
  @SequenceGenerator(name = "proyecto_periodo_justificacion_seq", sequenceName = "proyecto_periodo_justificacion_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  @NotNull
  private Long proyectoId;

  /** Num periodo */
  @Column(name = "num_periodo", nullable = true)
  private Integer numPeriodo;

  /** Fecha Inicio */
  @Column(name = "fecha_inicio", nullable = false)
  private Instant fechaInicio;

  /** Fecha Fin */
  @Column(name = "fecha_fin", nullable = false)
  private Instant fechaFin;

  /** Fecha inicio presentacion */
  @Column(name = "fecha_inicio_presentacion", nullable = true)
  private Instant fechaInicioPresentacion;

  /** Fecha fin presentacion */
  @Column(name = "fecha_fin_presentacion", nullable = true)
  private Instant fechaFinPresentacion;

  /** Obervaciones */
  @Column(name = "observaciones", nullable = true)
  @Size(max = 2000)
  private String observaciones;

  /** Tipo justificacion */
  @Column(name = "tipo_justificacion", length = 10)
  @Enumerated(EnumType.STRING)
  private TipoJustificacion tipoJustificacion;

  /** Estado */
  @ManyToOne
  @JoinColumn(name = "estado")
  private EstadoProyectoPeriodoJustificacion estado;

  /** Convocatoria periodo Justificacion Id */
  @Column(name = "convocatoria_periodo_justificacion_id")
  private Long convocatoriaPeriodoJustificacionId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOPERIODOJUSTIFICACION_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }
}
