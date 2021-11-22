package org.crue.hercules.sgi.pii.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.pii.model.TramoReparto.OnActualizar;
import org.crue.hercules.sgi.pii.model.TramoReparto.OnCrear;
import org.crue.hercules.sgi.pii.validation.DesdeLowerThanHastaTramoReparto;
import org.crue.hercules.sgi.pii.validation.DistributionPercentTramoReparto;
import org.crue.hercules.sgi.pii.validation.NoOverlappedTramoReparto;
import org.crue.hercules.sgi.pii.validation.NotAllowedGapBetweenTramoReparto;
import org.crue.hercules.sgi.pii.validation.UniqueTipoTramoReparto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tramo_reparto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NoOverlappedTramoReparto(groups = { OnActualizar.class, OnCrear.class })
@NotAllowedGapBetweenTramoReparto(groups = { OnActualizar.class, OnCrear.class })
@UniqueTipoTramoReparto(groups = { OnActualizar.class, OnCrear.class })
@DistributionPercentTramoReparto(groups = { OnActualizar.class, OnCrear.class })
@DesdeLowerThanHastaTramoReparto(groups = { OnActualizar.class, OnCrear.class })
public class TramoReparto extends BaseEntity {

  public static final int TIPO_LENGTH = 25;

  public enum Tipo {
    /** Inicial */
    INICIAL,
    /** Intermedio */
    INTERMEDIO,
    /** Final */
    FINAL
  }

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
  @Column(name = "hasta", nullable = true)
  private Integer hasta;

  /** Porcentaje Universidad. */
  @Column(name = "porcentaje_universidad", precision = 5, scale = 2, nullable = false)
  private BigDecimal porcentajeUniversidad;

  /** Porcentaje Inventores. */
  @Column(name = "porcentaje_inventores", precision = 5, scale = 2, nullable = false)
  private BigDecimal porcentajeInventores;

  /** Tipo de Tramo de Reparto. */
  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", length = TIPO_LENGTH, nullable = false)
  private Tipo tipo;

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
