package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
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
 * Test de integracion de ModeloEjecucion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ModeloEjecucionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String MODELO_EJECUCION_CONTROLLER_BASE_PATH = "/modeloejecuciones";

  private HttpEntity<ModeloEjecucion> buildRequest(HttpHeaders headers, ModeloEjecucion entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", "CSP-ME-V", "CSP-ME-C", "CSP-ME-E", "CSP-ME-B", "CSP-ME-R", "CSP-PRO-C",
                "CSP-CON-C", "CSP-CON-E", "CSP-CON-V", "CSP-PRO-E", "CSP-SOL-E", "CSP-SOL-V", "AUTH")));

    HttpEntity<ModeloEjecucion> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void create_ReturnsModeloEjecucion() throws Exception {

    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(null);

    final ResponseEntity<ModeloEjecucion> response = restTemplate.exchange(MODELO_EJECUCION_CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, modeloEjecucion), ModeloEjecucion.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ModeloEjecucion modeloEjecucionCreado = response.getBody();
    Assertions.assertThat(modeloEjecucionCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(modeloEjecucionCreado.getNombre()).as("getNombre()").isEqualTo(modeloEjecucion.getNombre());
    Assertions.assertThat(modeloEjecucionCreado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(modeloEjecucion.getDescripcion());
    Assertions.assertThat(modeloEjecucionCreado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void update_ReturnsModeloEjecucion() throws Exception {
    Long idModeloEjecucion = 1L;
    ModeloEjecucion modeloEjecucion = generarMockModeloEjecucion(idModeloEjecucion, "nombre-actualizado");

    final ResponseEntity<ModeloEjecucion> response = restTemplate.exchange(
        MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, modeloEjecucion),
        ModeloEjecucion.class, idModeloEjecucion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ModeloEjecucion modeloEjecucionActualizado = response.getBody();
    Assertions.assertThat(modeloEjecucionActualizado.getId()).as("getId()").isEqualTo(modeloEjecucion.getId());
    Assertions.assertThat(modeloEjecucionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(modeloEjecucion.getNombre());
    Assertions.assertThat(modeloEjecucionActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(modeloEjecucion.getDescripcion());
    Assertions.assertThat(modeloEjecucionActualizado.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void reactivar_ReturnModeloEjecucion() throws Exception {
    Long idModeloEjecucion = 1L;

    final ResponseEntity<ModeloEjecucion> response = restTemplate.exchange(
        MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), ModeloEjecucion.class, idModeloEjecucion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloEjecucion modeloEjecucionDisabled = response.getBody();
    Assertions.assertThat(modeloEjecucionDisabled.getId()).as("getId()").isEqualTo(idModeloEjecucion);
    Assertions.assertThat(modeloEjecucionDisabled.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(modeloEjecucionDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(modeloEjecucionDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.TRUE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void desactivar_ReturnModeloEjecucion() throws Exception {
    Long idModeloEjecucion = 1L;

    final ResponseEntity<ModeloEjecucion> response = restTemplate.exchange(
        MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), ModeloEjecucion.class, idModeloEjecucion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloEjecucion modeloEjecucionDisabled = response.getBody();
    Assertions.assertThat(modeloEjecucionDisabled.getId()).as("getId()").isEqualTo(idModeloEjecucion);
    Assertions.assertThat(modeloEjecucionDisabled.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(modeloEjecucionDisabled.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(modeloEjecucionDisabled.getActivo()).as("getActivo()").isEqualTo(Boolean.FALSE);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findById_ReturnsModeloEjecucion() throws Exception {
    Long idModeloEjecucion = 1L;

    final ResponseEntity<ModeloEjecucion> response = restTemplate.exchange(
        MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null),
        ModeloEjecucion.class, idModeloEjecucion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ModeloEjecucion modeloEjecucion = response.getBody();
    Assertions.assertThat(modeloEjecucion.getId()).as("getId()").isEqualTo(idModeloEjecucion);
    Assertions.assertThat(modeloEjecucion.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(modeloEjecucion.getDescripcion()).as("getDescripcion()").isEqualTo("descripcion-1");
    Assertions.assertThat(modeloEjecucion.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAll_WithPagingSortingAndFiltering_ReturnsModeloEjecucionSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<ModeloEjecucion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloEjecucion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloEjecucion> modelosEjecucion = response.getBody();
    Assertions.assertThat(modelosEjecucion.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modelosEjecucion.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modelosEjecucion.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modelosEjecucion.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllTodos_WithPagingSortingAndFiltering_ReturnsModeloEjecucionSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "nombre,desc";
    String filter = "descripcion=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<ModeloEjecucion>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloEjecucion>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloEjecucion> modelosEjecucion = response.getBody();
    Assertions.assertThat(modelosEjecucion.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modelosEjecucion.get(0).getNombre()).as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modelosEjecucion.get(1).getNombre()).as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modelosEjecucion.get(2).getNombre()).as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * 
   * MODELO TIPO ENLACE
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloTipoEnlaces_WithPagingSortingAndFiltering_ReturnsModeloTipoEnlaceSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "tipoEnlace.nombre,desc";
    String filter = "tipoEnlace.descripcion=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipoenlaces")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloTipoEnlace>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloTipoEnlace>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloTipoEnlace> modeloTipoEnlaces = response.getBody();
    Assertions.assertThat(modeloTipoEnlaces.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modeloTipoEnlaces.get(0).getTipoEnlace().getNombre())
        .as("get(0).getTipoEnlace().getNombre())").isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modeloTipoEnlaces.get(1).getTipoEnlace().getNombre())
        .as("get(1).getTipoEnlace().getNombre())").isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modeloTipoEnlaces.get(2).getTipoEnlace().getNombre())
        .as("get(2).getTipoEnlace().getNombre())").isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * 
   * MODELO TIPO FASE
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloTipoFases_WithPagingSortingAndFiltering_ReturnsModeloTipoFaseSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "tipoFase.nombre,desc";
    String filter = "tipoFase.descripcion=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofases")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloTipoFase>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloTipoFase>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloTipoFase> modeloTipoFases = response.getBody();
    Assertions.assertThat(modeloTipoFases.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modeloTipoFases.get(0).getTipoFase().getNombre()).as("get(0).getTipoFase().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modeloTipoFases.get(1).getTipoFase().getNombre()).as("get(1).getTipoFase().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modeloTipoFases.get(2).getTipoFase().getNombre()).as("get(2).getTipoFase().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloTipoFasesConvocatoria_WithPagingSortingAndFiltering_ReturnsModeloTipoFaseSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "tipoFase.nombre,desc";
    String filter = "tipoFase.descripcion=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofases/convocatoria")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloTipoFase>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloTipoFase>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloTipoFase> modeloTipoFases = response.getBody();
    Assertions.assertThat(modeloTipoFases.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modeloTipoFases.get(0).getTipoFase().getNombre()).as("get(0).getTipoFase().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modeloTipoFases.get(1).getTipoFase().getNombre()).as("get(1).getTipoFase().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modeloTipoFases.get(2).getTipoFase().getNombre()).as("get(2).getTipoFase().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloTipoFasesProyecto_WithPagingSortingAndFiltering_ReturnsModeloTipoFaseSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "tipoFase.nombre,desc";
    String filter = "tipoFase.descripcion=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofases/proyecto")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloTipoFase>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloTipoFase>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloTipoFase> modeloTipoFases = response.getBody();
    Assertions.assertThat(modeloTipoFases.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modeloTipoFases.get(0).getTipoFase().getNombre()).as("get(0).getTipoFase().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modeloTipoFases.get(1).getTipoFase().getNombre()).as("get(1).getTipoFase().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modeloTipoFases.get(2).getTipoFase().getNombre()).as("get(2).getTipoFase().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * 
   * MODELO TIPO FASE DOCUMENTO
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloTipoDocumentos_WithPagingSortingAndFiltering_ReturnsModeloTipoDocumentoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "tipoDocumento.nombre,desc";
    String filter = "tipoDocumento.descripcion=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipodocumentos")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloTipoDocumento>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloTipoDocumento>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloTipoDocumento> modeloTipoDocumentos = response.getBody();
    Assertions.assertThat(modeloTipoDocumentos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modeloTipoDocumentos.get(0).getTipoDocumento().getNombre())
        .as("get(0).getTipoDocumento().getNombre())").isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modeloTipoDocumentos.get(1).getTipoDocumento().getNombre())
        .as("get(1).getTipoDocumento().getNombre())").isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modeloTipoDocumentos.get(2).getTipoDocumento().getNombre())
        .as("get(2).getTipoDocumento().getNombre())").isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * 
   * MODELO TIPO FINALIDAD
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloTipoFinalidades_WithPagingSortingAndFiltering_ReturnsModeloTipoFinalidadSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "tipoFinalidad.nombre,desc";
    String filter = "tipoFinalidad.descripcion=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipofinalidades")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloTipoFinalidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloTipoFinalidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloTipoFinalidad> tiposFinalidad = response.getBody();
    Assertions.assertThat(tiposFinalidad.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(tiposFinalidad.get(0).getTipoFinalidad().getNombre())
        .as("get(0).getTipoFinalidad().getNombre())").isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(tiposFinalidad.get(1).getTipoFinalidad().getNombre())
        .as("get(1).getTipoFinalidad().getNombre())").isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(tiposFinalidad.get(2).getTipoFinalidad().getNombre())
        .as("get(2).getTipoFinalidad().getNombre())").isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * 
   * MODELO TIPO HITO
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloTipoHitos_WithPagingSortingAndFiltering_ReturnsModeloTipoHitoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "tipoHito.nombre,desc";
    String filter = "tipoHito.descripcion=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloTipoHito>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloTipoHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloTipoHito> modeloTipoHitos = response.getBody();
    Assertions.assertThat(modeloTipoHitos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modeloTipoHitos.get(0).getTipoHito().getNombre()).as("get(0).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modeloTipoHitos.get(1).getTipoHito().getNombre()).as("get(1).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modeloTipoHitos.get(2).getTipoHito().getNombre()).as("get(2).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloTipoHitosConvocatoria_WithPagingSortingAndFiltering_ReturnsModeloTipoHitoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "tipoHito.nombre,desc";
    String filter = "tipoHito.descripcion=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos/convocatoria")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloTipoHito>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloTipoHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloTipoHito> modeloTipoHitos = response.getBody();
    Assertions.assertThat(modeloTipoHitos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modeloTipoHitos.get(0).getTipoHito().getNombre()).as("get(0).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modeloTipoHitos.get(1).getTipoHito().getNombre()).as("get(1).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modeloTipoHitos.get(2).getTipoHito().getNombre()).as("get(2).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloTipoHitosProyecto_WithPagingSortingAndFiltering_ReturnsModeloTipoHitoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "tipoHito.nombre,desc";
    String filter = "tipoHito.descripcion=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos/proyecto")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloTipoHito>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloTipoHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloTipoHito> modeloTipoHitos = response.getBody();
    Assertions.assertThat(modeloTipoHitos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modeloTipoHitos.get(0).getTipoHito().getNombre()).as("get(0).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modeloTipoHitos.get(1).getTipoHito().getNombre()).as("get(1).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modeloTipoHitos.get(2).getTipoHito().getNombre()).as("get(2).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloTipoHitosSolicitud_WithPagingSortingAndFiltering_ReturnsModeloTipoHitoSubList()
      throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "tipoHito.nombre,desc";
    String filter = "tipoHito.descripcion=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelotipohitos/solicitud")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloTipoHito>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloTipoHito>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloTipoHito> modeloTipoHitos = response.getBody();
    Assertions.assertThat(modeloTipoHitos.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(modeloTipoHitos.get(0).getTipoHito().getNombre()).as("get(0).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(modeloTipoHitos.get(1).getTipoHito().getNombre()).as("get(1).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(modeloTipoHitos.get(2).getTipoHito().getNombre()).as("get(2).getTipoHito().getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * 
   * MODELO UNIDAD
   * 
   */

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void findAllModeloUnidades_WithPagingSortingAndFiltering_ReturnsModeloUnidadSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "unidadGestionRef,desc";
    String filter = "unidadGestionRef=ke=00";

    Long idModeloEjecucion = 1L;

    URI uri = UriComponentsBuilder
        .fromUriString(MODELO_EJECUCION_CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/modelounidades")
        .queryParam("s", sort).queryParam("q", filter).buildAndExpand(idModeloEjecucion).toUri();

    final ResponseEntity<List<ModeloUnidad>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ModeloUnidad>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ModeloUnidad> unidadesModelo = response.getBody();
    Assertions.assertThat(unidadesModelo.size()).isEqualTo(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(unidadesModelo.get(0).getUnidadGestionRef()).as("get(0).getUnidadGestion())")
        .isEqualTo("unidad-" + String.format("%03d", 3));
    Assertions.assertThat(unidadesModelo.get(1).getUnidadGestionRef()).as("get(1).getUnidadGestion())")
        .isEqualTo("unidad-" + String.format("%03d", 2));
    Assertions.assertThat(unidadesModelo.get(2).getUnidadGestionRef()).as("get(2).getUnidadGestion())")
        .isEqualTo("unidad-" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto ModeloEjecucion
   * 
   * @param id id del ModeloEjecucion
   * @return el objeto ModeloEjecucion
   */
  private ModeloEjecucion generarMockModeloEjecucion(Long id) {
    return generarMockModeloEjecucion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ModeloEjecucion
   * 
   * @param id id del ModeloEjecucion
   * @return el objeto ModeloEjecucion
   */
  private ModeloEjecucion generarMockModeloEjecucion(Long id, String nombre) {
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion();
    modeloEjecucion.setId(id);
    modeloEjecucion.setNombre(nombre);
    modeloEjecucion.setDescripcion("descripcion-" + id);
    modeloEjecucion.setActivo(Boolean.TRUE);

    return modeloEjecucion;
  }

}