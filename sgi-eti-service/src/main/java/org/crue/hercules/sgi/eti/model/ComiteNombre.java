package org.crue.hercules.sgi.eti.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * Comite -> Nombre
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ComiteNombre implements Serializable, I18nFieldValue {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Género de nombre de investigación */
  public enum Genero {
    /** Femenino */
    F,
    /** Masculino */
    M;
  }

  /** Language. */
  @Column(name = "lang", nullable = false, length = 2)
  @Convert(converter = LanguageConverter.class)
  @NotNull
  private Language lang;

  /** Nombre */
  @Column(name = "value_", length = 255, nullable = false)
  @NotNull
  @Size(max = 255)
  private String value;

  /** Género Nombre */
  @Column(name = "genero", length = 1, nullable = false)
  @Enumerated(EnumType.STRING)
  private Genero genero;
}