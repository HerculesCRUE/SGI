package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
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
 * Test de integracion de ProyectoSocioEquipo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProyectoSocioEquipoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/proyectosocioequipos";

  private HttpEntity<ProyectoSocioEquipo> buildRequest(HttpHeaders headers, ProyectoSocioEquipo entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "AUTH")));

    HttpEntity<ProyectoSocioEquipo> request = new HttpEntity<>(entity, headers);
    return request;
  }

  private HttpEntity<List<ProyectoSocioEquipo>> buildRequestList(HttpHeaders headers, List<ProyectoSocioEquipo> entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-E")));

    HttpEntity<List<ProyectoSocioEquipo>> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/rol_socio.sql", "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_socio.sql", "classpath:scripts/proyecto_socio_equipo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsProyectoSocioEquipoList() throws Exception {

    // given: una lista con uno de los ProyectoSocioEquipo actualizado,
    // otro nuevo y sin los otros 3 periodos existentes
    Long proyectoSocioId = 1L;
    ProyectoSocioEquipo updatedProyectoSocioEquipo = generarMockProyectoSocioEquipo(3L);
    updatedProyectoSocioEquipo.setProyectoSocioId(1L);

    List<ProyectoSocioEquipo> proyectoSocioEquipos = new ArrayList<ProyectoSocioEquipo>();
    proyectoSocioEquipos.add(updatedProyectoSocioEquipo);

    // when: updateProyectoSocioEquipo
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID)
        .buildAndExpand(proyectoSocioId).toUri();

    final ResponseEntity<List<ProyectoSocioEquipo>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequestList(null, proyectoSocioEquipos), new ParameterizedTypeReference<List<ProyectoSocioEquipo>>() {
        });

    // then: Se crea el nuevo ProyectoSocioEquipo, se actualiza el
    // existe y se eliminan los otros
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    List<ProyectoSocioEquipo> responseData = response.getBody();
    Assertions.assertThat(responseData.get(0).getId()).as("get(0).getId()")
        .isEqualTo(updatedProyectoSocioEquipo.getId());
    Assertions.assertThat(responseData.get(0).getProyectoSocioId()).as("get(0).getProyectoSocioId()")
        .isEqualTo(proyectoSocioId);
    Assertions.assertThat(responseData.get(0).getFechaInicio()).as("get(0).getFechaInicio()")
        .isEqualTo(updatedProyectoSocioEquipo.getFechaInicio());
    Assertions.assertThat(responseData.get(0).getFechaFin()).as("get(0).getFechaFin()")
        .isEqualTo(updatedProyectoSocioEquipo.getFechaFin());
    Assertions.assertThat(responseData.get(0).getPersonaRef()).as("get(0).getPersonaRef()").isEqualTo("001");
    Assertions.assertThat(responseData.get(0).getRolProyecto().getId()).as("get(0).getRolProyecto().getId()")
        .isEqualTo(updatedProyectoSocioEquipo.getRolProyecto().getId());

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "fechaInicio,asc";

    URI uriFindAllProyectoSocioEquipo = UriComponentsBuilder
        .fromUriString("/proyectosocios" + PATH_PARAMETER_ID + CONTROLLER_BASE_PATH).queryParam("s", sort)
        .buildAndExpand(proyectoSocioId).toUri();

    final ResponseEntity<List<ProyectoSocioEquipo>> responseFindAllProyectoSocioEquipo = restTemplate.exchange(
        uriFindAllProyectoSocioEquipo, HttpMethod.GET, buildRequestList(headers, null),
        new ParameterizedTypeReference<List<ProyectoSocioEquipo>>() {
        });

    Assertions.assertThat(responseFindAllProyectoSocioEquipo.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ProyectoSocioEquipo> responseDataFindAll = responseFindAllProyectoSocioEquipo.getBody();
    Assertions.assertThat(responseDataFindAll.size()).as("size()").isEqualTo(proyectoSocioEquipos.size());
    Assertions.assertThat(responseDataFindAll.get(0).getId()).as("responseDataFindAll.get(0).getId()")
        .isEqualTo(responseData.get(0).getId());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = { "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql", "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql", "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql", "classpath:scripts/rol_socio.sql", "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/proyecto_socio.sql", "classpath:scripts/proyecto_socio_equipo.sql" })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsProyectoSocioEquipo() throws Exception {
    Long idProyectoSocioEquipo = 1L;

    final ResponseEntity<ProyectoSocioEquipo> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ProyectoSocioEquipo.class, idProyectoSocioEquipo);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ProyectoSocioEquipo proyectoSocioEquipo = response.getBody();
    Assertions.assertThat(proyectoSocioEquipo.getId()).as("getId()").isEqualTo(idProyectoSocioEquipo);
    Assertions.assertThat(proyectoSocioEquipo.getProyectoSocioId()).as("getProyectoSocioId()").isEqualTo(1L);
    Assertions.assertThat(proyectoSocioEquipo.getFechaInicio()).as("getFechaInicio()")
        .isEqualTo(Instant.parse("2021-01-11T00:00:00Z"));
    Assertions.assertThat(proyectoSocioEquipo.getFechaFin()).as("getFechaFin()")
        .isEqualTo(Instant.parse("2022-01-11T23:59:59Z"));
    Assertions.assertThat(proyectoSocioEquipo.getPersonaRef()).as("getPersonaRef()").isEqualTo("personaRef-001");
    Assertions.assertThat(proyectoSocioEquipo.getRolProyecto().getId()).as("getRolProyecto()").isEqualTo(1);

  }

  /**
   * Función que genera un ProyectoSocioEquipo
   * 
   * @param id Identificador
   * @return el ProyectoSocioEquipo
   */
  private ProyectoSocioEquipo generarMockProyectoSocioEquipo(Long id) {

    // @formatter:off
    ProyectoSocioEquipo proyectoSocioEquipo = ProyectoSocioEquipo.builder()
      .id(id)
      .proyectoSocioId(1L)
      .rolProyecto(
        RolProyecto.builder()
          .id(1L)
          .abreviatura("001")
          .nombre("rolProyecto1")
          .equipo(RolProyecto.Equipo.INVESTIGACION)
          .activo(Boolean.TRUE)
          .build()
      )
      .personaRef("001")
      .fechaInicio(Instant.parse("2021-04-10T00:00:00Z"))
    .build();
    // @formatter:on

    return proyectoSocioEquipo;
  }

}
