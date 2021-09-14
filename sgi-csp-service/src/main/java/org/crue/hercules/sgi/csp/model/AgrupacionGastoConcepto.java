package org.crue.hercules.sgi.csp.model;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "agrupacion_gasto_concepto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgrupacionGastoConcepto extends BaseEntity {
  public static final int CONCEPTO_GASTO_LENGTH = 50;
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agrupacion_gasto_concepto_seq")
  @SequenceGenerator(name = "agrupacion_gasto_concepto_seq", sequenceName = "agrupacion_gasto_concepto_seq", allocationSize = 1)
  private Long id;

  /** Agrupacion Gasto */
  @Column(name = "agrupacion_gasto_id", nullable = false)
  private Long agrupacionGastoId;

  /** Concepto Gasto */
  @ManyToOne
  @JoinColumn(name = "concepto_gasto_id", foreignKey = @ForeignKey(name = "FK_AGRUPACIONCONGASTOCONCEPTO_CONCEPTOGASTO"))
  private ConceptoGasto conceptoGasto;

  @ManyToOne
  @JoinColumn(name = "agrupacion_gasto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_AGRUPACIONGASTOCONCEPTO_AGRUPACIONGASTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoAgrupacionGasto proyectoAgrupacionGasto = null;

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
