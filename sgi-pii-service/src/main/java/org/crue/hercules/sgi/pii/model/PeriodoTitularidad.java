package org.crue.hercules.sgi.pii.model;

import java.time.Instant;
import java.util.List;

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

import org.crue.hercules.sgi.pii.model.PeriodoTitularidad.OnActualizar;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidad.OnEliminar;
import org.crue.hercules.sgi.pii.validation.ValidPeriodoTitularidad;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = PeriodoTitularidad.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidPeriodoTitularidad(groups = { OnActualizar.class, OnEliminar.class })
public class PeriodoTitularidad extends BaseEntity {

  public static final String TABLE_NAME = "periodo_titularidad";
  private static final String SEQ_SUFFIX = "_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_NAME + SEQ_SUFFIX)
  @SequenceGenerator(name = TABLE_NAME + SEQ_SUFFIX, sequenceName = TABLE_NAME + SEQ_SUFFIX, allocationSize = 1)
  private Long id;

  @Column(name = "fecha_inicio", nullable = false)
  private Instant fechaInicio;

  @Column(name = "fecha_fin", nullable = true)
  private Instant fechaFin;

  @Column(name = "invencion_id", nullable = false)
  private Long invencionId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "invencion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PERIODOTITULARIDAD_INVENCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Invencion invencion = null;

  @OneToMany(mappedBy = "periodoTitularidad")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<PeriodoTitularidadTitular> titulares = null;

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

  /**
   * Interfaz para marcar validaciones en la eliminacion de la entidad.
   */
  public interface OnEliminar {
  }

}
