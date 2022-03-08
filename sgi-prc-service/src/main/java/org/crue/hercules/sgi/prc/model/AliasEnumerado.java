package org.crue.hercules.sgi.prc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = AliasEnumerado.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "codigo_cvn" }, name = "UK_ALIASENUMERADO_CODIGOCVN") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AliasEnumerado extends BaseEntity {

  protected static final String TABLE_NAME = "alias_enumerado";
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

  /** CampoCVN */
  @Column(name = "codigo_cvn", length = CAMPO_CVN_LENGTH, nullable = false)
  private CodigoCVN codigoCVN;

  /** prefijoEnumerado */
  @Column(name = "prefijo_enumerado", length = PREFIJO_ENUMERADO_LENGTH, nullable = false)
  private String prefijoEnumerado;

}
