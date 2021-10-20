package org.crue.hercules.sgi.csp.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

  /** ProyectoPeriodoJustificacion Ids */
  @OneToMany(mappedBy = "estado")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ProyectoPeriodoJustificacion> proyectoJustificaciones = null;

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

}
