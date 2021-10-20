package org.crue.hercules.sgi.pii.model;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = PeriodoTitularidadTitular.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodoTitularidadTitular extends BaseEntity {

  private static final String SEQ_SUFFIX = "_seq";
  public static final String TABLE_NAME = "periodotitularidad_titular";
  public static final int REF_LENGTH = 50;
  public static final int PARTICIPACION_MIN = 1;
  public static final int PARTICIPACION_MAX = 100;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_NAME + SEQ_SUFFIX)
  @SequenceGenerator(name = TABLE_NAME + SEQ_SUFFIX, sequenceName = TABLE_NAME + SEQ_SUFFIX, allocationSize = 1)
  private Long id;

  /** Porcentaje de Participación en la Invención */
  @Column(name = "participacion", nullable = false)
  private BigDecimal participacion;

  /** Referencia a una Empresa titular */
  @Column(name = "titular_ref", length = REF_LENGTH, nullable = false)
  private String titularRef;

  @Column(name = "periodotitularidad_id", nullable = false)
  private Long periodoTitularidadId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "periodotitularidad_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PERIODOTITULARIDADTITULAR_PERIODOTITULARIDAD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final PeriodoTitularidad periodoTitularidad = null;

}
