package org.crue.hercules.sgi.com.config;

import java.util.Properties;

import org.crue.hercules.sgi.com.freemarker.FreemarkerDatabaseEmailTemplateLoader;
import org.crue.hercules.sgi.com.freemarker.FreemarkerEmailTemplateProcessor;
import org.crue.hercules.sgi.com.repository.EmailTplRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;

/**
 * Freemarker is used a the default template engine for the email templates.
 */
@Configuration
public class FreemarkerConfig {

  @Bean
  public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration(
      MultiTemplateLoader freemarkerDatabaseTemplateLoader, SgiConfigProperties sgiConfigProperties) {
    FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
    bean.setPreTemplateLoaders(freemarkerDatabaseTemplateLoader);
    Properties freemarkerProperties = new Properties();
    // Configure the Freemarket default time zone as the app configured time zone
    freemarkerProperties.setProperty(freemarker.core.Configurable.TIME_ZONE_KEY,
        sgiConfigProperties.getTimeZone().getID());
    freemarkerProperties.setProperty(freemarker.core.Configurable.AUTO_IMPORT_KEY, "sgi.ftl as sgi");
    // Disable search of localized templates
    freemarkerProperties.setProperty(freemarker.template.Configuration.LOCALIZED_LOOKUP_KEY, "false");
    bean.setFreemarkerSettings(freemarkerProperties);
    return bean;
  }

  @Bean
  public MultiTemplateLoader getFreemarkerDatabaseTemplateLoader(EmailTplRepository repository) {
    // The Email templates will be loaded from the database
    FreemarkerDatabaseEmailTemplateLoader dbLoader = new FreemarkerDatabaseEmailTemplateLoader(repository);
    ClassTemplateLoader classLoader = new ClassTemplateLoader(this.getClass().getClassLoader(),
        "/org/crue/hercules/sgi/com/freemarker/");
    return new MultiTemplateLoader(new TemplateLoader[] { classLoader, dbLoader });
  }

  @Bean
  public FreemarkerEmailTemplateProcessor getFreemarkerEmailTemplateProcessor(EmailTplRepository repository,
      freemarker.template.Configuration freemarkerCfg) {
    return new FreemarkerEmailTemplateProcessor(repository, freemarkerCfg);
  }
}
