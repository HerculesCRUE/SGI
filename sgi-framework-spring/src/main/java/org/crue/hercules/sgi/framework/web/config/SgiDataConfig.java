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
 * Enables set hibernate.default_schema property via
 * SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA environment variable.
 */
@Component
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Slf4j
public class SgiDataConfig {
  private static final String HIBERNATE_CUSTOM_DEFAULT_SCHEMA_PARAM = "hibernate.default.schema";

  private static final String DATABASE_STARTUP_VALIDATOR_BEAN_NAME = "databaseStartupValidator";

  /**
   * Gets a {@link DatabaseDriver} of the provided driver class name.
   * 
   * @param driverClassName the driver class name
   * @return the {@link DatabaseDriver}
   */
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
    log.warn("Unknown database driver: {}", driverClassName);
    log.debug("databaseDriver(String driverClassName) - end");
    return DatabaseDriver.UNKNOWN;
  }

  /**
   * Creates a {@link DatabaseStartupValidator} using the provided
   * {@link DataSource} and {@link DatabaseDriver} to defer application
   * initialization until a database has started up.
   * 
   * @param dataSource     the {@link DataSource}
   * @param databaseDriver the {@link DatabaseDriver}
   * @return the {@link DatabaseStartupValidator}
   */
  @Bean
  public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource, DatabaseDriver databaseDriver) {
    log.debug("databaseStartupValidator(DataSource dataSource, DatabaseDriver databaseDriver) - start");
    DatabaseStartupValidator dsv = new DatabaseStartupValidator();
    dsv.setDataSource(dataSource);
    dsv.setValidationQuery(databaseDriver.getValidationQuery());
    log.debug("databaseStartupValidator(DataSource dataSource, DatabaseDriver databaseDriver) - end");
    return dsv;
  }

  /**
   * Creates a {@link BeanFactoryPostProcessor} that let beans like the
   * {@link EntityManagerFactory}, {@link Liquibase} or {@link JdbcTemplate} that
   * need the database depend on the {@link DatabaseStartupValidator}.
   * 
   * @return the {@link BeanFactoryPostProcessor}
   */
  @Bean
  public static BeanFactoryPostProcessor dependsOnPostProcessor() {
    return bf -> {
      log.debug("dependsOnPostProcessor() - start");
      // Let beans that need the database depend on the DatabaseStartupValidator
      // like the EntityManagerFactory, Liquibase or JdbcTemplate
      String[] jdbc = bf.getBeanNamesForType(JdbcTemplate.class);
      Stream.of(jdbc).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn(DATABASE_STARTUP_VALIDATOR_BEAN_NAME));

      String[] liquibase = bf.getBeanNamesForType(Liquibase.class);
      Stream.of(liquibase).map(bf::getBeanDefinition)
          .forEach(it -> it.setDependsOn(DATABASE_STARTUP_VALIDATOR_BEAN_NAME));

      String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);
      Stream.of(jpa).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn(DATABASE_STARTUP_VALIDATOR_BEAN_NAME));
      log.debug("dependsOnPostProcessor() - end");
    };
  }

  /**
   * Returns a {@link HibernatePropertiesCustomizer} that enables set
   * hibernate.default_schema property via
   * SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA environment variable.
   * 
   * @param validator the {@link Validator} to set to Hibernate
   * @return the {@link HibernatePropertiesCustomizer}
   */
  @Bean
  public static HibernatePropertiesCustomizer hibernatePropertiesCustomizer(Validator validator) {
    return (Map<String, Object> hibernateProperties) -> {
      log.debug("customize(Map<String, Object> hibernateProperties) - start");
      if (hibernateProperties.containsKey(HIBERNATE_CUSTOM_DEFAULT_SCHEMA_PARAM)) {
        Object propertyValue = hibernateProperties.get(HIBERNATE_CUSTOM_DEFAULT_SCHEMA_PARAM);
        log.info("{}={}", HIBERNATE_CUSTOM_DEFAULT_SCHEMA_PARAM, propertyValue);
        hibernateProperties.put(AvailableSettings.DEFAULT_SCHEMA, propertyValue);
      }
      // Prevent Hibernate Validator from creating it's own ConstraintValidatorManager
      // for entity level validation
      // see:
      // https://stackoverflow.com/questions/56542699/hibernate-validator-in-spring-boot-using-different-constraintvalidatormanager
      hibernateProperties.put("javax.persistence.validation.factory", validator);
      log.debug("customize(Map<String, Object> hibernateProperties) - end");
    };
  }

  /**
   * Registers the {@link SgiAuditorAware}.
   * 
   * @return the {@link SgiAuditorAware}
   */
  @Bean
  public AuditorAware<String> auditorAware() {
    log.debug("auditorAware() - start");
    log.debug("auditorAware() - end");
    return new SgiAuditorAware();
  }
}