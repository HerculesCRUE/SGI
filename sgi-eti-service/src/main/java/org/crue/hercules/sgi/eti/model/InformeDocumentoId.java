package org.crue.hercules.sgi.eti.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.persistence.LanguageConverter;

import lombok.EqualsAndHashCode;

/**
 * Informe Documento Id
 */
@EqualsAndHashCode
public class InformeDocumentoId implements Serializable {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  @Column(name = "informe_id", nullable = false)
  private Long informeId;

  @Column(name = "lang", nullable = false, length = 2)
  @Convert(converter = LanguageConverter.class)
  private Language lang;

}