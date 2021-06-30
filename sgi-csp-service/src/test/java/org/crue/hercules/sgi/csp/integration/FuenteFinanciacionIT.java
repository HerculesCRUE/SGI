package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.controller.FuenteFinanciacionController;
import org.crue.hercules.sgi.csp.dto.FuenteFinanciacionInput;
import org.crue.hercules.sgi.csp.dto.FuenteFinanciacionOutput;
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
 * Test de integracion de FuenteFinanciacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FuenteFinanciacionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_ACTIVAR = "/activar";
  private static final String CONTROLLER_BASE_PATH = FuenteFinanciacionController.MAPPING;

  private HttpEntity<FuenteFinanciacionInput> buildRequest(HttpHeaders headers, FuenteFinanciacionInput entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", "CSP-CON-V", "CSP-CON-E", "CSP-CON-C", "CSP-CON-INV-V", "CSP-SOL-V",
                "CSP-SOL-C", "CSP-SOL-E", "CSP-SOL-B", "CSP-PRO-C", "CSP-SOL-R", "CSP-PRO-V", "CSP-PRO-E", "CSP-PRO-B",
                "CSP-PRO-R", "CSP-FNT-V", "CSP-FNT-C", "CSP-FNT-E", "CSP-FNT-B", "CSP-FNT-R", "AUTH")));

    HttpEntity<FuenteFinanciacionInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsFuenteFinanciacion() throws Exception {
    FuenteFinanciacionInput fuenteFinanciacion = generarMockFuenteFinanciacionInput();

    final ResponseEntity<FuenteFinanciacionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, fuenteFinanciacion), FuenteFinanciacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    FuenteFinanciacionOutput fuenteFinanciacionCreado = response.getBody();
    Assertions.assertThat(fuenteFinanciacionCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(fuenteFinanciacionCreado.getNombre()).as("getNombre()")
        .isEqualTo(fuenteFinanciacion.getNombre());
    Assertions.assertThat(fuenteFinanciacionCreado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(fuenteFinanciacion.getDescripcion());
    Assertions.assertThat(fuenteFinanciacionCreado.getFondoEstructural()).as("getFondoEstructural()")
        .isEqualTo(fuenteFinanciacion.getFondoEstructural());
    Assertions.assertThat(fuenteFinanciacionCreado.getTipoAmbitoGeografico().getId())
        .as("getTipoAmbitoGeografico().getId()").isEqualTo(fuenteFinanciacion.getTipoAmbitoGeograficoId());
    Assertions.assertThat(fuenteFinanciacionCreado.getTipoOrigenFuenteFinanciacion().getId())
        .as("getTipoOrigenFuenteFinanciacion().getId()")
        .isEqualTo(fuenteFinanciacion.getTipoOrigenFuenteFinanciacionId());
    Assertions.assertThat(fuenteFinanciacionCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsFuenteFinanciacion() throws Exception {
    Long idFuenteFinanciacion = 1L;
    FuenteFinanciacionInput fuenteFinanciacion = generarMockFuenteFinanciacionInput();
    fuenteFinanciacion.setNombre("nombre-actualizado");

    final ResponseEntity<FuenteFinanciacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, fuenteFinanciacion),
        FuenteFinanciacionOutput.class, idFuenteFinanciacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FuenteFinanciacionOutput fuenteFinanciacionActualizado = response.getBody();
    Assertions.assertThat(fuenteFinanciacionActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(fuenteFinanciacionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(fuenteFinanciacion.getNombre());
    Assertions.assertThat(fuenteFinanciacionActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(fuenteFinanciacion.getDescripcion());
    Assertions.assertThat(fuenteFinanciacionActualizado.getFondoEstructural()).as("getFondoEstructural()")
        .isEqualTo(fuenteFinanciacion.getFondoEstructural());
    Assertions.assertThat(fuenteFinanciacionActualizado.getTipoAmbitoGeografico().getId())
        .as("getTipoAmbitoGeografico().getId()").isEqualTo(fuenteFinanciacion.getTipoAmbitoGeograficoId());
    Assertions.assertThat(fuenteFinanciacionActualizado.getTipoOrigenFuenteFinanciacion().getId())
        .as("getTipoOrigenFuenteFinanciacion().getId()")
        .isEqualTo(fuenteFinanciacion.getTipoOrigenFuenteFinanciacionId());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnFuenteFinanciacion() throws Exception {
    Long idFuenteFinanciacion = 1L;

    final ResponseEntity<FuenteFinanciacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), FuenteFinanciacionOutput.class, idFuenteFinanciacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FuenteFinanciacionOutput fuenteFinanciacion = response.getBody();
    Assertions.assertThat(fuenteFinanciacion.getId()).as("getId()").isNotNull();
    Assertions.assertThat(fuenteFinanciacion.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(fuenteFinanciacion.getDescripcion()).as("descripcion-001")
        .isEqualTo(fuenteFinanciacion.getDescripcion());
    Assertions.assertThat(fuenteFinanciacion.getFondoEstructural()).as("getFondoEstructural()").isEqualTo(true);
    Assertions.assertThat(fuenteFinanciacion.getTipoAmbitoGeografico().getId()).as("getTipoAmbitoGeografico().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId())
        .as("getTipoOrigenFuenteFinanciacion().getId()").isEqualTo(1L);
    Assertions.assertThat(fuenteFinanciacion.getActivo()).as("getActivo()").isEqualTo(false);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void activar_ReturnFuenteFinanciacion() throws Exception {
    Long idFuenteFinanciacion = 1L;

    final ResponseEntity<FuenteFinanciacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_ACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        FuenteFinanciacionOutput.class, idFuenteFinanciacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FuenteFinanciacionOutput fuenteFinanciacion = response.getBody();
    Assertions.assertThat(fuenteFinanciacion.getId()).as("getId()").isNotNull();
    Assertions.assertThat(fuenteFinanciacion.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(fuenteFinanciacion.getDescripcion()).as("descripcion-001")
        .isEqualTo(fuenteFinanciacion.getDescripcion());
    Assertions.assertThat(fuenteFinanciacion.getFondoEstructural()).as("getFondoEstructural()").isEqualTo(true);
    Assertions.assertThat(fuenteFinanciacion.getTipoAmbitoGeografico().getId()).as("getTipoAmbitoGeografico().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId())
        .as("getTipoOrigenFuenteFinanciacion().getId()").isEqualTo(1L);
    Assertions.assertThat(fuenteFinanciacion.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsFuenteFinanciacion() throws Exception {
    Long idFuenteFinanciacion = 1L;

    final ResponseEntity<FuenteFinanciacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        FuenteFinanciacionOutput.class, idFuenteFinanciacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    FuenteFinanciacionOutput fuenteFinanciacion = response.getBody();
    Assertions.assertThat(fuenteFinanciacion.getId()).as("getId()").isNotNull();
    Assertions.assertThat(fuenteFinanciacion.getNombre()).as("getNombre()").isEqualTo("nombre-001");
    Assertions.assertThat(fuenteFinanciacion.getDescripcion()).as("descripcion-001")
        .isEqualTo(fuenteFinanciacion.getDescripcion());
    Assertions.assertThat(fuenteFinanciacion.getFondoEstructural()).as("getFondoEstructural()").isEqualTo(true);
    Assertions.assertThat(fuenteFinanciacion.getTipoAmbitoGeografico().getId()).as("getTipoAmbitoGeografico().getId()")
        .isEqualTo(1L);
    Assertions.assertThat(fuenteFinanciacion.getTipoOrigenFuenteFinanciacion().getId())
        .as("getTipoOrigenFuenteFinanciacion().getId()").isEqualTo(1L);
    Assertions.assertThat(fuenteFinanciacion.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsFuenteFinanciacionSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<FuenteFinanciacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<FuenteFinanciacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<FuenteFinanciacionOutput> fuenteFinanciaciones = response.getBody();
    Assertions.assertThat(fuenteFinanciaciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(fuenteFinanciaciones.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(fuenteFinanciaciones.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(fuenteFinanciaciones.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsFuenteFinanciacionSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-TDOC-V")));
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<FuenteFinanciacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<FuenteFinanciacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<FuenteFinanciacionOutput> fuenteFinanciaciones = response.getBody();
    Assertions.assertThat(fuenteFinanciaciones.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(fuenteFinanciaciones.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(fuenteFinanciaciones.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(fuenteFinanciaciones.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  private FuenteFinanciacionInput generarMockFuenteFinanciacionInput() {
    FuenteFinanciacionInput fuenteFinanciacion = new FuenteFinanciacionInput();
    fuenteFinanciacion.setNombre("nombre");
    fuenteFinanciacion.setDescripcion("descripcion");
    fuenteFinanciacion.setFondoEstructural(true);
    fuenteFinanciacion.setTipoAmbitoGeograficoId(1L);
    fuenteFinanciacion.setTipoOrigenFuenteFinanciacionId(1L);
    return fuenteFinanciacion;
  }

}
