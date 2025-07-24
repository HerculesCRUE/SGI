package org.crue.hercules.sgi.csp.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValue;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.persistence.LanguageConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Tipo Documento -> Descripcion
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TipoDocumentoDescripcion implements Serializable, I18nFieldValue {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Language. */
  @Column(name = "lang", nullable = false, length = 2)
  @Convert(converter = LanguageConverter.class)
  @NotNull
  private Language lang;

  /** Descripcion */
  @Column(name = "value_", length = 250, nullable = true)
  @Size(max = 250)
  private String value;
}
