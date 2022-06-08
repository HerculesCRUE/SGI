package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.ConvocatoriaBaremacionController;
import org.crue.hercules.sgi.prc.dto.BaremoInput;
import org.crue.hercules.sgi.prc.dto.BaremoOutput;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionInput;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionOutput;
import org.crue.hercules.sgi.prc.dto.RangoInput;
import org.crue.hercules.sgi.prc.dto.RangoOutput;
import org.crue.hercules.sgi.prc.model.Baremo.TipoCuantia;
import org.crue.hercules.sgi.prc.model.Rango.TipoRango;
import org.crue.hercules.sgi.prc.model.Rango.TipoTemporalidad;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
 * Test de integracion de ConvocatoriaBaremacion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConvocatoriaBaremacionIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = ConvocatoriaBaremacionController.MAPPING;
  private static final String PATH_ID = ConvocatoriaBaremacionController.PATH_ID;
  private static final String PATH_BAREMOS = ConvocatoriaBaremacionController.PATH_BAREMOS;
  public static final String PATH_RANGOS = ConvocatoriaBaremacionController.PATH_RANGOS;

  private static final Long DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID = 1L;

  private static final String NOMBRE_PREFIX = "Convocatoria baremación ";

  private HttpEntity<Object> buildRequest(HttpHeaders headers, Object entity,
      String... roles)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", roles)));

    HttpEntity<Object> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/convocatoria_baremacion.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @ValueSource(strings = { "nombre=ik=\"Convocatoria ACI2 2021\"" })
  void findAll_WithPagingSortingAndFiltering_ReturnsConvocatoriaBaremacionSubList(
      String filter)
      throws Exception {
    String roles = "PRC-CON-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/todos")
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUri();

    final ResponseEntity<List<ConvocatoriaBaremacionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ConvocatoriaBaremacionOutput>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConvocatoriaBaremacionOutput> convocatoriasBaremacion = response.getBody();
    int numConvocatoriasBaremacion = convocatoriasBaremacion.size();
    Assertions.assertThat(numConvocatoriasBaremacion).as("numConvocatorias").isEqualTo(1);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(convocatoriasBaremacion.get(0).getNombre())
        .as("get(0).getNombre())")
        .isEqualTo("Convocatoria ACI2 2021");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/convocatoria_baremacion.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsConvocatoriaBaremacion() throws Exception {
    Long idConvocatoriaBaremacion = 1L;
    String roles = "PRC-CON-V";

    final ResponseEntity<ConvocatoriaBaremacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.GET, buildRequest(null, null, roles),
        ConvocatoriaBaremacionOutput.class, idConvocatoriaBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaBaremacionOutput convocatoriaBaremacion = response.getBody();
    Assertions.assertThat(convocatoriaBaremacion.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaBaremacion.getNombre()).as("getNombre()")
        .isEqualTo("Convocatoria ACI 2021");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/convocatoria_baremacion.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void activar_ReturnsConvocatoriaBaremacion() throws Exception {
    Long idConvocatoriaBaremacion = 5L;
    String roles = "PRC-CON-R";

    final ResponseEntity<ConvocatoriaBaremacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID + "/activar", HttpMethod.PATCH, buildRequest(null, null, roles),
        ConvocatoriaBaremacionOutput.class, idConvocatoriaBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaBaremacionOutput convocatoriaBaremacion = response.getBody();
    Assertions.assertThat(convocatoriaBaremacion.getId()).as("getId()").isEqualTo(idConvocatoriaBaremacion);
    Assertions.assertThat(convocatoriaBaremacion.getActivo()).as("getActivo()")
        .isEqualTo(Boolean.TRUE);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/convocatoria_baremacion.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnsConvocatoriaBaremacion() throws Exception {
    Long idConvocatoriaBaremacion = 1L;
    String roles = "PRC-CON-B";

    final ResponseEntity<ConvocatoriaBaremacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID + "/desactivar", HttpMethod.PATCH, buildRequest(null, null, roles),
        ConvocatoriaBaremacionOutput.class, idConvocatoriaBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaBaremacionOutput convocatoriaBaremacion = response.getBody();
    Assertions.assertThat(convocatoriaBaremacion.getId()).as("getId()").isEqualTo(idConvocatoriaBaremacion);
    Assertions.assertThat(convocatoriaBaremacion.getActivo()).as("getActivo()")
        .isEqualTo(Boolean.FALSE);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/convocatoria_baremacion.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsConvocatoriaBaremacion() throws Exception {
    String roles = "PRC-CON-C";
    ConvocatoriaBaremacionInput convocatoriaBaremacion = generarMockConvocatoriaBaremacionInput();

    final ResponseEntity<ConvocatoriaBaremacionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null, convocatoriaBaremacion, roles), ConvocatoriaBaremacionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ConvocatoriaBaremacionOutput convocatoriaBaremacionCreado = response.getBody();
    Assertions.assertThat(convocatoriaBaremacionCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaBaremacionCreado.getNombre()).as("getNombre()")
        .isEqualTo(convocatoriaBaremacion.getNombre());
    Assertions.assertThat(convocatoriaBaremacionCreado.getAnio()).as("getAnio()")
        .isEqualTo(convocatoriaBaremacion.getAnio());
    Assertions.assertThat(convocatoriaBaremacionCreado.getAniosBaremables()).as("getAniosBaremables()")
        .isEqualTo(convocatoriaBaremacion.getAniosBaremables());
    Assertions.assertThat(convocatoriaBaremacionCreado.getUltimoAnio()).as("getUltimoAnio()")
        .isEqualTo(convocatoriaBaremacion.getUltimoAnio());
    Assertions.assertThat(convocatoriaBaremacionCreado.getImporteTotal()).as("getImporteTotal()")
        .isEqualTo(convocatoriaBaremacion.getImporteTotal());
    Assertions.assertThat(convocatoriaBaremacionCreado.getPartidaPresupuestaria()).as("getPartidaPresupuestaria()")
        .isEqualTo(convocatoriaBaremacion.getPartidaPresupuestaria());
    Assertions.assertThat(convocatoriaBaremacionCreado.getActivo()).as("getActivo()").isTrue();
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/convocatoria_baremacion.sql"
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsConvocatoriaBaremacion() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;
    ConvocatoriaBaremacionInput convocatoriaBaremacion = generarMockConvocatoriaBaremacionInput();
    convocatoriaBaremacion.setNombre("nombre-actualizado");

    final ResponseEntity<ConvocatoriaBaremacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.PUT, buildRequest(null, convocatoriaBaremacion, roles),
        ConvocatoriaBaremacionOutput.class, convocatoriaBaremacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConvocatoriaBaremacionOutput convocatoriaBaremacionActualizado = response.getBody();
    Assertions.assertThat(convocatoriaBaremacionActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(convocatoriaBaremacionActualizado.getNombre()).as("getNombre()")
        .isEqualTo(convocatoriaBaremacion.getNombre());
    Assertions.assertThat(convocatoriaBaremacionActualizado.getAnio()).as("getAnio()")
        .isEqualTo(convocatoriaBaremacion.getAnio());
    Assertions.assertThat(convocatoriaBaremacionActualizado.getAniosBaremables()).as("getAniosBaremables()")
        .isEqualTo(convocatoriaBaremacion.getAniosBaremables());
    Assertions.assertThat(convocatoriaBaremacionActualizado.getUltimoAnio()).as("getUltimoAnio()")
        .isEqualTo(convocatoriaBaremacion.getUltimoAnio());
    Assertions.assertThat(convocatoriaBaremacionActualizado.getImporteTotal()).as("getImporteTotal()")
        .isEqualTo(convocatoriaBaremacion.getImporteTotal());
    Assertions.assertThat(convocatoriaBaremacionActualizado.getPartidaPresupuestaria()).as("getPartidaPresupuestaria()")
        .isEqualTo(convocatoriaBaremacion.getPartidaPresupuestaria());
    Assertions.assertThat(convocatoriaBaremacionActualizado.getActivo()).as("getActivo()").isTrue();
  }

  private ConvocatoriaBaremacionInput generarMockConvocatoriaBaremacionInput(String nombreSuffix, Integer anio) {
    ConvocatoriaBaremacionInput convocatoriaBaremacion = new ConvocatoriaBaremacionInput();
    convocatoriaBaremacion.setNombre(NOMBRE_PREFIX + nombreSuffix);
    convocatoriaBaremacion.setAnio(anio);
    convocatoriaBaremacion.setAniosBaremables(3);
    convocatoriaBaremacion.setUltimoAnio(2028);
    convocatoriaBaremacion.setImporteTotal(new BigDecimal(50000));
    convocatoriaBaremacion.setPartidaPresupuestaria("1234567890");

    return convocatoriaBaremacion;
  }

  private ConvocatoriaBaremacionInput generarMockConvocatoriaBaremacionInput() {
    return generarMockConvocatoriaBaremacionInput("001", 2020);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ParameterizedTest
  @ValueSource(strings = { "peso==10" })
  void findBaremos_WithPagingSortingAndFiltering_ReturnsBaremoubList(
      String filter)
      throws Exception {
    String roles = "PRC-CON-V";
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,asc";
    Long convocatoriaBaremacionId = 1L;

    String uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_BAREMOS)
        .queryParam("s", sort)
        .queryParam("q", filter)
        .build(false)
        .toUriString();

    final ResponseEntity<List<BaremoOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<BaremoOutput>>() {
        }, convocatoriaBaremacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<BaremoOutput> baremos = response.getBody();
    int numBaremos = baremos.size();
    Assertions.assertThat(numBaremos).as("numBaremos").isEqualTo(1);

    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("1");

    Assertions.assertThat(baremos.get(0).getPeso())
        .as("get(0).getPeso())")
        .isEqualTo(10);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  @SuppressWarnings("unchecked")
  void updateBaremos_Returns400TotalWeight() throws Exception {
    String roles = "PRC-CON-E";
    List<BaremoInput> baremos = new ArrayList<>();
    final Long convocatoriaBaremacionId = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    baremos.add(generarMockBaremoInput(null, null, null, null, 1L, convocatoriaBaremacionId));

    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMOS, HttpMethod.PATCH, buildRequest(null, baremos, roles),
        Object.class, convocatoriaBaremacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
    Assertions.assertThat(body).as("type").containsEntry("type", "urn:problem-type:validation");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  @SuppressWarnings("unchecked")
  void updateBaremos_Returns400WeightRequired() throws Exception {
    String roles = "PRC-CON-E";
    List<BaremoInput> baremos = new ArrayList<>();
    final Long convocatoriaBaremacionId = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    baremos.add(generarMockBaremoInput(null, new BigDecimal(50), null, null, 1L, convocatoriaBaremacionId));
    baremos.add(generarMockBaremoInput(100, null, null, null, 3L, convocatoriaBaremacionId));

    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMOS, HttpMethod.PATCH, buildRequest(null, baremos, roles),
        Object.class, convocatoriaBaremacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
    Assertions.assertThat(body).as("type").containsEntry("type", "urn:problem-type:validation");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  @SuppressWarnings("unchecked")
  void updateBaremos_Returns400QuantityConfiguration() throws Exception {
    String roles = "PRC-CON-E";
    List<BaremoInput> baremos = new ArrayList<>();
    final Long convocatoriaBaremacionId = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    baremos
        .add(
            generarMockBaremoInput(100, new BigDecimal(50.50), null, TipoCuantia.PUNTOS, 2L, convocatoriaBaremacionId));

    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMOS, HttpMethod.PATCH, buildRequest(null, baremos, roles),
        Object.class, convocatoriaBaremacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
    Assertions.assertThat(body).as("type").containsEntry("type", "urn:problem-type:validation");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  @SuppressWarnings("unchecked")
  void updateBaremos_Returns400NotScaleable() throws Exception {
    String roles = "PRC-CON-E";
    List<BaremoInput> baremos = new ArrayList<>();
    final Long convocatoriaBaremacionId = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    baremos.add(generarMockBaremoInput(100, new BigDecimal(50.50), null, null, 4L, convocatoriaBaremacionId));

    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMOS, HttpMethod.PATCH, buildRequest(null, baremos, roles),
        Object.class, convocatoriaBaremacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
    Assertions.assertThat(body).as("type").containsEntry("type", "urn:problem-type:validation");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  @SuppressWarnings("unchecked")
  void updateBaremos_Returns400RepeteadConfig() throws Exception {
    String roles = "PRC-CON-E";
    List<BaremoInput> baremos = new ArrayList<>();
    final Long convocatoriaBaremacionId = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    baremos.add(generarMockBaremoInput(100, null, null, null, 3L, convocatoriaBaremacionId));
    baremos.add(generarMockBaremoInput(null, new BigDecimal(50), null, null, 5L, convocatoriaBaremacionId));
    baremos.add(generarMockBaremoInput(null, new BigDecimal(100), null, null, 5L, convocatoriaBaremacionId));

    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMOS, HttpMethod.PATCH, buildRequest(null, baremos, roles),
        Object.class, convocatoriaBaremacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
    Assertions.assertThat(body).as("type").containsEntry("type", "urn:problem-type:validation");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  @SuppressWarnings("unchecked")
  void updateBaremos_Returns403ConvocatoriaBaremacionNotUpdatableException() throws Exception {
    String roles = "PRC-CON-E";
    List<BaremoInput> baremos = new ArrayList<>();
    final Long convocatoriaBaremacionId = 2L;
    baremos.add(generarMockBaremoInput(100, null, null, null, 3L, convocatoriaBaremacionId));

    final ResponseEntity<Object> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMOS, HttpMethod.PATCH, buildRequest(null, baremos, roles),
        Object.class, convocatoriaBaremacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) response.getBody();
    Assertions.assertThat(body).as("type").containsEntry("type",
        "urn:problem-type:access-denied");
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void updateBaremos_ReturnsBaremoOutputList() throws Exception {
    String roles = "PRC-CON-E";
    List<BaremoInput> baremos = new ArrayList<>();
    final Long convocatoriaBaremacionId = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    baremos.add(generarMockBaremoInput(25, new BigDecimal(50), null, null, 1L, convocatoriaBaremacionId));
    baremos.add(generarMockBaremoInput(25, null, null, TipoCuantia.RANGO, 2L, convocatoriaBaremacionId));
    baremos.add(generarMockBaremoInput(50, null, null, null, 3L, convocatoriaBaremacionId));
    baremos.add(generarMockBaremoInput(null, new BigDecimal(50), null, null, 5L, convocatoriaBaremacionId));
    baremos.add(generarMockBaremoInput(null, new BigDecimal(100), null, null, 6L, convocatoriaBaremacionId));

    final ResponseEntity<List<BaremoOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_BAREMOS, HttpMethod.PATCH, buildRequest(null, baremos, roles),
        new ParameterizedTypeReference<List<BaremoOutput>>() {
        }, convocatoriaBaremacionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<BaremoOutput> baremosUpdated = response.getBody();
    Assertions.assertThat(baremosUpdated).as("size").size().isEqualTo(baremos.size());
  }

  @Test
  void find_without_data() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    final ResponseEntity<RangoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_RANGOS, HttpMethod.GET,
        buildRequest(null, null, roles),
        RangoOutput.class, convocatoriaBaremacionId, TipoRango.LICENCIA);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void find_without_convocatoria() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 14L;

    final ResponseEntity<RangoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_RANGOS, HttpMethod.GET,
        buildRequest(null, null, roles),
        RangoOutput.class, convocatoriaBaremacionId, TipoRango.LICENCIA);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void find_with_data() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    final ResponseEntity<List<RangoOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_RANGOS, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<RangoOutput>>() {
        }, convocatoriaBaremacionId, TipoRango.LICENCIA);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<RangoOutput> rangos = response.getBody();
    int numRangos = rangos.size();
    Assertions.assertThat(numRangos).as("numRangos").isEqualTo(3);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_without_convocatoria() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 144L;

    List<RangoInput> rangos = new ArrayList<>();
    rangos.add(RangoInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(RangoInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());

    final ResponseEntity<RangoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_RANGOS, HttpMethod.PATCH,
        buildRequest(null, rangos, roles),
        RangoOutput.class, convocatoriaBaremacionId, TipoRango.LICENCIA);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_validation_ko() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    List<RangoInput> rangos = new ArrayList<>();
    rangos.add(RangoInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(RangoInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(1))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());

    final ResponseEntity<RangoOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_RANGOS, HttpMethod.PATCH,
        buildRequest(null, rangos, roles),
        RangoOutput.class, convocatoriaBaremacionId, TipoRango.LICENCIA);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ok() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    List<RangoInput> rangos = new ArrayList<>();
    rangos.add(RangoInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(9))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());
    rangos.add(RangoInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(10))
        .hasta(new BigDecimal(19))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.FINAL)
        .build());

    final ResponseEntity<List<RangoOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_RANGOS, HttpMethod.PATCH,
        buildRequest(null, rangos, roles),
        new ParameterizedTypeReference<List<RangoOutput>>() {
        }, convocatoriaBaremacionId, TipoRango.LICENCIA);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final List<RangoOutput> rangosOuput = response.getBody();
    int numRangos = rangosOuput.size();
    Assertions.assertThat(numRangos).as("numRangos").isEqualTo(2);

    rangos = new ArrayList<>();
    rangos.add(RangoInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .desde(new BigDecimal(1))
        .hasta(new BigDecimal(9))
        .puntos(new BigDecimal(1))
        .tipoRango(TipoRango.LICENCIA)
        .tipoTemporalidad(TipoTemporalidad.INICIAL)
        .build());

    final ResponseEntity<List<RangoOutput>> response2 = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_RANGOS, HttpMethod.PATCH,
        buildRequest(null, rangos, roles),
        new ParameterizedTypeReference<List<RangoOutput>>() {
        }, convocatoriaBaremacionId, TipoRango.LICENCIA);

    Assertions.assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final List<RangoOutput> rangosOuput2 = response2.getBody();
    int numRangos2 = rangosOuput2.size();
    Assertions.assertThat(numRangos2).as("numRangos2").isEqualTo(1);
  }

  private BaremoInput generarMockBaremoInput(
      Integer peso, BigDecimal puntos, BigDecimal cuantia,
      TipoCuantia tipoCuantia, Long configuracionBaremoId, Long convocatoriaBaremacionId) {
    return BaremoInput.builder()
        .peso(peso)
        .cuantia(cuantia)
        .puntos(puntos)
        .tipoCuantia(tipoCuantia)
        .configuracionBaremoId(configuracionBaremoId)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .build();
  }
}
