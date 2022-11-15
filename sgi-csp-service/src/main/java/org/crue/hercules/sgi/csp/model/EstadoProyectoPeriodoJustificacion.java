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
@Table(name = "estado_proyecto_periodo_justificacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoProyectoPeriodoJustificacion extends BaseEntity {

  /** Enumerado Tipo de Seguimiento */
  public enum TipoEstadoPeriodoJustificacion {
    /** Pendiente */
    PENDIENTE,
    /** Elaboracion */
    ELABORACION,
    /** Entregada */
    ENTREGADA,
    /** Subsanación */
    SUBSANACION,
    /** Cerrada */
    CERRADA;
  }

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_periodo_justificacion_seq")
  @SequenceGenerator(name = "estado_periodo_justificacion_seq", sequenceName = "estado_periodo_justificacion_seq", allocationSize = 1)
  private Long id;

  /** Proyecto periodo justificacion id */
  @Column(name = "proyecto_periodo_justificacion_id", nullable = false)
  private Long proyectoPeriodoJustificacionId;

  /** Tipo Estado */
  @Column(name = "estado", length = 10)
  @Enumerated(EnumType.STRING)
  private TipoEstadoPeriodoJustificacion estado;

  /** Fecha Estado */
  @Column(name = "fecha_estado", nullable = true)
  private Instant fechaEstado;

  /** Comentario */
  @Column(name = "comentario", nullable = true)
  @Size(max = 2000)
  private String comentario;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_periodo_justificacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ESTADOPROYECTOPERIODOJUSTIFICACION_PROYECTOPERIODOJUSTIFICACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoPeriodoJustificacion proyectoPeriodoJustificacion = null;

}
