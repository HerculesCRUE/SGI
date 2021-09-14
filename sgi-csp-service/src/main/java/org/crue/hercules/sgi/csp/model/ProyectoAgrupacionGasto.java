package org.crue.hercules.sgi.csp.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proyecto_agrupacion_gasto", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "nombre", "proyecto_id" }, name = "UK_PROYECTOAGRUPACIONGASTO_NOMBRE") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoAgrupacionGasto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_agrupacion_gasto_seq")
  @SequenceGenerator(name = "proyecto_agrupacion_gasto_seq", sequenceName = "proyecto_agrupacion_gasto_seq", allocationSize = 1)
  private Long id;

  /** Proyecto */
  @Column(name = "proyecto_id", nullable = false)
  private Long proyectoId;

  /** Nombre */
  @Column(name = "nombre", nullable = true)
  private String nombre;

  /* Conceptos de gasto */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "proyectoAgrupacionGasto")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<AgrupacionGastoConcepto> conceptos = null;

  @ManyToOne
  @JoinColumn(name = "proyecto_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOAGRUPACIONGASTO_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;

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
