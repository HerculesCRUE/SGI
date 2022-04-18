package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.ConvocatoriaBaremacionController;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionInput;
import org.crue.hercules.sgi.prc.dto.ConvocatoriaBaremacionOutput;
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
public class ConvocatoriaBaremacionIT extends BaseIT {

  private static final String CONTROLLER_BASE_PATH = ConvocatoriaBaremacionController.REQUEST_MAPPING;
  private static final String PATH_PARAMETER_ID = "/{id}";

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
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.GET, buildRequest(null, null, roles),
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
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/activar", HttpMethod.PATCH, buildRequest(null, null, roles),
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
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + "/desactivar", HttpMethod.PATCH, buildRequest(null, null, roles),
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
    Long idFuenteFinanciacion = 1L;
    ConvocatoriaBaremacionInput convocatoriaBaremacion = generarMockConvocatoriaBaremacionInput();
    convocatoriaBaremacion.setNombre("nombre-actualizado");

    final ResponseEntity<ConvocatoriaBaremacionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.PUT, buildRequest(null, convocatoriaBaremacion, roles),
        ConvocatoriaBaremacionOutput.class, idFuenteFinanciacion);

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
}
