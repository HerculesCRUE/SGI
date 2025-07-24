package org.crue.hercules.sgi.framework.i18n;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Languages supported
 */
public enum Language {
  /* Spanish */
  ES("es"),
  /* English */
  EN("en"),
  /* Basque */
  EU("eu");

  private String code;
  private Locale locale;

  private Language(String code) {
    this.code = code;
    this.locale = new Locale.Builder().setLanguage(code).build();
  }

  /**
   * Gets the ISO-639-1 code associated
   * 
   * @return the code
   */
  @JsonValue
  public String getCode() {
    return code;
  }

  /**
   * Gets the {@link Locale} associated
   * 
   * @return the {@link Locale}
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Gets the {@link Language} associated to a IS0-639-1 code, if exists.
   * 
   * @param code the language code
   * @return the {@link Language} or null
   */
  public static Language fromCode(String code) {
    for (Language lang : Language.values()) {
      if (lang.code.equals(code)) {
        return lang;
      }
    }
    return null;
  }
}
