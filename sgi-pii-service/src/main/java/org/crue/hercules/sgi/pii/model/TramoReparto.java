package org.crue.hercules.sgi.pii.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.pii.model.TramoReparto.OnActivar;
import org.crue.hercules.sgi.pii.model.TramoReparto.OnActualizar;
import org.crue.hercules.sgi.pii.model.TramoReparto.OnCrear;
import org.crue.hercules.sgi.pii.validation.DesdeLowerThanHastaTramoReparto;
import org.crue.hercules.sgi.pii.validation.DistributionPercentTramoReparto;
import org.crue.hercules.sgi.pii.validation.NoOverlappedTramoRepartoActivo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tramo_reparto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@NoOverlappedTramoRepartoActivo(groups = { OnActualizar.class, OnActivar.class, OnCrear.class })
@DistributionPercentTramoReparto(groups = { OnActualizar.class, OnCrear.class })
@DesdeLowerThanHastaTramoReparto(groups = { OnActualizar.class, OnCrear.class })
public class TramoReparto extends BaseActivableEntity {

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tramo_reparto_seq")
  @SequenceGenerator(name = "tramo_reparto_seq", sequenceName = "tramo_reparto_seq", allocationSize = 1)
  private Long id;

  /** Desde. */
  @Column(name = "desde", nullable = false)
  private Integer desde;

  /** Hasta. */
  @Column(name = "hasta", nullable = false)
  private Integer hasta;

  /** Porcentaje Universidad. */
  @Column(name = "porcentaje_universidad", precision = 5, scale = 2, nullable = false)
  private BigDecimal porcentajeUniversidad;

  /** Porcentaje Inventores. */
  @Column(name = "porcentaje_inventores", precision = 5, scale = 2, nullable = false)
  private BigDecimal porcentajeInventores;

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
   * Interfaz para marcar validaciones en las activaciones de la entidad.
   */
  public interface OnActivar {
  }
}
