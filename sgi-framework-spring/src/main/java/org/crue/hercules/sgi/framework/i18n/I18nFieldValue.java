package org.crue.hercules.sgi.framework.i18n;

/**
 * I18n interface for database fields that supports internacionalization
 */
public interface I18nFieldValue {

  /**
   * Gets the language of the value
   * 
   * @return the language
   */
  Language getLang();

  /**
   * Gets the value in the language
   * 
   * @return the value
   */
  String getValue();
}
