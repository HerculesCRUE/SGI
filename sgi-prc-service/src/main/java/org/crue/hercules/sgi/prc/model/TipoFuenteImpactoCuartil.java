package org.crue.hercules.sgi.prc.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.converter.TipoFuenteImpactoConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = TipoFuenteImpactoCuartil.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "tipo_fuente_impacto",
        "cuartil", "anio" }, name = "UK_TIPOFUENTEIMPACTOCUARTIL_TIPOFUENTE_CUARTIL_ANIO") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoFuenteImpactoCuartil extends BaseEntity {

  protected static final String TABLE_NAME = "tipo_fuente_impacto_cuartil";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  private static final long serialVersionUID = 1L;

  /** TipoRanking */
  public enum Cuartil {
    Q1,
    Q2,
    Q3,
    Q4;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "tipo_fuente_impacto", length = TIPO_FUENTE_IMPACTO_LENGTH, nullable = false)
  @Convert(converter = TipoFuenteImpactoConverter.class)
  private TipoFuenteImpacto fuenteImpacto;

  @Column(name = "cuartil", length = CUARTIL_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private Cuartil cuartil;

  @Column(name = "anio", nullable = false)
  private Integer anio;

}
