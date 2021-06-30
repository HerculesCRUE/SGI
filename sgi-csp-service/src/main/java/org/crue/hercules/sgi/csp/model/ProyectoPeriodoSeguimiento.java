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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto_periodo_seguimiento")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoPeriodoSeguimiento extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_periodo_seguimiento_seq")
  @SequenceGenerator(name = "proyecto_periodo_seguimiento_seq", sequenceName = "proyecto_periodo_seguimiento_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  @NotNull
  private Long proyectoId;

  /** Fecha inicio. */
  @Column(name = "fecha_inicio", nullable = false)
  @NotNull
  private Instant fechaInicio;

  /** Fecha fin. */
  @Column(name = "fecha_fin", nullable = false)
  @NotNull
  private Instant fechaFin;

  /** Número periodo. */
  @Column(name = "num_periodo", nullable = false)
  @Min(1)
  @NotNull
  private Integer numPeriodo;

  /** Fecha inicio presentación. */
  @Column(name = "fecha_inicio_presentacion", nullable = true)
  private Instant fechaInicioPresentacion;

  /** Fecha fin. */
  @Column(name = "fecha_fin_presentacion", nullable = true)
  private Instant fechaFinPresentacion;

  /** Tipo Seguimiento */
  @Column(name = "tipo_seguimiento", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoSeguimiento tipoSeguimiento;

  /** Observaciones */
  @Column(name = "observaciones", length = 2000, nullable = true)
  private String observaciones;

  /** Identificador de la convocatoria periodo de seguimiento */
  @Column(name = "convocatoria_periodo_seguimiento_id", nullable = true)
  private Long convocatoriaPeriodoSeguimientoId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOPERIODOSEGUIMIENTO_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;
}