package org.crue.hercules.sgi.framework.web.config;

import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.data.domain.SgiAuditorAware;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.DatabaseStartupValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import liquibase.Liquibase;
import lombok.extern.slf4j.Slf4j;

/**
 * SgiDataConfig
 * 
 * Allows Delay startup of your Spring Boot application until your DB is up
 * (see: https://deinum.biz/2020-06-30-Wait-for-database-startup/).
 * 
 * Enables {@link SgiAuditorAware}.
 * 
 * Enbles set hibernate.default_schema property via
 * SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA environment variable.
 */
@Component
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Slf4j
public class SgiDataConfig {
  private final static String HIBERNATE_CUSTOM_DEFAULT_SCHEMA_PARAM = "hibernate.default.schema";

  @Bean
  public DatabaseDriver databaseDriver(@Value("${spring.datasource.driver-class-name}") String driverClassName) {
    log.debug("databaseDriver(String driverClassName) - start");
    if (StringUtils.hasLength(driverClassName)) {
      for (DatabaseDriver driver : DatabaseDriver.values()) {
        if (driverClassName.equals(driver.getDriverClassName())) {
          log.debug("databaseDriver(String driverClassName) - end");
          return driver;
        }
      }
    }
    log.warn("Unknown database driver: {0}", driverClassName);
    log.debug("databaseDriver(String driverClassName) - end");
    return DatabaseDriver.UNKNOWN;
  }

  @Bean
  public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource, DatabaseDriver databaseDriver) {
    log.debug("databaseStartupValidator(DataSource dataSource, DatabaseDriver databaseDriver) - start");
    DatabaseStartupValidator dsv = new DatabaseStartupValidator();
    dsv.setDataSource(dataSource);
    dsv.setValidationQuery(databaseDriver.getValidationQuery());
    log.debug("databaseStartupValidator(DataSource dataSource, DatabaseDriver databaseDriver) - end");
    return dsv;
  }

  @Bean
  public static BeanFactoryPostProcessor dependsOnPostProcessor() {
    return bf -> {
      log.debug("dependsOnPostProcessor() - start");
      // Let beans that need the database depend on the DatabaseStartupValidator
      // like the EntityManagerFactory, Liquibase or JdbcTemplate
      String[] jdbc = bf.getBeanNamesForType(JdbcTemplate.class);
      Stream.of(jdbc).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("databaseStartupValidator"));

      String[] liquibase = bf.getBeanNamesForType(Liquibase.class);
      Stream.of(liquibase).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("databaseStartupValidator"));

      String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);
      Stream.of(jpa).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("databaseStartupValidator"));
      log.debug("dependsOnPostProcessor() - end");
    };
  }

  @Bean
  public static HibernatePropertiesCustomizer hibernatePropertiesCustomizer(Validator validator) {
    return new HibernatePropertiesCustomizer() {
      @Override
      public void customize(Map<String, Object> hibernateProperties) {
        log.debug("customize(Map<String, Object> hibernateProperties) - start");
        if (hibernateProperties.containsKey(HIBERNATE_CUSTOM_DEFAULT_SCHEMA_PARAM)) {
          Object propertyValue = hibernateProperties.get(HIBERNATE_CUSTOM_DEFAULT_SCHEMA_PARAM);
          log.info("{0}={1}", HIBERNATE_CUSTOM_DEFAULT_SCHEMA_PARAM, propertyValue);
          hibernateProperties.put(AvailableSettings.DEFAULT_SCHEMA, propertyValue);
        }
        // Prevent Hibernate Validator from creating it's own ConstraintValidatorManager
        // for entity level validation
        // see:
        // https://stackoverflow.com/questions/56542699/hibernate-validator-in-spring-boot-using-different-constraintvalidatormanager
        hibernateProperties.put("javax.persistence.validation.factory", validator);
        log.debug("customize(Map<String, Object> hibernateProperties) - end");
      }
    };
  }

  @Bean
  public AuditorAware<String> auditorAware() {
    log.debug("auditorAware() - start");
    log.debug("auditorAware() - end");
    return new SgiAuditorAware();
  }
}