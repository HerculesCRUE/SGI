package org.crue.hercules.sgi.eti.config;

import org.crue.hercules.sgi.eti.service.sgi.SgiApiCnfService;
import org.crue.hercules.sgi.framework.i18n.I18nConfigProvider;
import org.crue.hercules.sgi.framework.web.config.SgiI18nConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class I18nConfiguration extends SgiI18nConfig {

  @Bean
  I18nConfigProvider i18nConfigProvider(SgiApiCnfService cnfService) {
    return new CnfI18nConfigProvider(cnfService);
  }
}
