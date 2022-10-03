package org.crue.hercules.sgi.com.integration;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.com.dto.EmailParam;
import org.crue.hercules.sgi.com.dto.ProcessedEmailTpl;
import org.crue.hercules.sgi.com.model.Param;
import org.junit.jupiter.api.Test;
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
class EmailTplControllerIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = "/emailtpls";
  private static final String PATH_PARAMETER_PROCESS = "/{name}/process";
  private static final String PATH_PARAMETERS = "/{name}/parameters";
  private static final String PATH_PARAMETERS_SUBJECT = "/{name}/parameters/subject";
  private static final String PATH_PARAMETERS_CONTENT = "/{name}/parameters/content";

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
    "classpath:scripts/emailtpl_contenttpl.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void processTemplate_ReturnsProcessedEmailTpl() throws Exception {
    String[] roles = { "PII-INV-E" };
    String templateName = "CSP_COM_PROYECTO_FASE";
    List<EmailParam> emailParams = new LinkedList<>();
    emailParams.add(this.buildMockEmailParam("CSP_PRO_FASE_FECHA_INICIO", Instant.now().toString()));
    emailParams
        .add(this.buildMockEmailParam("CSP_PRO_FASE_FECHA_FIN", Instant.now().plus(33, ChronoUnit.DAYS).toString()));
    emailParams.add(this.buildMockEmailParam("CSP_PRO_TIPO_FASE", "Tsting template generation"));
    emailParams.add(this.buildMockEmailParam("CSP_PRO_FASE_OBSERVACIONES", "Testing email_tpl"));
    emailParams.add(this.buildMockEmailParam("CSP_PRO_FASE_TITULO_CONVOCATORIA", "Convocatoria Testing"));
    emailParams.add(this.buildMockEmailParam("CSP_PRO_FASE_TITULO_PROYECTO", "Proyecto Testing Mock"));

    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_PROCESS)
        .buildAndExpand(templateName).toUri();

    ResponseEntity<ProcessedEmailTpl> response = this.restTemplate.exchange(uri, HttpMethod.POST,
        this.buildRequest(null, emailParams, roles), ProcessedEmailTpl.class);

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    ProcessedEmailTpl emailTpl = response.getBody();
    Assertions.assertThat(emailTpl.getContentHtml()).isNotNull();
    Assertions.assertThat(emailTpl.getContentText()).isNotNull();
    Assertions.assertThat(emailTpl.getSubject()).isNotNull();
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
    "classpath:scripts/emailtpl_contenttpl.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void parameters_WithSortingAndPaging_ReturnsParamSublist() throws Exception {
    String[] roles = { "PII-INV-V" };
    String templateName = "CSP_COM_PROYECTO_FASE";

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETERS).queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(templateName).toUri();

    ResponseEntity<List<Param>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<Param>>() {
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
    "classpath:scripts/emailtpl_contenttpl.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void subjectParameters_WithSortingAndPaging_ReturnsParamSublist() throws Exception {
    String[] roles = { "PII-INV-V" };
    String templateName = "CSP_COM_PROYECTO_FASE";

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETERS_SUBJECT).queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(templateName).toUri();

    ResponseEntity<List<Param>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<Param>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("3");
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
    "classpath:scripts/emailtpl_contenttpl.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void contentParameters_WithSortingAndPaging_ReturnsParamSublist() throws Exception {
    String[] roles = { "PII-INV-V" };
    String templateName = "CSP_COM_PROYECTO_FASE";

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETERS_CONTENT).queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(templateName).toUri();

    ResponseEntity<List<Param>> response = this.restTemplate.exchange(uri, HttpMethod.GET,
        this.buildRequest(headers, null, roles), new ParameterizedTypeReference<List<Param>>() {
        });

    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).isEqualTo("6");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).isEqualTo("0");
  }

  private EmailParam buildMockEmailParam(String name, String value) {
    return EmailParam.builder()
        .name(name)
        .value(value)
        .build();
  }
}