package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
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
 * Test de integracion de SolicitudProyectoSocioEquipo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitudProyectoSocioEquipoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/solicitudproyectosocioequipo";

  private HttpEntity<SolicitudProyectoSocioEquipo> buildRequest(HttpHeaders headers,
      SolicitudProyectoSocioEquipo entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-C", "CSP-SOL-E")));

    HttpEntity<SolicitudProyectoSocioEquipo> request = new HttpEntity<>(entity, headers);
    return request;

  }

  private HttpEntity<List<SolicitudProyectoSocioEquipo>> buildRequestList(HttpHeaders headers,
      List<SolicitudProyectoSocioEquipo> entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-SOL-E", "CSP-SOL-C")));

    HttpEntity<List<SolicitudProyectoSocioEquipo>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsListSolicitudProyectoSocioEquipo() throws Exception {

    // given: una lista con uno de los SolicitudProyectoSocioEquipo actualizado,
    // otro nuevo y sin los otros 3 proyecto equipo socio existentes
    Long solicitudProyectoSocioId = 1L;

    SolicitudProyectoSocioEquipo newsolicitudProyectoEquipoSocio = generarSolicitudProyectoSocioEquipo(null, 1L);

    SolicitudProyectoSocioEquipo updateSolicitudProyectoSocioEquipo = generarSolicitudProyectoSocioEquipo(103L, 1L);
    updateSolicitudProyectoSocioEquipo.setPersonaRef("user-002");
    updateSolicitudProyectoSocioEquipo.setMesInicio(3);
    updateSolicitudProyectoSocioEquipo.setMesFin(4);

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocioUpdate = new ArrayList<SolicitudProyectoSocioEquipo>();
    solicitudProyectoEquipoSocioUpdate.add(newsolicitudProyectoEquipoSocio);
    solicitudProyectoEquipoSocioUpdate.add(updateSolicitudProyectoSocioEquipo);

    // when: updateSolicitudProyectoSocioEquipo
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(solicitudProyectoSocioId).toUri();

    final ResponseEntity<List<SolicitudProyectoSocioEquipo>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequestList(null, solicitudProyectoEquipoSocioUpdate),
        new ParameterizedTypeReference<List<SolicitudProyectoSocioEquipo>>() {
        });

    // then: Se crea el nuevo SolicitudProyectoSocioEquipo, se actualiza el
    // existente y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<SolicitudProyectoSocioEquipo> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(2);

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsSolicitudProyectoSocioEquipo() throws Exception {
    Long idSolicitudProyectoSocioEquipo = 1L;

    final ResponseEntity<SolicitudProyectoSocioEquipo> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        SolicitudProyectoSocioEquipo.class, idSolicitudProyectoSocioEquipo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    SolicitudProyectoSocioEquipo solicitudProyectoSocioEquipo = response.getBody();
    Assertions.assertThat(solicitudProyectoSocioEquipo.getId()).as("getId()").isEqualTo(idSolicitudProyectoSocioEquipo);
    Assertions.assertThat(solicitudProyectoSocioEquipo.getRolProyecto().getId()).as("getRolProyecto().getId()")
        .isEqualTo(1);
    Assertions.assertThat(solicitudProyectoSocioEquipo.getPersonaRef()).as("getPersonaRef()").isEqualTo("user-001");
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioEquipo
   * 
   * @param solicitudProyectoEquipoSocioId
   * @param entidadesRelacionadasId
   * @return el objeto SolicitudProyectoSocioEquipo
   */
  private SolicitudProyectoSocioEquipo generarSolicitudProyectoSocioEquipo(Long solicitudProyectoEquipoSocioId,
      Long entidadesRelacionadasId) {

    SolicitudProyectoSocioEquipo solicitudProyectoSocioEquipo = SolicitudProyectoSocioEquipo.builder()
        .id(solicitudProyectoEquipoSocioId).solicitudProyectoSocioId(entidadesRelacionadasId)
        .rolProyecto(RolProyecto.builder().id(entidadesRelacionadasId).build())
        .personaRef("user-" + solicitudProyectoEquipoSocioId).mesInicio(1).mesFin(2).build();

    return solicitudProyectoSocioEquipo;
  }

}
