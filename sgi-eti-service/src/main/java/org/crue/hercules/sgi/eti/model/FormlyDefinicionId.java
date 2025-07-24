package org.crue.hercules.sgi.eti.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.persistence.LanguageConverter;

import lombok.EqualsAndHashCode;

/**
 * Formly Definicion Id
 */
@EqualsAndHashCode
public class FormlyDefinicionId implements Serializable {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  @NotNull
  @Column(name = "formly_id", nullable = false)
  private Long formlyId;

  @Column(name = "lang", nullable = false, length = 2)
  @Convert(converter = LanguageConverter.class)
  private Language lang;
}