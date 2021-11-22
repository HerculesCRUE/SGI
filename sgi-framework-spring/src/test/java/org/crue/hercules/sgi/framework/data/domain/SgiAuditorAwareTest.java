package org.crue.hercules.sgi.framework.data.domain;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.data.domain.SgiAuditorAwareTest.TestConfig;
import org.crue.hercules.sgi.framework.data.domain.SgiAuditorAwareTest.TestConfig.TestEntity;
import org.crue.hercules.sgi.framework.data.jpa.domain.Activable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@DataJpaTest
@ContextConfiguration(classes = { TestConfig.class })
class SgiAuditorAwareTest {
  @Autowired
  private AuditorAware<String> auditorAware;

  @Test
  @WithMockUser(username = "user")
  void currentAuditor_withUser_returnsUser() {
    Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
    Assertions.assertThat(currentAuditor).isPresent().contains("user");
  }

  @Test
  void currentAuditor_withoutUser_returnsEmpty() {
    Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
    Assertions.assertThat(currentAuditor).isEmpty();
  }

  // Using full qualified class names (otherwise compilation fail with maven).
  // See: https://bugs.openjdk.java.net/browse/JDK-8056066
  /**
   * A nested @Configuration class wild be used instead of the application’s
   * primary configuration.
   * <p>
   * Unlike a nested @Configuration class, which would be used instead of your
   * application’s primary configuration, a nested @TestConfiguration class is
   * used in addition to your application’s primary configuration.
   */
  @org.springframework.context.annotation.Configuration
  /**
   * Tells Spring Boot to start adding beans based on classpath settings, other
   * beans, and various property settings.
   */
  @org.springframework.boot.autoconfigure.EnableAutoConfiguration
  @org.springframework.data.jpa.repository.config.EnableJpaAuditing(auditorAwareRef = "auditorAware")
  @org.springframework.boot.autoconfigure.domain.EntityScan(basePackageClasses = TestEntity.class)
  public static class TestConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
      return new SgiAuditorAware();
    }

    @Entity
    @Table(name = "test_entity")
    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    public static class TestEntity extends Activable {
      /** Id */
      @Id
      @Column(name = "id", nullable = false)
      @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_entity_seq")
      @SequenceGenerator(name = "test_entity_seq", sequenceName = "test_entity_seq", allocationSize = 1)
      private Long id;

      @Column(name = "name", nullable = false)
      private String name;
    }
  }
}
