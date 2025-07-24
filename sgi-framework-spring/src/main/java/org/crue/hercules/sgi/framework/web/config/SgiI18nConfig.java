package org.crue.hercules.sgi.framework.web.config;

import org.crue.hercules.sgi.framework.i18n.DefaultI18nConfigProvider;
import org.crue.hercules.sgi.framework.i18n.I18nConfig;
import org.crue.hercules.sgi.framework.i18n.I18nConfigProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for I18n configuration. To override default
 * {@link I18nConfigProvider} you should extend this class and create a new bean
 * that provide different implementation.
 */
@Configuration
public class SgiI18nConfig {
  /**
   * Create the default I18n config provider
   * 
   * @return I18n config provider
   */
  @Bean
  @ConditionalOnMissingBean
  public I18nConfigProvider defaultI18nConfigProvider() {
    return new DefaultI18nConfigProvider();
  }

  /**
   * Create the I18n config
   * 
   * @param configProvider the config provider to use
   * @return the I18n config
   */
  @Bean
  public I18nConfig i18nConfig(I18nConfigProvider configProvider) {
    return new I18nConfig(configProvider);
  }
}
