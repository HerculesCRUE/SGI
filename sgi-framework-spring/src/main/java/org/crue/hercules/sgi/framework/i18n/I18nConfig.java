package org.crue.hercules.sgi.framework.i18n;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

/**
 * I18n config as a singleton.
 * 
 * Gets the values from an {@link I18nConfigProvider}.
 */
public class I18nConfig implements InitializingBean {

  private static I18nConfig instance;

  private final I18nConfigProvider configProvider;

  /**
   * Default constructor
   * 
   * @param configProvider The provider of config values
   */
  public I18nConfig(I18nConfigProvider configProvider) {
    this.configProvider = configProvider;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    instance = this;
  }

  /**
   * Get's the singleton instance
   * 
   * @return the instance
   */
  public static I18nConfig get() {
    return instance;
  }

  /**
   * Get's the list of languages ordered by priority. Lower index has the highest
   * priority.
   * 
   * @return the list of languages
   */
  public List<Language> getLanguagePriorities() {
    return configProvider.getLanguagePriorities();
  }

  /**
   * Get's the list of enabled languages.
   * 
   * @return the list of languages
   */
  public List<Language> getEnabledLanguages() {
    return configProvider.getEnabledLanguages();
  }

}
