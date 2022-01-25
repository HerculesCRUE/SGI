package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.controller.ResultadoInformePatentabilidadController;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad;
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
 * Test de integracion de ResultadoInformePatentabilidad.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResultadoInformePatentabilidadIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = ResultadoInformePatentabilidadController.MAPPING;
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_ACTIVAR = "/activar";
  private static final String PATH_DESACTIVAR = "/desactivar";

  private HttpEntity<ResultadoInformePatentabilidad> buildRequest(HttpHeaders headers,
      ResultadoInformePatentabilidad entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ResultadoInformePatentabilidad> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/resultado_informe_patentabilidad.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsResultadoInformePatentabilidad() throws Exception {

    String[] roles = { "PII-RIP-C" };
    ResultadoInformePatentabilidad nuevoResultadoInformePatentabilidad = generarMockResultadoInformePatentabilidad(
        null);

    final ResponseEntity<ResultadoInformePatentabilidad> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, nuevoResultadoInformePatentabilidad, roles), ResultadoInformePatentabilidad.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final ResultadoInformePatentabilidad resultado = response.getBody();
    Assertions.assertThat(resultado.getId()).as("id").isEqualTo(5L);
    Assertions.assertThat(resultado.getNombre()).as("nombre")
        .isEqualTo(nuevoResultadoInformePatentabilidad.getNombre());
    Assertions.assertThat(resultado.getDescripcion()).as("descripcion")
        .isEqualTo(nuevoResultadoInformePatentabilidad.getDescripcion());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/resultado_informe_patentabilidad.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsResultadoInformePatentabilidad() throws Exception {

    String[] roles = { "PII-RIP-E" };
    Long idResultadoInformePatentabilidad = 1L;

    ResultadoInformePatentabilidad resultadoInformePatentabilidad = generarMockResultadoInformePatentabilidad(
        idResultadoInformePatentabilidad);
    resultadoInformePatentabilidad.setDescripcion("descripcion-modificada");

    final ResponseEntity<ResultadoInformePatentabilidad> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null,
            resultadoInformePatentabilidad, roles),
        ResultadoInformePatentabilidad.class,
        idResultadoInformePatentabilidad);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final ResultadoInformePatentabilidad resultado = response.getBody();
    Assertions.assertThat(resultado.getId()).as("id").isEqualTo(1L);
    Assertions.assertThat(resultado.getNombre()).as("nombre").isEqualTo(resultadoInformePatentabilidad.getNombre());
    Assertions.assertThat(resultado.getDescripcion()).as("descripcion")
        .isEqualTo(resultadoInformePatentabilidad.getDescripcion());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/resultado_informe_patentabilidad.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsResultadoInformePatentabilidadSubList() throws Exception {

    String[] roles = { "PII-RIP-V", "PII-RIP-C", "PII-RIP-E", "PII-RIP-B", "PII-RIP-R" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false)
        .toUri();

    final ResponseEntity<List<ResultadoInformePatentabilidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ResultadoInformePatentabilidad>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ResultadoInformePatentabilidad> resultadoInformePatentabilidad = response.getBody();
    Assertions.assertThat(resultadoInformePatentabilidad.size()).isEqualTo(4);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("4");

    Assertions.assertThat(resultadoInformePatentabilidad.get(0).getNombre())
        .isEqualTo("nombre-resultado-informe-" + String.format("%03d", 4));
    Assertions.assertThat(resultadoInformePatentabilidad.get(1).getNombre())
        .isEqualTo("nombre-resultado-informe-" + String.format("%03d", 3));
    Assertions.assertThat(resultadoInformePatentabilidad.get(2).getNombre())
        .isEqualTo("nombre-resultado-informe-" + String.format("%03d", 2));
    Assertions.assertThat(resultadoInformePatentabilidad.get(3).getNombre())
        .isEqualTo("nombre-resultado-informe-" + String.format("%03d", 1));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/resultado_informe_patentabilidad.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findActivos_WithPagingSortingAndFiltering_ReturnsResultadoInformePatentabilidadSubList()
      throws Exception {
    String[] roles = { "PII-RIP-V", "PII-RIP-C", "PII-RIP-E", "PII-RIP-B", "PII-RIP-R", "PII-INV-V", "PII-INV-E" };

    // when: Obtiene la page=0 con pagesize=2
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false)
        .toUri();

    final ResponseEntity<List<ResultadoInformePatentabilidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ResultadoInformePatentabilidad>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ResultadoInformePatentabilidad> resultadoInformePatentabilidad = response.getBody();
    Assertions.assertThat(resultadoInformePatentabilidad.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    Assertions.assertThat(resultadoInformePatentabilidad.get(0).getActivo()).isEqualTo(Boolean.TRUE);
    Assertions.assertThat(resultadoInformePatentabilidad.get(1).getActivo()).isEqualTo(Boolean.TRUE);
    Assertions.assertThat(resultadoInformePatentabilidad.get(2).getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/resultado_informe_patentabilidad.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsResultadoInformePatentabilidad() throws Exception {
    String[] roles = { "PII-RIP-V", "PII-RIP-C", "PII-RIP-E", "PII-RIP-B", "PII-RIP-R" };
    Long resultadoInformeId = 1L;

    final ResponseEntity<ResultadoInformePatentabilidad> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null, roles),
        ResultadoInformePatentabilidad.class,
        resultadoInformeId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ResultadoInformePatentabilidad resultadoInformePatentabilidad = response.getBody();

    Assertions.assertThat(resultadoInformePatentabilidad).isNotNull();
    Assertions.assertThat(resultadoInformePatentabilidad.getId()).as("id")
        .isEqualTo(1);
    Assertions.assertThat(resultadoInformePatentabilidad.getNombre()).as("nombre")
        .isEqualTo("nombre-resultado-informe-001");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/resultado_informe_patentabilidad.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void activar_ReturnResultadoInformePatentabilidadActivo() throws Exception {
    String[] roles = { "PII-RIP-R" };
    Long resultadoInformeId = 4L;

    final ResponseEntity<ResultadoInformePatentabilidad> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ACTIVAR, HttpMethod.PATCH, buildRequest(null, null, roles),
        ResultadoInformePatentabilidad.class,
        resultadoInformeId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ResultadoInformePatentabilidad resultadoInformePatentabilidad = response.getBody();

    Assertions.assertThat(resultadoInformePatentabilidad).isNotNull();
    Assertions.assertThat(resultadoInformePatentabilidad.getId()).as("id")
        .isEqualTo(4);
    Assertions.assertThat(resultadoInformePatentabilidad.getActivo()).as("activo")
        .isEqualTo(Boolean.TRUE);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/resultado_informe_patentabilidad.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnsResultadoInformePatentabilidadDesactivado() throws Exception {
    String[] roles = { "PII-RIP-B" };
    Long resultadoInformeId = 1L;

    final ResponseEntity<ResultadoInformePatentabilidad> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DESACTIVAR, HttpMethod.PATCH, buildRequest(null, null, roles),
        ResultadoInformePatentabilidad.class,
        resultadoInformeId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ResultadoInformePatentabilidad resultadoInformePatentabilidad = response.getBody();

    Assertions.assertThat(resultadoInformePatentabilidad).isNotNull();
    Assertions.assertThat(resultadoInformePatentabilidad.getId()).as("id")
        .isEqualTo(1);
    Assertions.assertThat(resultadoInformePatentabilidad.getActivo()).as("activo")
        .isEqualTo(Boolean.FALSE);
  }

  /**
   * Función que devuelve un objeto ResultadoInformePatentabilidad
   * 
   * @param id id del ResultadoInformePatentabilidad
   * @return el objeto ResultadoInformePatentabilidad
   */
  private ResultadoInformePatentabilidad generarMockResultadoInformePatentabilidad(Long id) {
    ResultadoInformePatentabilidad resultadoInformePatentabilidad = new ResultadoInformePatentabilidad();
    resultadoInformePatentabilidad.setId(id);
    resultadoInformePatentabilidad.setNombre("nombre-resultado-" + String.format("%03d", id));
    resultadoInformePatentabilidad.setDescripcion("descripcion-resultado-" + String.format("%03d", id));

    return resultadoInformePatentabilidad;
  }

}
