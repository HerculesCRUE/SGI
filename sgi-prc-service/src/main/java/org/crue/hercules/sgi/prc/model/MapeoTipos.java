package org.crue.hercules.sgi.prc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = MapeoTipos.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapeoTipos extends BaseEntity {

  protected static final String TABLE_NAME = "mapeo_tipos";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "id_tipo_ref", nullable = false)
  private Long idTipoRef;

  @Column(name = "valor", length = VALOR_MAPEO_TIPOS, nullable = false)
  private String valor;

  /** CampoCVN */
  @Column(name = "codigo_cvn", length = CAMPO_CVN_LENGTH, nullable = false)
  private CodigoCVN codigoCVN;

}
