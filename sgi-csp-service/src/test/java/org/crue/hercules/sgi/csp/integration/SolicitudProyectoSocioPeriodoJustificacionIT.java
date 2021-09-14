package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
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

/**
 * Test de integracion de SolicitudProyectoSocioPeriodoJustificacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SolicitudProyectoSocioPeriodoJustificacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectosocioperiodojustificaciones";

  private HttpEntity<SolicitudProyectoSocioPeriodoJustificacion> buildRequest(HttpHeaders headers,
      SolicitudProyectoSocioPeriodoJustificacion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E")));

    HttpEntity<SolicitudProyectoSocioPeriodoJustificacion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<List<SolicitudProyectoSocioPeriodoJustificacion>> buildRequestList(HttpHeaders headers,
      List<SolicitudProyectoSocioPeriodoJustificacion> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<List<SolicitudProyectoSocioPeriodoJustificacion>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void updateSolicitudProyectoSocioPeriodoJustificacionesSolicitudProyectoSocio_ReturnsSolicitudProyectoSocioPeriodoJustificacionList()
      throws Exception {

    // given: una lista con uno de los SolicitudProyectoSocioPeriodoJustificacion
    // actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoSocioPeriodoJustificacion newSolicitudProyectoSocioPeriodoJustificacion = generarMockSolicitudProyectoSocioPeriodoJustificacion(
        null, 27, 30, 1L);
    SolicitudProyectoSocioPeriodoJustificacion updatedSolicitudProyectoSocioPeriodoJustificacion = generarMockSolicitudProyectoSocioPeriodoJustificacion(
        103L, 1, 26, 1L);

    List<SolicitudProyectoSocioPeriodoJustificacion> solicitudProyectoSocioPeriodoJustificaciones = Arrays
        .asList(newSolicitudProyectoSocioPeriodoJustificacion, updatedSolicitudProyectoSocioPeriodoJustificacion);

    // when: updateSolicitudProyectoSocioPeriodoJustificacionesSolicitudProyecto
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<SolicitudProyectoSocioPeriodoJustificacion>> response = restTemplate.exchange(uri,
        HttpMethod.PATCH, buildRequestList(null, solicitudProyectoSocioPeriodoJustificaciones),
        new ParameterizedTypeReference<List<SolicitudProyectoSocioPeriodoJustificacion>>() {
        });

    // then: Se crea el nuevo SolicitudProyectoSocioPeriodoJustificacion, se
    // actualiza el
    // existe y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<SolicitudProyectoSocioPeriodoJustificacion> responseData = response.getBody();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getId());
    Assertions.assertThat(responseData.get(0).getSolicitudProyectoSocioId()).as("get(0).getSolicitudProyectoSocioId()")
        .isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(responseData.get(0).getMesInicial()).as("get(0).getMesInicial()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(responseData.get(0).getMesFinal()).as("get(0).getMesFinal()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(responseData.get(0).getFechaInicio()).as("get(0).getFechaInicio()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getFechaInicio());
    Assertions.assertThat(responseData.get(0).getFechaFin()).as("get(0).getFechaFin()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getFechaFin());
    Assertions.assertThat(responseData.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()").isEqualTo(1);
    Assertions.assertThat(responseData.get(0).getObservaciones()).as("get(0).getObservaciones()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoJustificacion.getObservaciones());

    Assertions.assertThat(responseData.get(1).getSolicitudProyectoSocioId()).as("get(1).getSolicitudProyectoSocioId()")
        .isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(responseData.get(1).getMesInicial()).as("get(1).getMesInicial()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getMesInicial());
    Assertions.assertThat(responseData.get(1).getMesFinal()).as("get(1).getMesFinal()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getMesFinal());
    Assertions.assertThat(responseData.get(1).getFechaInicio()).as("get(1).getFechaInicio()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getFechaInicio());
    Assertions.assertThat(responseData.get(1).getFechaFin()).as("get(1).getFechaFin()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getFechaFin());
    Assertions.assertThat(responseData.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()").isEqualTo(2);
    Assertions.assertThat(responseData.get(1).getObservaciones()).as("get(1).getObservaciones()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoJustificacion.getObservaciones());

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CENL-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "mesInicial,asc";

    URI uriFindAllSolicitudProyectoSocioPeriodoJustificacion = UriComponentsBuilder
        .fromUriString("/solicitudproyectosocio" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH).queryParam("s", sort)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<SolicitudProyectoSocioPeriodoJustificacion>> responseFindAllSolicitudProyectoSocioPeriodoJustificacion = restTemplate
        .exchange(uriFindAllSolicitudProyectoSocioPeriodoJustificacion, HttpMethod.GET, buildRequestList(headers, null),
            new ParameterizedTypeReference<List<SolicitudProyectoSocioPeriodoJustificacion>>() {
            });

    Assertions.assertThat(responseFindAllSolicitudProyectoSocioPeriodoJustificacion.getStatusCode())
        .isEqualTo(HttpStatus.OK);
    final List<SolicitudProyectoSocioPeriodoJustificacion> responseDataFindAll = responseFindAllSolicitudProyectoSocioPeriodoJustificacion
        .getBody();
    Assertions.assertThat(responseDataFindAll.size()).as("size()")
        .isEqualTo(solicitudProyectoSocioPeriodoJustificaciones.size());
    Assertions.assertThat(responseDataFindAll.get(0).getId()).as("responseDataFindAll.get(0).getId()")
        .isEqualTo(responseData.get(0).getId());
    Assertions.assertThat(responseDataFindAll.get(1).getId()).as("responseDataFindAll.get(1).getId()")
        .isEqualTo(responseData.get(1).getId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsSolicitudProyectoSocioPeriodoJustificacion() throws Exception {
    Long idSolicitudProyectoSocioPeriodoJustificacion = 1L;

    final ResponseEntity<SolicitudProyectoSocioPeriodoJustificacion> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoSocioPeriodoJustificacion.class, idSolicitudProyectoSocioPeriodoJustificacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion = response.getBody();
    Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacion.getId()).as("getId()")
        .isEqualTo(idSolicitudProyectoSocioPeriodoJustificacion);
    Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacion.getSolicitudProyectoSocioId())
        .as("getSolicitudProyectoSocioId()").isEqualTo(1L);
    Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacion.getMesInicial()).as("getMesInicial()")
        .isEqualTo(1);
    Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacion.getMesFinal()).as("getMesFinal()").isEqualTo(2);
    Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacion.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(Instant.parse("2020-10-10T00:00:00Z"));
    Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacion.getFechaFin()).as("getFechaFin()")
        .isEqualTo(Instant.parse("2020-11-20T23:59:59Z"));
    Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacion.getNumPeriodo()).as("getNumPeriodo()")
        .isEqualTo(1);
    Assertions.assertThat(solicitudProyectoSocioPeriodoJustificacion.getObservaciones()).as("getObservaciones()")
        .isEqualTo("observaciones-001");
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioPeriodoJustificacion
   * 
   * @param id                  id del SolicitudProyectoSocioPeriodoJustificacion
   * @param mesInicial          Mes inicial
   * @param mesFinal            Mes final
   * @param solicitudProyectoId Id SolicitudProyecto
   * @return el objeto SolicitudProyectoSocioPeriodoJustificacion
   */
  private SolicitudProyectoSocioPeriodoJustificacion generarMockSolicitudProyectoSocioPeriodoJustificacion(Long id,
      Integer mesInicial, Integer mesFinal, Long solicitudProyectoId) {
    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion = new SolicitudProyectoSocioPeriodoJustificacion();
    solicitudProyectoSocioPeriodoJustificacion.setId(id);
    solicitudProyectoSocioPeriodoJustificacion
        .setSolicitudProyectoSocioId(solicitudProyectoId == null ? 1 : solicitudProyectoId);
    solicitudProyectoSocioPeriodoJustificacion.setNumPeriodo(1);
    solicitudProyectoSocioPeriodoJustificacion.setMesInicial(mesInicial);
    solicitudProyectoSocioPeriodoJustificacion.setMesFinal(mesFinal);
    solicitudProyectoSocioPeriodoJustificacion.setFechaInicio(Instant.parse("2020-10-10T00:00:00Z"));
    solicitudProyectoSocioPeriodoJustificacion.setFechaFin(Instant.parse("2020-11-20T23:59:59Z"));
    solicitudProyectoSocioPeriodoJustificacion.setObservaciones("observaciones-" + id);

    return solicitudProyectoSocioPeriodoJustificacion;
  }

}
