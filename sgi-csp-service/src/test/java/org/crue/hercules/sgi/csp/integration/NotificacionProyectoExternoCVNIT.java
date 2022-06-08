package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.NotificacionProyectoExternoCVNController;
import org.crue.hercules.sgi.csp.dto.NotificacionCVNEntidadFinanciadoraOutput;
import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNAsociarAutorizacionInput;
import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNAsociarProyectoInput;
import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNInput;
import org.crue.hercules.sgi.csp.dto.NotificacionProyectoExternoCVNOutput;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificacionProyectoExternoCVNIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = NotificacionProyectoExternoCVNController.MAPPING;
  private static final String PATH_NOTIFICACIONES_CVN_ENTIDAD_FINANCIADORA = "/notificacionescvnentidadfinanciadora";
  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_ASOCIAR_AUTORIZACION = "/asociarautorizacion";
  private static final String PATH_ASOCIAR_PROYECTO = "/asociarproyecto";

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s",
        tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/notificacion_proyecto_externo_cvn.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsNotificacionProyectoExternoCVNOutputSubList() throws Exception {
    String[] roles = { "CSP-CVPR-V" };
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";
    String filter = "id<=3";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<NotificacionProyectoExternoCVNOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<NotificacionProyectoExternoCVNOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<NotificacionProyectoExternoCVNOutput> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3L);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/autorizacion.sql",
      "classpath:scripts/estado_autorizacion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsNotificacionProyectoExternoCVN() throws Exception {
    NotificacionProyectoExternoCVNInput toCreate = buildMockNotificacionProyectoExternoCVN();

    final ResponseEntity<NotificacionProyectoExternoCVN> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, toCreate, "CSP-CVPR-E"), NotificacionProyectoExternoCVN.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/notificacion_proyecto_externo_cvn.sql",
    "classpath:scripts/notificacion_cvn_entidad_financiadora.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllNotificacionCVNEntidadFinanciadora_WithPagingAndSorting_ReturnsNotificacionCVNEntidadFinanciadoraOutputSubList()
      throws Exception {
    String[] roles = { "CSP-CVPR-V" };
    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,asc";

    // when: find Convocatoria
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_NOTIFICACIONES_CVN_ENTIDAD_FINANCIADORA)
        .queryParam("s", sort)
        .buildAndExpand(1L).toUri();

    final ResponseEntity<List<NotificacionCVNEntidadFinanciadoraOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<NotificacionCVNEntidadFinanciadoraOutput>>() {
        });

    // given: Proyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<NotificacionCVNEntidadFinanciadoraOutput> responseData = response.getBody();
    Assertions.assertThat(responseData.size()).isEqualTo(3);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0)).isNotNull();
    Assertions.assertThat(responseData.get(1)).isNotNull();
    Assertions.assertThat(responseData.get(2)).isNotNull();

    Assertions.assertThat(responseData.get(0).getId()).isEqualTo(1L);
    Assertions.assertThat(responseData.get(1).getId()).isEqualTo(2L);
    Assertions.assertThat(responseData.get(2).getId()).isEqualTo(3L);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/notificacion_proyecto_externo_cvn.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsNotificacionProyectoExternoCVNOutput() throws Exception {
    String roles = "CSP-CVPR-V";
    Long notificacionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(notificacionId).toUri();

    final ResponseEntity<NotificacionProyectoExternoCVNOutput> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(null, null, roles), NotificacionProyectoExternoCVNOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getId()).isEqualTo(notificacionId);
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/notificacion_proyecto_externo_cvn.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void asociarAutorizacion_ReturnsNotificacionProyectoExternoCVNOutput() throws Exception {
    String roles = "CSP-CVPR-E";
    Long notificacionId = 1L;
    NotificacionProyectoExternoCVNAsociarAutorizacionInput toUpdate = buildNotificacionProyectoExternoCVNAsociarAutorizacionInput();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ASOCIAR_AUTORIZACION)
        .buildAndExpand(notificacionId).toUri();

    final ResponseEntity<NotificacionProyectoExternoCVNOutput> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), NotificacionProyectoExternoCVNOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getId()).isEqualTo(notificacionId);

    NotificacionProyectoExternoCVNOutput updated = response.getBody();
    Assertions.assertThat(updated.getAutorizacionId()).isEqualTo(updated.getAutorizacionId());
  }

  @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/notificacion_proyecto_externo_cvn.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void asociarProyecto_ReturnsNotificacionProyectoExternoCVNOutput() throws Exception {
    String roles = "CSP-CVPR-E";
    Long notificacionId = 1L;
    NotificacionProyectoExternoCVNAsociarProyectoInput toUpdate = buildNotificacionProyectoExternoCVNAsociarProyectoInput();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ASOCIAR_PROYECTO)
        .buildAndExpand(notificacionId).toUri();

    final ResponseEntity<NotificacionProyectoExternoCVNOutput> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), NotificacionProyectoExternoCVNOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).isNotNull();
    Assertions.assertThat(response.getBody().getId()).isEqualTo(notificacionId);

    NotificacionProyectoExternoCVNOutput updated = response.getBody();
    Assertions.assertThat(updated.getProyectoId()).isEqualTo(updated.getProyectoId());
  }

  private NotificacionProyectoExternoCVNInput buildMockNotificacionProyectoExternoCVN() {
    return NotificacionProyectoExternoCVNInput.builder()
        .autorizacionId(2L)
        .entidadParticipacionRef("00132245")
        .fechaFin(Instant.parse("2023-01-01T14:00:00.000Z"))
        .fechaInicio(Instant.parse("2022-01-31T14:00:00.000Z"))
        .responsableRef("23302408")
        .solicitanteRef("00112233")
        .proyectoCVNId("001")
        .titulo("Notificación 1 con Entidad y Solicitante")
        .build();
  }

  private NotificacionProyectoExternoCVNAsociarProyectoInput buildNotificacionProyectoExternoCVNAsociarProyectoInput() {
    return NotificacionProyectoExternoCVNAsociarProyectoInput.builder()
        .proyectoId(3L)
        .build();
  }

  private NotificacionProyectoExternoCVNAsociarAutorizacionInput buildNotificacionProyectoExternoCVNAsociarAutorizacionInput() {
    return NotificacionProyectoExternoCVNAsociarAutorizacionInput.builder()
        .autorizacionId(3L)
        .build();
  }

}
