package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.CertificadoAutorizacionController;
import org.crue.hercules.sgi.csp.dto.CertificadoAutorizacionInput;
import org.crue.hercules.sgi.csp.dto.CertificadoAutorizacionOutput;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CertificadoAutorizacionIT extends BaseIT {
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = CertificadoAutorizacionController.REQUEST_MAPPING;

  private HttpEntity<CertificadoAutorizacionInput> buildRequest(HttpHeaders headers,
      CertificadoAutorizacionInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<CertificadoAutorizacionInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsCertificadoAutorizacionOutput() throws Exception {
    String roles = "CSP-AUT-INV-ER";
    CertificadoAutorizacionInput toCreate = buildMockCertificadoAutorizacionInput(1L, "DOC-0001", "cert-aut-new");

    final ResponseEntity<CertificadoAutorizacionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, roles), CertificadoAutorizacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(response.getBody()).isNotNull();

    final CertificadoAutorizacionOutput created = response.getBody();
    Assertions.assertThat(created.getId()).isNotNull();
    Assertions.assertThat(created.getAutorizacionId()).isEqualTo(toCreate.getAutorizacionId());
    Assertions.assertThat(created.getNombre()).isEqualTo(toCreate.getNombre());
    Assertions.assertThat(created.getDocumentoRef()).isEqualTo(toCreate.getDocumentoRef());
    Assertions.assertThat(created.getVisible()).isEqualTo(toCreate.getVisible());
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/certificado_autorizacion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsCertificadoAutorizacionOutput() throws Exception {
    String roles = "CSP-AUT-E";
    CertificadoAutorizacionInput toUpdate = buildMockCertificadoAutorizacionInput(1L, "DOC-0001", "cert-aut-new");
    Long certificadoAutorizacionId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(
        certificadoAutorizacionId).toUri();

    final ResponseEntity<CertificadoAutorizacionOutput> response = restTemplate.exchange(uri,
        HttpMethod.PUT, buildRequest(null, toUpdate, roles), CertificadoAutorizacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();

    final CertificadoAutorizacionOutput updated = response.getBody();
    Assertions.assertThat(updated.getId()).isNotNull();
    Assertions.assertThat(updated.getAutorizacionId()).isEqualTo(toUpdate.getAutorizacionId());
    Assertions.assertThat(updated.getNombre()).isEqualTo(toUpdate.getNombre());
    Assertions.assertThat(updated.getDocumentoRef()).isEqualTo(toUpdate.getDocumentoRef());
    Assertions.assertThat(updated.getVisible()).isEqualTo(toUpdate.getVisible());
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/certificado_autorizacion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteById_ReturnsStatusCode204() throws Exception {
    String roles = "CSP-AUT-B";
    Long certificadoAutorizacionId = 2L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID).buildAndExpand(
        certificadoAutorizacionId).toUri();

    final ResponseEntity<Void> response = restTemplate.exchange(uri,
        HttpMethod.DELETE, buildRequest(null, null, roles), Void.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  private CertificadoAutorizacionInput buildMockCertificadoAutorizacionInput(Long autorizacionId, String documentoRef,
      String nombre) {
    return CertificadoAutorizacionInput.builder()
        .documentoRef(documentoRef)
        .autorizacionId(autorizacionId)
        .visible(Boolean.TRUE)
        .nombre(nombre)
        .build();
  }
}