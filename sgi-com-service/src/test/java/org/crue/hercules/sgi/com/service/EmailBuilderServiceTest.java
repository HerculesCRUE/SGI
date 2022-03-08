package org.crue.hercules.sgi.com.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.com.exceptions.ContentException;
import org.crue.hercules.sgi.com.exceptions.ParamException;
import org.crue.hercules.sgi.com.exceptions.RecipientException;
import org.crue.hercules.sgi.com.exceptions.SubjectException;
import org.crue.hercules.sgi.com.freemarker.FreemarkerDatabaseEmailTemplateLoader;
import org.crue.hercules.sgi.com.freemarker.FreemarkerEmailTemplateProcessor;
import org.crue.hercules.sgi.com.model.ContentTpl;
import org.crue.hercules.sgi.com.model.Email;
import org.crue.hercules.sgi.com.model.EmailTpl;
import org.crue.hercules.sgi.com.model.SubjectTpl;
import org.crue.hercules.sgi.com.repository.AttachmentRepository;
import org.crue.hercules.sgi.com.repository.EmailParamRepository;
import org.crue.hercules.sgi.com.repository.EmailTplRepository;
import org.crue.hercules.sgi.com.repository.RecipientRepository;
import org.crue.hercules.sgi.com.service.EmailBuilderService.EmailData;
import org.crue.hercules.sgi.com.service.sgi.SgiApiDocumentRefsService;
import org.crue.hercules.sgi.com.service.sgi.SgiApiEmailParamsService;
import org.crue.hercules.sgi.com.service.sgi.SgiApiInternetAddressesService;
import org.crue.hercules.sgi.com.service.sgi.SgiApiSgdocService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

import freemarker.template.Configuration;

public class EmailBuilderServiceTest extends BaseServiceTest {
  Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_0);
  FreemarkerEmailTemplateProcessor freemarkerEmailTemplateProcessor;

  @Mock
  EmailTplRepository emailTplRepository;

  @Mock
  RecipientRepository recipientRepository;

  @Mock
  AttachmentRepository attachmentRepository;

  @Mock
  EmailParamRepository emailParamRepository;

  @Mock
  SgiApiInternetAddressesService sgiApiInternetAddressesService;

  @Mock
  SgiApiDocumentRefsService sgiApiDocumentRefsService;

  @Mock
  SgiApiEmailParamsService sgiApiEmailParamsService;

  @Mock
  SgiApiSgdocService sgiApiSgdocService;

  @Mock
  private RestTemplate restTemplate;

  private EmailBuilderService service;

  @BeforeEach
  public void setUp() throws Exception {
    FreemarkerDatabaseEmailTemplateLoader templateLoader = new FreemarkerDatabaseEmailTemplateLoader(
        emailTplRepository);
    freemarkerCfg.setTemplateLoader(templateLoader);
    freemarkerEmailTemplateProcessor = new FreemarkerEmailTemplateProcessor(emailTplRepository, freemarkerCfg);
    service = new EmailBuilderService(
        freemarkerEmailTemplateProcessor,
        recipientRepository,
        attachmentRepository,
        emailParamRepository,
        sgiApiInternetAddressesService,
        sgiApiDocumentRefsService,
        sgiApiEmailParamsService,
        sgiApiSgdocService);
  }

  @Test
  public void build_retunsEmailDTO() throws ContentException, RecipientException, ParamException, SubjectException {
    // given: an existing Email with template
    SubjectTpl subjectTpl = SubjectTpl.builder().tpl("Subject").build();
    ContentTpl contentTpl = ContentTpl.builder().tplText("Content").tplHtml("Content").build();
    EmailTpl emailTpl = EmailTpl.builder().name("test").subjectTpl(subjectTpl).contentTpl(contentTpl).build();
    Email email = Email.builder().id(1L).emailTpl(emailTpl).build();

    BDDMockito.given(emailTplRepository.findByName(emailTpl.getName()))
        .willReturn(Optional.of(emailTpl));

    // when: build
    EmailData dto = service.build(email);
    // then: the EmailDTO is created
    Assertions.assertThat(dto).as("isNotNull()").isNotNull();
  }

}
