package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
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
 * Test de integracion de RolProyecto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RolProyectoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/rolproyectos";
  private static final String PATH_PRINCIPAL = "/principal";
  private static final String PATH_COLECTIVOS = "/colectivos";

  private HttpEntity<RolProyecto> buildRequest(HttpHeaders headers, RolProyecto entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", "CSP-ROLE-E", "CSP-ROLE-V", "CSP-ROLE-C", "CSP-ROLE-R", "CSP-PRO-C",
                "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-B", "CSP-PRO-R", "CSP-SOL-INV-ER")));

    HttpEntity<RolProyecto> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsRolProyecto() throws Exception {
    Long id = 1L;

    final ResponseEntity<RolProyecto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), RolProyecto.class, id);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    RolProyecto responseData = response.getBody();
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()").isEqualTo("001");
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-001");
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()").isEqualTo(Boolean.FALSE);
    Assertions.assertThat(responseData.getOrden()).as("getOrden()").isEqualTo(null);
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(RolProyecto.Equipo.INVESTIGACION);
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsRolProyectoSubList() throws Exception {

    // given: data for RolProyecto

    // first page, 3 elements per page sorted by nombre desc
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-PRO-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "3");
    String sort = "id,desc";
    String filter = "descripcion=ke=00";

    // when: find RolProyecto
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();
    final ResponseEntity<List<RolProyecto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<RolProyecto>>() {
        });

    // given: RolProyecto data filtered and sorted
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<RolProyecto> responseData = response.getBody();
    Assertions.assertThat(responseData).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("3");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(responseData.get(0).getDescripcion()).as("get(0).getDescripcion())")
        .isEqualTo("descripcion-" + String.format("%03d", 3));
    Assertions.assertThat(responseData.get(1).getDescripcion()).as("get(1).getDescripcion())")
        .isEqualTo("descripcion-" + String.format("%03d", 2));
    Assertions.assertThat(responseData.get(2).getDescripcion()).as("get(2).getDescripcion())")
        .isEqualTo("descripcion-" + String.format("%03d", 1));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/rol_proyecto.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findPrincipal_ReturnRolProyecto() throws Exception {
    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PRINCIPAL).build().toUri();

    final ResponseEntity<RolProyecto> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        RolProyecto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    RolProyecto rol = response.getBody();
    Assertions.assertThat(rol).isNotNull();
    Assertions.assertThat(rol.getRolPrincipal()).isTrue();
    Assertions.assertThat(rol.getActivo()).isTrue();
    Assertions.assertThat(rol.getId()).isEqualTo(1L);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/rol_proyecto.sql",
    "classpath:scripts/rol_proyecto_colectivo.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllColectivos_ReturnsStringList() throws Exception {
    Long rolId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_COLECTIVOS)
        .buildAndExpand(rolId)
        .toUri();

    final ResponseEntity<List<String>> response = restTemplate.exchange(uri, HttpMethod.GET, buildRequest(null, null),
        new ParameterizedTypeReference<List<String>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<String> colectivos = response.getBody();

    Assertions.assertThat(colectivos.isEmpty()).isFalse();
    Assertions.assertThat(colectivos).hasSize(2);

  }

}
