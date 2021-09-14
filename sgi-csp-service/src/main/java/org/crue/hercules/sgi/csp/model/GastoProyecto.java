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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gasto_proyecto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GastoProyecto extends BaseEntity {
  public static final int GASTO_REF_LENGTH = 50;
  public static final int OBSERVACIONES_LENGTH = 2000;
  public static final int IMPORTE_INSCRIPCION_MIN = 0;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gasto_proyecto_seq")
  @SequenceGenerator(name = "gasto_proyecto_seq", sequenceName = "gasto_proyecto_seq", allocationSize = 1)
  @Null(groups = { OnCrear.class })
  @NotNull(groups = { OnActualizar.class })
  private Long id;

  /** Proyecto Id */
  @Column(name = "proyecto_id", nullable = false)
  @NotNull(groups = { OnCrear.class, OnActualizar.class })
  private Long proyectoId;

  /** Gasto ref */
  @Column(name = "gasto_ref", length = GASTO_REF_LENGTH, nullable = false)
  @Size(max = GASTO_REF_LENGTH, groups = { OnCrear.class, OnActualizar.class })
  @NotBlank(groups = { OnCrear.class, OnActualizar.class })
  private String gastoRef;

  /** Concepto gasto */
  @ManyToOne
  @JoinColumn(name = "concepto_gasto_id", nullable = true, foreignKey = @ForeignKey(name = "FK_GASTOPROYECTO_CONCEPTOGASTO"))
  private ConceptoGasto conceptoGasto;

  /** Estado gasto */
  @ManyToOne
  @JoinColumn(name = "estado_gasto_proyecto_id", nullable = true, foreignKey = @ForeignKey(name = "FK_GASTOPROYECTO_ESTADOGASTOPROYECTO"))
  private EstadoGastoProyecto estado;

  /** Fecha congreso */
  @Column(name = "fecha_congreso", nullable = false)
  @NotNull(groups = { OnCrear.class, OnActualizar.class })
  private Instant fechaCongreso;

  /** Importe inscripcion */
  @Column(name = "importe_inscripcion", nullable = true)
  @Min(value = IMPORTE_INSCRIPCION_MIN, groups = { OnCrear.class, OnActualizar.class })
  private BigDecimal importeInscripcion;

  /** Observaciones */
  @Column(name = "observaciones", length = OBSERVACIONES_LENGTH, nullable = true)
  @Size(max = OBSERVACIONES_LENGTH, groups = { OnCrear.class, OnActualizar.class })
  private String observaciones;

  /**
   * Interfaz para marcar validaciones en la creación de la entidad.
   */
  public interface OnCrear {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }

}