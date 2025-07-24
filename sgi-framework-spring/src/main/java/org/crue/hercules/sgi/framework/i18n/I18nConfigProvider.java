package org.crue.hercules.sgi.framework.i18n;

import java.util.List;

/*
 * Provider of I18n config values
 */
public interface I18nConfigProvider {

  public static final String CNF_ENABLED_LANGUAGES_KEY = "i18n-enabled-languages";
  public static final String CNF_LANGUAGE_PRIORITIES_KEY = "i18n-languages-priority";

  /**
   * Reload values
   */
  public void refresh();

  /**
   * Get's the list of enabled languages.
   * 
   * @return the list of languages
   */
  public List<Language> getEnabledLanguages();

  /**
   * Get's the list of languages ordered by priority. Lower index has the highest
   * priority.
   * 
   * @return the list of languages
   */
  public List<Language> getLanguagePriorities();
}
