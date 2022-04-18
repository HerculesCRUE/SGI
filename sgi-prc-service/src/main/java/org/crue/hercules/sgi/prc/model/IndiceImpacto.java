package org.crue.hercules.sgi.prc.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
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

import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.converter.TipoFuenteImpactoConverter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = IndiceImpacto.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndiceImpacto extends BaseEntity {

  protected static final String TABLE_NAME = "indice_impacto";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** TipoRanking */
  public enum TipoRanking {
    /** Clase1 */
    CLASE1,
    /** Clase2 */
    CLASE2,
    /** Clase3 */
    CLASE3,
    /** A* */
    A_POR,
    /** A */
    A;
  }

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** TipoFuenteImpacto */
  @Column(name = "tipo_fuente_impacto", length = TIPO_FUENTE_IMPACTO_LENGTH, nullable = false)
  @Convert(converter = TipoFuenteImpactoConverter.class)
  private TipoFuenteImpacto fuenteImpacto;

  /** indice */
  @Column(name = "indice", nullable = true)
  private BigDecimal indice;

  /** TipoRanking */
  @Column(name = "tipo_ranking", length = TIPO_RANKING_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoRanking ranking;

  /** anio */
  @Column(name = "anio", nullable = true)
  private Integer anio;

  /** otraFuenteImpacto */
  @Column(name = "otra_fuente_impacto", length = OTRA_FUENTE_IMPACTO_LENGTH, nullable = true)
  private String otraFuenteImpacto;

  /** posicionPublicacion */
  @Column(name = "posicion_publicacion", nullable = true)
  private BigDecimal posicionPublicacion;

  /** numeroRevistas */
  @Column(name = "numero_revistas", nullable = true)
  private BigDecimal numeroRevistas;

  /** revista25 */
  @Column(name = "revista_25", columnDefinition = "boolean default false", nullable = true)
  private Boolean revista25;

  /** ProduccionCientifica Id */
  @Column(name = "produccion_cientifica_id", nullable = false)
  private Long produccionCientificaId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "produccion_cientifica_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INDICEIMPACTO_PRODUCCIONCIENTIFICA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProduccionCientifica produccionCientifica = null;

}
