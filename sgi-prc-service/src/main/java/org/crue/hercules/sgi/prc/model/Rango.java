package org.crue.hercules.sgi.prc.model;

import java.math.BigDecimal;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = Rango.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rango extends BaseEntity {

  protected static final String TABLE_NAME = "rango";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  private static final long serialVersionUID = 1L;

  public enum TipoRango {
    CUANTIA_CONTRATOS,
    LICENCIA,
    CUANTIA_COSTES_INDIRECTOS;
  }

  public enum TipoTemporalidad {
    INICIAL,
    INTERMEDIO,
    FINAL;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "tipo_rango", length = TIPO_RANGO_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoRango tipoRango;

  @Column(name = "tipo_temporalidad", length = TIPO_TEMPORALIDAD_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoRango tipoTemporalidad;

  @Column(name = "desde", nullable = false)
  private BigDecimal desde;

  @Column(name = "hasta", nullable = true)
  private BigDecimal hasta;

  @Column(name = "puntos", nullable = false)
  private BigDecimal puntos;

  @Column(name = "convocatoria_baremacion_id", nullable = false)
  private Long convocatoriaBaremacionId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "convocatoria_baremacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_RANGO_CONVOCATORIABAREMACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConvocatoriaBaremacion convocatoriaBaremacion = null;

}
