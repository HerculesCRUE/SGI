package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;

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
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "anualidad_gasto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnualidadGasto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anualidad_gasto_seq")
  @SequenceGenerator(name = "anualidad_gasto_seq", sequenceName = "anualidad_gasto_seq", allocationSize = 1)
  private Long id;

  /** Proyecto Anualidad Id */
  @Column(name = "proyecto_anualidad_id", nullable = false)
  @NotNull
  private Long proyectoAnualidadId;

  /** Concepto gasto */
  @ManyToOne
  @JoinColumn(name = "concepto_gasto_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ANUALIDADGASTO_CONCEPTGASTO"))
  private ConceptoGasto conceptoGasto;

  /** Referencia código económico */
  @Column(name = "codigo_economico_ref", nullable = true)
  private String codigoEconomicoRef;

  /** ProyectoPartida */
  @ManyToOne
  @JoinColumn(name = "proyecto_partida_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ANUALIDADGASTO_PROYECTOPARTIDA"))
  private ProyectoPartida proyectoPartida;

  /** Importe presupuesto */
  @Column(name = "importe_presupuesto", nullable = true)
  private BigDecimal importePresupuesto;

  /** Importe concedido */
  @Column(name = "importe_concedido", nullable = true)
  private BigDecimal importeConcedido;

  /** Referencia proyecto SGE */
  @Column(name = "proyecto_sge_ref", nullable = true)
  private String proyectoSgeRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_anualidad_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ANUALIDADGASTO_PROYECTOANUALIDAD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoAnualidad proyectoAnualidad = null;

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