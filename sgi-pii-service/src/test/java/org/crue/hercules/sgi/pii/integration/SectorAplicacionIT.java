package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.SectorAplicacionInput;
import org.crue.hercules.sgi.pii.dto.SectorAplicacionOutput;
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
 * Test de integracion de SectorAplicacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectorAplicacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/sectoresaplicacion";
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_ACTIVAR = "/activar";
  private static final String PATH_DESACTIVAR = "/desactivar";

  private HttpEntity<SectorAplicacionInput> buildRequest(HttpHeaders headers,
      SectorAplicacionInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<SectorAplicacionInput> request = new HttpEntity<>(entity, headers);

    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/sector_aplicacion.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findActivos_WithPagingSortingAndFiltering_ReturnSectorAplicacionOutputSubList() throws Exception {

    String[] roles = { "PII-SEA-V", "PII-SEA-C", "PII-SEA-E", "PII-SEA-B", "PII-SEA-R", "PII-INV-V", "PII-INV-C",
        "PII-INV-E", "PII-INV-B", "PII-INV-R" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "nombre=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<SectorAplicacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<SectorAplicacionOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SectorAplicacionOutput> sectorAplicacionOutput = response.getBody();
    Assertions.assertThat(sectorAplicacionOutput.size()).isEqualTo(2);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("2");

    Assertions.assertThat(sectorAplicacionOutput.get(0).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(sectorAplicacionOutput.get(1).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/sector_aplicacion.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnSectorAplicacionOutputSubList() throws Exception {

    String[] roles = { "PII-SEA-V", "PII-SEA-C", "PII-SEA-E", "PII-SEA-B", "PII-SEA-R" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "nombre=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<SectorAplicacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<SectorAplicacionOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SectorAplicacionOutput> sectorAplicacionOutput = response.getBody();
    Assertions.assertThat(sectorAplicacionOutput.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    Assertions.assertThat(sectorAplicacionOutput.get(0).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(sectorAplicacionOutput.get(1).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(sectorAplicacionOutput.get(2).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/sector_aplicacion.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnSectorAplicacionOutput() throws Exception {

    String[] roles = { "PII-SEA-V", "PII-SEA-C", "PII-SEA-E", "PII-SEA-B", "PII-SEA-R" };
    Long sectorAplicacionId = 1L;

    final ResponseEntity<SectorAplicacionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH
        + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null, roles), SectorAplicacionOutput.class, sectorAplicacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final SectorAplicacionOutput sectorAplicacionOutput = response.getBody();
    Assertions.assertThat(sectorAplicacionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(sectorAplicacionOutput.getNombre()).as("nombre").isEqualTo("nombre-sector-aplicacion-001");
    Assertions.assertThat(sectorAplicacionOutput.getDescripcion()).as("descripcion")
        .isEqualTo("descripcion-sector-aplicacion-001");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/sector_aplicacion.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnSectorAplicacionOutput() throws Exception {

    String[] roles = { "PII-SEA-C" };
    SectorAplicacionInput sectorAplicacionInput = generaMockSectorAplicacionInput();

    final ResponseEntity<SectorAplicacionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, sectorAplicacionInput, roles), SectorAplicacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final SectorAplicacionOutput sectorAplicacionOutput = response.getBody();
    Assertions.assertThat(sectorAplicacionOutput.getId()).as("id").isEqualTo(4);
    Assertions.assertThat(sectorAplicacionOutput.getNombre()).as("nombre").isEqualTo("nombre-sector-aplicacion");
    Assertions.assertThat(sectorAplicacionOutput.getDescripcion()).as("descripcion")
        .isEqualTo("descripcion-sector-aplicacion");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/sector_aplicacion.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnSectorAplicacionOutput() throws Exception {

    String[] roles = { "PII-SEA-E" };
    Long sectorAplicacionId = 1L;

    SectorAplicacionInput sectorAplicacionInput = generaMockSectorAplicacionInput();
    sectorAplicacionInput.setDescripcion("descripcion-sector-aplicacion-modificado");

    final ResponseEntity<SectorAplicacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT,
        buildRequest(null, sectorAplicacionInput, roles), SectorAplicacionOutput.class, sectorAplicacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final SectorAplicacionOutput sectorAplicacionOutput = response.getBody();
    Assertions.assertThat(sectorAplicacionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(sectorAplicacionOutput.getNombre()).as("nombre").isEqualTo("nombre-sector-aplicacion");
    Assertions.assertThat(sectorAplicacionOutput.getDescripcion()).as("descripcion")
        .isEqualTo("descripcion-sector-aplicacion-modificado");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/sector_aplicacion.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void activar_ReturnSectorAplicacionOutput() throws Exception {

    String[] roles = { "PII-SEA-R" };
    Long sectorAplicacionId = 3L;

    final ResponseEntity<SectorAplicacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null, roles), SectorAplicacionOutput.class, sectorAplicacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final SectorAplicacionOutput sectorAplicacionOutput = response.getBody();
    Assertions.assertThat(sectorAplicacionOutput.getId()).as("id").isEqualTo(3);
    Assertions.assertThat(sectorAplicacionOutput.getActivo()).as("activo")
        .isEqualTo(Boolean.TRUE);
    Assertions.assertThat(sectorAplicacionOutput.getNombre()).as("nombre").isEqualTo("nombre-sector-aplicacion-003");
    Assertions.assertThat(sectorAplicacionOutput.getDescripcion()).as("descripcion")
        .isEqualTo("descripcion-sector-aplicacion-003");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/sector_aplicacion.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnSectorAplicacionOutput() throws Exception {

    String[] roles = { "PII-SEA-B" };
    Long sectorAplicacionId = 1L;

    final ResponseEntity<SectorAplicacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null, roles), SectorAplicacionOutput.class, sectorAplicacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final SectorAplicacionOutput sectorAplicacionOutput = response.getBody();
    Assertions.assertThat(sectorAplicacionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(sectorAplicacionOutput.getActivo()).as("activo")
        .isEqualTo(Boolean.FALSE);
    Assertions.assertThat(sectorAplicacionOutput.getNombre()).as("nombre").isEqualTo("nombre-sector-aplicacion-001");
    Assertions.assertThat(sectorAplicacionOutput.getDescripcion()).as("descripcion")
        .isEqualTo("descripcion-sector-aplicacion-001");

  }

  /**
   * Función que devuelve un objeto SectorAplicacionInput
   * 
   * @return el objeto SectorAplicacionInput
   */
  private SectorAplicacionInput generaMockSectorAplicacionInput() {
    SectorAplicacionInput sectorAplicacionInput = new SectorAplicacionInput();
    sectorAplicacionInput.setNombre("nombre-sector-aplicacion");
    sectorAplicacionInput.setDescripcion("descripcion-sector-aplicacion");

    return sectorAplicacionInput;
  }
}
