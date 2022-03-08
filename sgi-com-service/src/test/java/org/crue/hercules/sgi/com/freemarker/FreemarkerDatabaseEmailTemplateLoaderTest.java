package org.crue.hercules.sgi.com.freemarker;

import java.io.IOException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.com.config.SgiConfigProperties;
import org.crue.hercules.sgi.com.freemarker.FreemarkerDatabaseEmailTemplateLoader.EmailTplTemplateSource;
import org.crue.hercules.sgi.com.model.EmailTpl;
import org.crue.hercules.sgi.com.repository.EmailTplRepository;
import org.crue.hercules.sgi.framework.spring.context.support.boot.autoconfigure.ApplicationContextSupportAutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@Import({ ValidationAutoConfiguration.class, MessageSourceAutoConfiguration.class,
    ApplicationContextSupportAutoConfiguration.class })
@EnableConfigurationProperties(value = SgiConfigProperties.class)
@TestPropertySource(locations = { "classpath:application.yml" })
@ContextConfiguration(initializers = { ConfigFileApplicationContextInitializer.class })
public class FreemarkerDatabaseEmailTemplateLoaderTest {
  @Mock
  private EmailTplRepository repository;

  private FreemarkerDatabaseEmailTemplateLoader service;

  @BeforeEach
  void setUp() throws Exception {
    service = new FreemarkerDatabaseEmailTemplateLoader(repository);
  }

  @Test
  void findTemplateSource_withPath_returnsTemplateSource() throws IOException {
    // given: a template with the given name
    String tplName = "tpl";
    String tplSubjectPath = tplName + FreemarkerDatabaseEmailTemplateLoader.PATH_SUBJECT;
    BDDMockito.given(repository.findByName(tplName)).willReturn(Optional.of(
        EmailTpl.builder().id(1L).name(tplName).build()));

    // when: find the template by name
    EmailTplTemplateSource tpl = (EmailTplTemplateSource) service.findTemplateSource(tplSubjectPath);

    // then: the template is returned
    Assertions.assertThat(tpl).as("isNotNull()").isNotNull();
    Assertions.assertThat(tpl.getName()).as("getName()").isEqualTo(tplName);
  }

  @Test
  void findTemplateSource_withoutPath_throwsIllegalArgumentException() throws IOException {
    // given: a template with name without path
    String tplNoPath = "tpl";

    // when: find the template by name
    // then: exception is thrown
    Assertions.assertThatThrownBy(() -> service.findTemplateSource(
        tplNoPath)).isInstanceOf(IllegalArgumentException.class);
  }
}
