package org.crue.hercules.sgi.pii.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.InformePatentabilidadOutput;
import org.crue.hercules.sgi.pii.dto.InvencionAreaConocimientoInput;
import org.crue.hercules.sgi.pii.dto.InvencionAreaConocimientoOutput;
import org.crue.hercules.sgi.pii.dto.InvencionDocumentoOutput;
import org.crue.hercules.sgi.pii.dto.InvencionDto;
import org.crue.hercules.sgi.pii.dto.InvencionGastoOutput;
import org.crue.hercules.sgi.pii.dto.InvencionIngresoOutput;
import org.crue.hercules.sgi.pii.dto.InvencionInput;
import org.crue.hercules.sgi.pii.dto.InvencionInventorInput;
import org.crue.hercules.sgi.pii.dto.InvencionInventorOutput;
import org.crue.hercules.sgi.pii.dto.InvencionOutput;
import org.crue.hercules.sgi.pii.dto.InvencionSectorAplicacionInput;
import org.crue.hercules.sgi.pii.dto.InvencionSectorAplicacionOutput;
import org.crue.hercules.sgi.pii.dto.SolicitudProteccionOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import lombok.Data;

/**
 * Test de integracion de Invencion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class InvencionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/invenciones";
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_ACTIVAR = "/activar";
  private static final String PATH_DESACTIVAR = "/desactivar";
  private static final String PATH_AREAS = "/{id}/areasconocimiento";
  public static final String PATH_SECTORES = "/{id}/sectoresaplicacion";
  public static final String PATH_INFORMESPATENTABILIDAD = "/{id}/informespatentabilidad";
  public static final String PATH_INVENCION_INVENTOR = "/{invencionId}/invencion-inventores";
  public static final String PATH_PRC = "/produccioncientifica/{anioInicio}/{anioFin}/{universidadId}";
  public static final String PATH_INVENCION_GASTO = "/{invencionId}/gastos";
  public static final String PATH_INVENCION_INGRESO = "/{invencionId}/ingresos";
  public static final String PATH_INVENCION_DOCUMENTOS = "/{invencionId}/invenciondocumentos";
  public static final String PATH_SOLICITUDES_PROTECCION = "/{invencionId}/solicitudesproteccion";

  private HttpEntity<Object> buildRequest(HttpHeaders headers,
      Object entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findActivos_WithPagingSortingAndFiltering_ReturnInvencionOutputSubList() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R", "PII-INV-MOD-V" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<InvencionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionOutput> tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput.size()).isEqualTo(3);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("3");

    Assertions.assertThat(tramoRepartoOutput.get(0).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(tramoRepartoOutput.get(1).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(tramoRepartoOutput.get(2).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/tipo_proteccion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findActivos_WithPagingSortingAndFiltering_ReturnStatusCodeNO_CONTENT() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R", "PII-INV-MOD-V" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<InvencionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnInvencionOutpuSubList() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<InvencionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionOutput> tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput.size()).isEqualTo(4);
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo("4");

    Assertions.assertThat(tramoRepartoOutput.get(0).getId()).as("id").isEqualTo(4);
    Assertions.assertThat(tramoRepartoOutput.get(1).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(tramoRepartoOutput.get(2).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(tramoRepartoOutput.get(3).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnStatusCodeNO_CONTENT() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "descripcion=ke=-00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<InvencionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E", "PII-INV-B", "PII-INV-R", "PII-INV-MOD-V" };
    Long invencionOutputId = 1L;

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH
        + PATH_PARAMETER_ID, HttpMethod.GET,
        buildRequest(null, null, roles), InvencionOutput.class, invencionOutputId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion-001");
    Assertions.assertThat(invencionOutput.getDescripcion()).as("descripcion").isEqualTo("descripcion-invencion-001");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-C" };
    InvencionInput invencionInput = generaMockInvencionInput();

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, invencionInput, roles), InvencionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(5);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion");
    Assertions.assertThat(invencionOutput.getDescripcion()).as("descripcion").isEqualTo("descripcion-invencion");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-E" };
    Long invencionId = 1L;
    InvencionInput invencionInput = generaMockInvencionInput();
    invencionInput.setComentarios("comentarios-invencion-modificado");

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT,
        buildRequest(null, invencionInput, roles), InvencionOutput.class, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion");
    Assertions.assertThat(invencionOutput.getComentarios()).as("comentarios")
        .isEqualTo("comentarios-invencion-modificado");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:cleanup.sql",
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void activar_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-R" };
    Long invencionId = 4L;

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ACTIVAR,
        HttpMethod.PATCH,
        buildRequest(null, null, roles), InvencionOutput.class, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(4);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion-004");
    Assertions.assertThat(invencionOutput.getComentarios()).as("comentarios")
        .isEqualTo("comentarios-invencion-004");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnInvencionOutput() throws Exception {

    String[] roles = { "PII-INV-B" };
    Long invencionId = 1L;

    final ResponseEntity<InvencionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DESACTIVAR,
        HttpMethod.PATCH,
        buildRequest(null, null, roles), InvencionOutput.class, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final InvencionOutput invencionOutput = response.getBody();

    Assertions.assertThat(invencionOutput.getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionOutput.getTitulo()).as("titulo").isEqualTo("titulo-invencion-001");
    Assertions.assertThat(invencionOutput.getComentarios()).as("comentarios")
        .isEqualTo("comentarios-invencion-001");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void exists_ReturnStatusCodeOK() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-E" };
    Long invencionOutputId = 1L;

    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH
        + PATH_PARAMETER_ID, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class, invencionOutputId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void exists_ReturnStatusCodeNO_CONTENT() throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-E" };
    Long invencionOutputId = 111L;

    final ResponseEntity<Void> response = restTemplate.exchange(CONTROLLER_BASE_PATH
        + PATH_PARAMETER_ID, HttpMethod.HEAD,
        buildRequest(null, null, roles), Void.class, invencionOutputId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/sector_aplicacion.sql",
    "classpath:scripts/invencion_sector_aplicacion.sql"
    // @formatter:on
  })
  @Test
  void updateSectoresAplicacion_ReturnInvencionSectorAplicacionOutputSubList() throws Exception {
    String[] roles = { "PII-INV-E", "PII-INV-C" };

    Long idInvencion = 2L;

    List<InvencionSectorAplicacionInput> toUpdate = Arrays.asList(InvencionSectorAplicacionInput.builder()
        .invencionId(idInvencion)
        .sectorAplicacionId(1L)
        .build());

    final ResponseEntity<List<InvencionSectorAplicacionOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_SECTORES,
        HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), new ParameterizedTypeReference<List<InvencionSectorAplicacionOutput>>() {
        }, idInvencion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/sector_aplicacion.sql",
    "classpath:scripts/invencion_sector_aplicacion.sql"
    // @formatter:on
  })
  @Test
  void updateSectoresAplicacion_ReturnError() throws Exception {
    String[] roles = { "PII-INV-E", "PII-INV-C" };

    Long idInvencion = 2L;

    List<InvencionSectorAplicacionInput> toUpdate = Arrays.asList(InvencionSectorAplicacionInput.builder()
        .invencionId(1L)
        .sectorAplicacionId(1L)
        .build());

    final ResponseEntity<ResponseError> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_SECTORES,
        HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), ResponseError.class, idInvencion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    ResponseError error = response.getBody();

    Assertions.assertThat(error).isNotNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql",
  "classpath:scripts/invencion_area_conocimiento.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAreasConocimientoByInvencionId_ReturnInvencionAreaConocimientoOutput() throws Exception {

    String[] roles = { "PII-INV-E", "PII-INV-V", "PII-INV-C" };
    Long invencionId = 1L;

    final ResponseEntity<List<InvencionAreaConocimientoOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_AREAS,
        HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<InvencionAreaConocimientoOutput>>() {
        }, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionAreaConocimientoOutput> invencionAreaConocimientoOutput = response.getBody();

    Assertions.assertThat(invencionAreaConocimientoOutput.size()).isEqualTo(1);
    Assertions.assertThat(invencionAreaConocimientoOutput.get(0).getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionAreaConocimientoOutput.get(0).getInvencionId()).as("invencionId").isEqualTo(1);
    Assertions.assertThat(invencionAreaConocimientoOutput.get(0).getAreaConocimientoRef()).as("areaConocimientoRef")
        .isEqualTo("560");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_area_conocimiento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateAreasConocimiento_ReturnInvencionAreaConocimientoOutputSubList() throws Exception {
    String[] roles = { "PII-INV-E", "PII-INV-C" };

    Long idInvencion = 2L;

    List<InvencionAreaConocimientoInput> toUpdate = Arrays.asList(InvencionAreaConocimientoInput.builder()
        .invencionId(idInvencion)
        .areaConocimientoRef("007")
        .build());

    final ResponseEntity<List<InvencionAreaConocimientoOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_AREAS,
        HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), new ParameterizedTypeReference<List<InvencionAreaConocimientoOutput>>() {
        }, idInvencion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_area_conocimiento.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateAreasConocimiento_ReturnError() throws Exception {
    String[] roles = { "PII-INV-E", "PII-INV-C" };

    Long idInvencion = 2L;

    List<InvencionAreaConocimientoInput> toUpdate = Arrays.asList(InvencionAreaConocimientoInput.builder()
        .invencionId(1L)
        .areaConocimientoRef("007")
        .build());

    final ResponseEntity<ResponseError> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_AREAS,
        HttpMethod.PATCH,
        buildRequest(null, toUpdate, roles), ResponseError.class, idInvencion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    ResponseError error = response.getBody();

    Assertions.assertThat(error).isNotNull();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql",
  "classpath:scripts/sector_aplicacion.sql",
  "classpath:scripts/invencion_sector_aplicacion.sql"
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findSectoresAplicacionByInvencionId_ReturnInvencionSectorAplicacionOutputSubList() throws Exception {

    String[] roles = { "PII-INV-E", "PII-INV-V", "PII-INV-C" };
    Long invencionId = 1L;

    final ResponseEntity<List<InvencionSectorAplicacionOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_SECTORES,
        HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<InvencionSectorAplicacionOutput>>() {
        }, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionSectorAplicacionOutput> invencionSectorAplicacionOutput = response.getBody();

    Assertions.assertThat(invencionSectorAplicacionOutput.size()).isEqualTo(1);
    Assertions.assertThat(invencionSectorAplicacionOutput.get(0).getId()).as("id").isEqualTo(1);
    Assertions.assertThat(invencionSectorAplicacionOutput.get(0).getInvencionId()).as("invencionId").isEqualTo(1);
    Assertions.assertThat(invencionSectorAplicacionOutput.get(0).getSectorAplicacion().getId()).as("sectorAplicacionId")
        .isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
  "classpath:cleanup.sql",
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/resultado_informe_patentabilidad.sql",
  "classpath:scripts/invencion.sql",
  "classpath:scripts/informe_patentabilidad.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findInformesPatentabilidadByInvencionId_ReturnInformePatentabilidadOutputSubList() throws Exception {

    String[] roles = { "PII-INV-E", "PII-INV-V" };
    Long invencionId = 1L;

    final ResponseEntity<List<InformePatentabilidadOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_INFORMESPATENTABILIDAD,
        HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<InformePatentabilidadOutput>>() {
        }, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InformePatentabilidadOutput> informePatentabilidadOutput = response.getBody();

    Assertions.assertThat(informePatentabilidadOutput.size()).isEqualTo(1);
    Assertions.assertThat(informePatentabilidadOutput.get(0).getId()).as("id").isEqualTo(1);
    Assertions.assertThat(informePatentabilidadOutput.get(0).getInvencionId()).as("invencionId").isEqualTo(1);
    Assertions.assertThat(informePatentabilidadOutput.get(0).getComentarios()).as("comentarios")
        .isEqualTo("comentarios-001");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/invencion.sql",
  "classpath:scripts/invencion_inventor.sql",
// @formatter:on
  })

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findInvencionInventoresByInvencionId__WithPagingSortingAndFiltering_ReturnInvencionInventoresOutputSubList()
      throws Exception {

    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "inventorRef=ke=0";
    Long invencionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INVENCION_INVENTOR).queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(invencionId).toUri();

    final ResponseEntity<List<InvencionInventorOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionInventorOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionInventorOutput> invencionInventorOutput = response.getBody();

    Assertions.assertThat(invencionInventorOutput.size()).isEqualTo(3);
    Assertions.assertThat(invencionInventorOutput.get(0).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(invencionInventorOutput.get(1).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(invencionInventorOutput.get(2).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
  // @formatter:off
  "classpath:scripts/tipo_proteccion.sql",
  "classpath:scripts/tipo_caducidad.sql",
  "classpath:scripts/via_proteccion.sql",
  "classpath:scripts/invencion.sql",
  "classpath:scripts/invencion_inventor.sql",
  "classpath:scripts/periodo_titularidad.sql",
  "classpath:scripts/periodo_titularidad_titular.sql",
  "classpath:scripts/solicitud_proteccion.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @CsvSource({ "2020, 2021, 'Q3018001'" })
  void findInvencionesProduccionCientifica(Integer anioInicio, Integer anioFin, String universidadId)
      throws Exception {

    String roles = "CSP-PRO-PRC-V";

    final ResponseEntity<List<InvencionDto>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PRC,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<InvencionDto>>() {
        }, anioInicio, anioFin, universidadId);
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    int numInvenciones = response.getBody().size();
    Assertions.assertThat(numInvenciones).isEqualTo(3);

    int numParticipaciones = response.getBody().get(0).getParticipaciones().size();
    int numSolicitudes = response.getBody().get(0).getSolicitudesProteccion().size();
    int numInventores = response.getBody().get(0).getInventores().size();
    Assertions.assertThat(numParticipaciones).isEqualTo(1);
    Assertions.assertThat(numSolicitudes).isEqualTo(1);
    Assertions.assertThat(numInventores).isEqualTo(3);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_inventor.sql",
    "classpath:scripts/periodo_titularidad.sql",
    "classpath:scripts/periodo_titularidad_titular.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_gasto.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findInvencionGastosByInvencionId_ReturnsInvencionGastoOutputSubList() throws Exception {
    String[] roles = { "PII-INV-V", "PII-INV-E" };
    Long invencionId = 1L;
    final ResponseEntity<List<InvencionGastoOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_INVENCION_GASTO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<InvencionGastoOutput>>() {
        }, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).hasSize(5);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_inventor.sql",
    "classpath:scripts/periodo_titularidad.sql",
    "classpath:scripts/periodo_titularidad_titular.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_ingreso.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findInvencionIngresoByInvencionId_ReturnsInvencionIngresoOutputSubList() throws Exception {
    String[] roles = { "PII-INV-V", "PII-INV-E" };
    Long invencionId = 1L;
    final ResponseEntity<List<InvencionIngresoOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_INVENCION_INGRESO,
        HttpMethod.GET,
        buildRequest(null, null, roles),
        new ParameterizedTypeReference<List<InvencionIngresoOutput>>() {
        }, invencionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(response.getBody()).hasSize(5);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/resultado_informe_patentabilidad.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_documento.sql"
  // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findInvencionDocumentosByInvencionId_WithPagingSortingAndFiltering_ReturnsInvencionDocumentoOutputSubList()
      throws Exception {
    String[] roles = { "PII-INV-V", "PII-INV-C", "PII-INV-E" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "invencionId=1";
    Long invencionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INVENCION_DOCUMENTOS).queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(invencionId).toUri();

    final ResponseEntity<List<InvencionDocumentoOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<InvencionDocumentoOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionDocumentoOutput> invencionDocumentoOutput = response.getBody();

    Assertions.assertThat(invencionDocumentoOutput).hasSize(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_inventor.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateInventoresByInvencionId_WithPagingSortingAndFiltering_ReturnsInvencionInventorOutputSubList()
      throws Exception {
    String[] roles = { "PII-INV-C", "PII-INV-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "";
    String inventorRef1 = "11223344";
    String inventorRef2 = "11223345";

    BigDecimal participacion1 = new BigDecimal(50L);
    BigDecimal participacion2 = new BigDecimal(50L);

    Long invencionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_INVENCION_INVENTOR)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(invencionId).toUri();

    List<InvencionInventorInput> invencionInventoresInput = Arrays
        .asList(buildMockInvencionInventorInput(1L, invencionId, inventorRef1, participacion1),
            buildMockInvencionInventorInput(2L, invencionId, inventorRef2, participacion2));

    final ResponseEntity<List<InvencionInventorOutput>> response = restTemplate.exchange(uri, HttpMethod.PATCH,
        buildRequest(headers, invencionInventoresInput, roles),
        new ParameterizedTypeReference<List<InvencionInventorOutput>>() {
        });
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<InvencionInventorOutput> invencionesInventorOutput = response.getBody();

    Assertions.assertThat(invencionesInventorOutput).hasSize(2);

    InvencionInventorOutput inventor1 = invencionesInventorOutput.get(1);
    InvencionInventorOutput inventor2 = invencionesInventorOutput.get(0);

    Assertions.assertThat(inventor1.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(inventor2.getId()).as("getId()").isEqualTo(2L);

    Assertions.assertThat(inventor1.getInventorRef()).as("getInventorRef()").isEqualTo(inventorRef1);
    Assertions.assertThat(inventor2.getInventorRef()).as("getInventorRef()").isEqualTo(inventorRef2);

    Assertions.assertThat(inventor1.getParticipacion().longValue()).as("getParticipacion()")
        .isEqualTo(participacion1.longValue());
    Assertions.assertThat(inventor2.getParticipacion().longValue()).as("getParticipacion()")
        .isEqualTo(participacion2.longValue());
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findSolicitudesProteccionByInvencionId_WithPagingSortingAndFiltering_ReturnsSolicitudProteccionOutputSubList()
      throws Exception {
    String[] roles = { "PII-INV-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "";

    Long invencionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_SOLICITUDES_PROTECCION)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(invencionId).toUri();

    final ResponseEntity<List<SolicitudProteccionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<SolicitudProteccionOutput>>() {
        });
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<SolicitudProteccionOutput> solicitudes = response.getBody();

    Assertions.assertThat(solicitudes).hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql"
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findSolicitudesProteccionByInvencionId_WithPagingSortingAndFiltering_ReturnsHttpStatusNO_CONTENT()
      throws Exception {
    String[] roles = { "PII-INV-E" };
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "";

    Long invencionId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_SOLICITUDES_PROTECCION)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .buildAndExpand(invencionId).toUri();

    final ResponseEntity<List<SolicitudProteccionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles),
        new ParameterizedTypeReference<List<SolicitudProteccionOutput>>() {
        });
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  /**
   * Función que devuelve un objeto InvencionInput
   * 
   * @return el objeto InvencionInput
   */
  private InvencionInput generaMockInvencionInput() {

    InvencionInput invencionInput = new InvencionInput();
    invencionInput.setTitulo("titulo-invencion");
    invencionInput.setDescripcion("descripcion-invencion");
    invencionInput.setComentarios("comentarios-invencion");
    invencionInput.setFechaComunicacion(Instant.parse("2020-10-19T00:00:00Z"));
    invencionInput.setTipoProteccionId(1L);

    return invencionInput;
  }

  private InvencionInventorInput buildMockInvencionInventorInput(Long id, Long invencionId, String inventorRef,
      BigDecimal participacion) {
    return InvencionInventorInput.builder()
        .id(id)
        .activo(Boolean.TRUE)
        .invencionId(invencionId)
        .inventorRef(inventorRef)
        .participacion(participacion)
        .repartoUniversidad(Boolean.TRUE)
        .build();
  }

  @Data
  private static class ResponseError {
    public String type;
    public String title;
    public int status;
    public String detail;
    public String instance;
  }

}
