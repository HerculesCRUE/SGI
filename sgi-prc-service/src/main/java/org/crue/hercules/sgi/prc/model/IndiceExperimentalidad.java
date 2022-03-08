package org.crue.hercules.sgi.prc.model;

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
@Table(name = IndiceExperimentalidad.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "departamento_ref",
        "tabla_indice_id" }, name = "UK_INDICEEXPERIMENTALIDAD_DEPARTAMENTOREF_TABLAINDICEID") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndiceExperimentalidad extends BaseEntity {

  protected static final String TABLE_NAME = "indice_experimentalidad";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

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

  /** departamentoRef */
  @Column(name = "departamento_ref", length = DEPARTAMENTO_REF_LENGTH, nullable = false)
  private String departamentoRef;

  @Column(name = "valor", nullable = false)
  private BigDecimal valor;

  /** TablaIndice Id */
  @Column(name = "tabla_indice_id", nullable = false)
  private Long tablaIndiceId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "tabla_indice_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INDICEEXPERIMENTALIDAD_TABLAINDICE"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final TablaIndice tablaIndice = null;

}
