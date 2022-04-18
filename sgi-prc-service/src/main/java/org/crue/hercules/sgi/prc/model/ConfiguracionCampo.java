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

import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.converter.CodigoCVNConverter;
import org.crue.hercules.sgi.prc.model.converter.EpigrafeCVNConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = ConfiguracionCampo.TABLE_NAME, uniqueConstraints = {
    @UniqueConstraint(columnNames = { "codigo_cvn" }, name = "UK_CONFIGURACIONCAMPO_CODIGOCVN") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionCampo extends BaseEntity {

  protected static final String TABLE_NAME = "configuracion_campo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** TipoFormato */
  public enum TipoFormato {
    ENUMERADO,
    FECHA,
    TEXTO,
    NUMERO,
    BOOLEANO;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** CampoCVN */
  @Column(name = "codigo_cvn", length = CAMPO_CVN_LENGTH, nullable = false)
  @Convert(converter = CodigoCVNConverter.class)
  private CodigoCVN codigoCVN;

  /** TipoFormato */
  @Column(name = "tipo_formato", length = TIPO_FORMATO_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoFormato tipoFormato;

  /** fechaReferenciaInicio */
  @Column(name = "fecha_referencia_inicio", columnDefinition = "boolean default false", nullable = false)
  private Boolean fechaReferenciaInicio;

  /** fechaReferenciaFin */
  @Column(name = "fecha_referencia_fin", columnDefinition = "boolean default false", nullable = false)
  private Boolean fechaReferenciaFin;

  /** EpigrafeCVN */
  @Column(name = "epigrafe_cvn", length = EPIGRAFE_LENGTH, nullable = false)
  @Convert(converter = EpigrafeCVNConverter.class)
  private EpigrafeCVN epigrafeCVN;

  /** validacionAdicional */
  @Column(name = "validacion_adicional", columnDefinition = "boolean default false", nullable = false)
  private Boolean validacionAdicional;

}
