package org.crue.hercules.sgi.pii.model;

import java.math.BigDecimal;
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

import org.crue.hercules.sgi.pii.model.Reparto.OnActualizar;
import org.crue.hercules.sgi.pii.model.Reparto.OnEjecutar;
import org.crue.hercules.sgi.pii.validation.RepartoIsEjecutable;
import org.crue.hercules.sgi.pii.validation.RepartoIsUpdatable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reparto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RepartoIsUpdatable(groups = { OnActualizar.class })
@RepartoIsEjecutable(groups = { OnEjecutar.class })
public class Reparto extends BaseEntity {

  public static final int ESTADO_LENGTH = 25;

  public enum Estado {
    /** Pendiente de ejecutar */
    PENDIENTE_EJECUTAR,
    /** Ejecutado */
    EJECUTADO
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reparto_seq")
  @SequenceGenerator(name = "reparto_seq", sequenceName = "reparto_seq", allocationSize = 1)
  private Long id;

  /** Invencion Id. */
  @Column(name = "invencion_id", nullable = false)
  private Long invencionId;

  /** Fecha. */
  @Column(name = "fecha", nullable = false)
  private Instant fecha;

  /** Importe Universidad. */
  @Column(name = "importe_universidad", nullable = false)
  private BigDecimal importeUniversidad;

  /** Importe Equipo Inventor. */
  @Column(name = "importe_equipo_inventor", nullable = true)
  private BigDecimal importeEquipoInventor;

  /** Estado del reparto. */
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", length = ESTADO_LENGTH, nullable = false)
  private Estado estado;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "invencion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_REPARTO_INVENCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Invencion invencion = null;

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }

  /**
   * Interfaz para marcar validaciones en la ejecucion de la entidad.
   */
  public interface OnEjecutar {
  }
}
