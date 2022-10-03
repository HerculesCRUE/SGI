package org.crue.hercules.sgi.com.integration;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.com.dto.EmailInput;
import org.crue.hercules.sgi.com.dto.EmailInput.Deferrable;
import org.crue.hercules.sgi.com.dto.EmailOutput;
import org.crue.hercules.sgi.com.dto.EmailParam;
import org.crue.hercules.sgi.com.dto.Recipient;
import org.crue.hercules.sgi.com.dto.Status;
import org.crue.hercules.sgi.com.enums.ServiceType;
import org.crue.hercules.sgi.com.repository.EmailRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmailControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/emails";
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_SEND = "/{id}/send";

  @Autowired
  private EmailRepository emailRepository;

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/param.sql",
    "classpath:scripts/content_tpl.sql",
    "classpath:scripts/contenttpl_param.sql",
    "classpath:scripts/subject_tpl.sql",
    "classpath:scripts/subjecttpl_param.sql",
    "classpath:scripts/email_tpl.sql",
    "classpath:scripts/emailtpl_subjecttpl.sql",
    "classpath:scripts/emailtpl_contenttpl.sql",
    "classpath:scripts/email.sql",
    "classpath:scripts/recipient.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnEmailOutput() throws Exception {
    String[] roles = { "PII-INV-E" };

    EmailInput input = this.buildMockEmailInput();
    ResponseEntity<EmailOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        this.buildRequest(null, input, roles), EmailOutput.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isNotNull();

    EmailOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(this.emailRepository.count());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/param.sql",
    "classpath:scripts/content_tpl.sql",
    "classpath:scripts/contenttpl_param.sql",
    "classpath:scripts/subject_tpl.sql",
    "classpath:scripts/subjecttpl_param.sql",
    "classpath:scripts/email_tpl.sql",
    "classpath:scripts/emailtpl_subjecttpl.sql",
    "classpath:scripts/emailtpl_contenttpl.sql",
    "classpath:scripts/email.sql",
    "classpath:scripts/recipient.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnEmailOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long emailId = 1L;

    EmailInput input = this.buildMockEmailInput();
    ResponseEntity<EmailOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT,
        this.buildRequest(null, input, roles), EmailOutput.class, emailId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    EmailOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(emailId);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/param.sql",
    "classpath:scripts/content_tpl.sql",
    "classpath:scripts/contenttpl_param.sql",
    "classpath:scripts/subject_tpl.sql",
    "classpath:scripts/subjecttpl_param.sql",
    "classpath:scripts/email_tpl.sql",
    "classpath:scripts/emailtpl_subjecttpl.sql",
    "classpath:scripts/emailtpl_contenttpl.sql",
    "classpath:scripts/email.sql",
    "classpath:scripts/recipient.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void delete() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long emailId = 1L;

    ResponseEntity<Void> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.DELETE,
        this.buildRequest(null, null, roles), Void.class, emailId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/param.sql",
    "classpath:scripts/content_tpl.sql",
    "classpath:scripts/contenttpl_param.sql",
    "classpath:scripts/subject_tpl.sql",
    "classpath:scripts/subjecttpl_param.sql",
    "classpath:scripts/email_tpl.sql",
    "classpath:scripts/emailtpl_subjecttpl.sql",
    "classpath:scripts/emailtpl_contenttpl.sql",
    "classpath:scripts/email.sql",
    "classpath:scripts/recipient.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void get_ReturnEmailOutput() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long emailId = 1L;

    ResponseEntity<EmailOutput> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET,
        this.buildRequest(null, null, roles), EmailOutput.class, emailId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    EmailOutput output = response.getBody();
    Assertions.assertThat(output.getId()).isEqualTo(emailId);
    Assertions.assertThat(output.getTemplate()).isEqualTo("GENERIC_EMAIL");
    Assertions.assertThat(output.getRecipients()).hasSize(1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/param.sql",
    "classpath:scripts/content_tpl.sql",
    "classpath:scripts/contenttpl_param.sql",
    "classpath:scripts/subject_tpl.sql",
    "classpath:scripts/subjecttpl_param.sql",
    "classpath:scripts/email_tpl.sql",
    "classpath:scripts/emailtpl_subjecttpl.sql",
    "classpath:scripts/emailtpl_contenttpl.sql",
    "classpath:scripts/email.sql",
    "classpath:scripts/recipient.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll() throws Exception {
    String[] roles = { "PII-INV-V" };

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "id<7";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    ResponseEntity<List<EmailOutput>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<EmailOutput>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("6");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/param.sql",
    "classpath:scripts/content_tpl.sql",
    "classpath:scripts/contenttpl_param.sql",
    "classpath:scripts/subject_tpl.sql",
    "classpath:scripts/subjecttpl_param.sql",
    "classpath:scripts/email_tpl.sql",
    "classpath:scripts/emailtpl_subjecttpl.sql",
    "classpath:scripts/emailtpl_contenttpl.sql",
    "classpath:scripts/email.sql",
    "classpath:scripts/recipient.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void send_ReturnsStatus() throws Exception {
    String[] roles = { "PII-INV-E" };
    Long emailId = 6L;

    ResponseEntity<Status> response = this.restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_SEND,
        HttpMethod.GET,
        this.buildRequest(null, null, roles), Status.class, emailId);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  private EmailInput buildMockEmailInput() {
    List<EmailParam> emailParams = new LinkedList<>();
    emailParams.add(this.buildMockEmailParam("CSP_PRO_FASE_FECHA_INICIO", Instant.now().toString()));
    emailParams
        .add(this.buildMockEmailParam("CSP_PRO_FASE_FECHA_FIN", Instant.now().plus(33, ChronoUnit.DAYS).toString()));
    emailParams.add(this.buildMockEmailParam("CSP_PRO_TIPO_FASE", "Tsting template generation"));
    emailParams.add(this.buildMockEmailParam("CSP_PRO_FASE_OBSERVACIONES", "Testing email_tpl"));
    emailParams.add(this.buildMockEmailParam("CSP_PRO_FASE_TITULO_CONVOCATORIA", "Convocatoria Testing"));
    emailParams.add(this.buildMockEmailParam("CSP_PRO_FASE_TITULO_PROYECTO", "Proyecto Testing Mock"));

    List<Recipient> recipients = new LinkedList<>();
    recipients.add(Recipient.builder().name("test@um.com").address("test@um.com").build());

    return EmailInput.builder()
        .deferrableParams(Deferrable.builder()
            .method(HttpMethod.GET)
            .type(ServiceType.CSP)
            .url("http://sgie-csp-service/comunicados/proyectofases")
            .build())
        .params(emailParams)
        .recipients(recipients)
        .template("CSP_COM_PROYECTO_FASE")
        .build();
  }

  private EmailParam buildMockEmailParam(String name, String value) {
    return EmailParam.builder()
        .name(name)
        .value(value)
        .build();
  }
}