package org.crue.hercules.sgi.com.config;

import java.util.Properties;

import org.crue.hercules.sgi.com.freemarker.FreemarkerDatabaseEmailTemplateLoader;
import org.crue.hercules.sgi.com.freemarker.FreemarkerEmailTemplateProcessor;
import org.crue.hercules.sgi.com.repository.EmailTplRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

/**
 * Freemarker is used a the default template engine for the email templates.
 */
@Configuration
public class FreemarkerConfig {

  @Bean
  public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration(
      FreemarkerDatabaseEmailTemplateLoader freemarkerDatabaseTemplateLoader, SgiConfigProperties sgiConfigProperties) {
    FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
    bean.setPreTemplateLoaders(freemarkerDatabaseTemplateLoader);
    Properties freemarkerProperties = new Properties();
    // Configure the Freemarket default time zone as the app configured time zone
    freemarkerProperties.setProperty(freemarker.core.Configurable.TIME_ZONE_KEY,
        sgiConfigProperties.getTimeZone().getID());
    bean.setFreemarkerSettings(freemarkerProperties);
    return bean;
  }

  @Bean
  public FreemarkerDatabaseEmailTemplateLoader getFreemarkerDatabaseTemplateLoader(EmailTplRepository repository) {
    // The Email templates will be loaded from the database
    return new FreemarkerDatabaseEmailTemplateLoader(repository);
  }

  @Bean
  public FreemarkerEmailTemplateProcessor getFreemarkerEmailTemplateProcessor(EmailTplRepository repository,
      freemarker.template.Configuration freemarkerCfg) {
    return new FreemarkerEmailTemplateProcessor(repository, freemarkerCfg);
  }
}
