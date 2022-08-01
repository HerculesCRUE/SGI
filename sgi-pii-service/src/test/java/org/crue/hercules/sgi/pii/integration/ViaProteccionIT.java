package org.crue.hercules.sgi.pii.integration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.pii.dto.ViaProteccionInput;
import org.crue.hercules.sgi.pii.dto.ViaProteccionOutput;
import org.crue.hercules.sgi.pii.enums.TipoPropiedad;
import org.crue.hercules.sgi.pii.repository.ViaProteccionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Test de integracion de ViaProteccion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ViaProteccionIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String CONTROLLER_BASE_PATH = "/viasproteccion";
  private static final String PATH_TODOS = "/todos";
  private static final String PATH_ACTIVAR = "/activar";
  private static final String PATH_DESACTIVAR = "/desactivar";

  @Autowired
  private ViaProteccionRepository viaProteccionRepository;

  private HttpEntity<ViaProteccionInput> buildRequest(HttpHeaders headers,
      ViaProteccionInput entity, String... roles) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("bearer %s", tokenBuilder.buildToken("user", roles)));

    HttpEntity<ViaProteccionInput> request = new HttpEntity<>(entity, headers);

    return request;
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
// @formatter:off
  "classpath:scripts/via_proteccion.sql",
// @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnViaProteccionOutputSubList() throws Exception {

    String[] roles = { "PII-VPR-V", "PII-VPR-C", "PII-VPR-E", "PII-VPR-B", "PII-VPR-R", "PII-INV-E", "PII-INV-V" };

    // when: Obtiene la page=0 con pagesize=5
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "5");
    String sort = "id,desc";
    String filter = "nombre=ke=-00";

    Long numOfVias = this.viaProteccionRepository.count();

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_TODOS).queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<ViaProteccionOutput>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null, roles), new ParameterizedTypeReference<List<ViaProteccionOutput>>() {
        });

    // then: Respuesta OK, retorna la información de la página correcta en el header
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ViaProteccionOutput> tramoRepartoOutput = response.getBody();
    Assertions.assertThat(tramoRepartoOutput).hasSize(numOfVias.intValue());
    Assertions.assertThat(response.getHeaders().getFirst("X-Page")).isEqualTo("0");
    Assertions.assertThat(response.getHeaders().getFirst("X-Page-Size")).isEqualTo("5");
    Assertions.assertThat(response.getHeaders().getFirst("X-Total-Count")).isEqualTo(numOfVias.toString());

    Assertions.assertThat(tramoRepartoOutput.get(0).getId()).as("id").isEqualTo(4);
    Assertions.assertThat(tramoRepartoOutput.get(1).getId()).as("id").isEqualTo(3);
    Assertions.assertThat(tramoRepartoOutput.get(2).getId()).as("id").isEqualTo(2);
    Assertions.assertThat(tramoRepartoOutput.get(3).getId()).as("id").isEqualTo(1);

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/via_proteccion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnViaProteccionOutput() throws Exception {

    String[] roles = { "PII-VPR-C" };

    ViaProteccionInput viaProteccionInput = generarMockViaProteccionInput();

    ResponseEntity<ViaProteccionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH,
        HttpMethod.POST, buildRequest(null,
            viaProteccionInput, roles),
        ViaProteccionOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ViaProteccionOutput viaProteccionOutput = response.getBody();

    Assertions.assertThat(viaProteccionOutput.getId()).as("id").isNotNull();
    Assertions.assertThat(viaProteccionOutput.getNombre()).as("nombre").isEqualTo("nombre-via-proteccion");
    Assertions.assertThat(viaProteccionOutput.getDescripcion()).as("descripcion")
        .isEqualTo("descripcion-via-proteccion");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/via_proteccion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void activar_ReturnViaProteccionOutput() throws Exception {

    String[] roles = { "PII-VPR-R" };
    Long viaProteccionId = 3L;

    ResponseEntity<ViaProteccionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_ACTIVAR,
        HttpMethod.PATCH, buildRequest(null,
            null, roles),
        ViaProteccionOutput.class, viaProteccionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ViaProteccionOutput viaProteccionOutput = response.getBody();

    Assertions.assertThat(viaProteccionOutput.getId()).as("id").isNotNull();
    Assertions.assertThat(viaProteccionOutput.getNombre()).as("nombre").isEqualTo("nombre-via-proteccion-003");
    Assertions.assertThat(viaProteccionOutput.getDescripcion()).as("descripcion")
        .isEqualTo("descricion-via-proteccion-003");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/via_proteccion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnViaProteccionOutput() throws Exception {

    String[] roles = { "PII-VPR-B" };
    Long viaProteccionId = 2L;

    ResponseEntity<ViaProteccionOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_DESACTIVAR,
        HttpMethod.PATCH, buildRequest(null,
            null, roles),
        ViaProteccionOutput.class, viaProteccionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ViaProteccionOutput viaProteccionOutput = response.getBody();

    Assertions.assertThat(viaProteccionOutput.getId()).as("id").isNotNull();
    Assertions.assertThat(viaProteccionOutput.getNombre()).as("nombre").isEqualTo("nombre-via-proteccion-002");
    Assertions.assertThat(viaProteccionOutput.getDescripcion()).as("descripcion")
        .isEqualTo("descricion-via-proteccion-002");

  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off  
  "classpath:scripts/via_proteccion.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnViaProteccionOutput() throws Exception {

    String[] roles = { "PII-VPR-E" };
    Long viaProteccionId = 1L;

    ViaProteccionInput viaProteccionInput = generarMockViaProteccionInput();
    viaProteccionInput.setDescripcion("descricion-via-proteccion-modificado");

    ResponseEntity<ViaProteccionOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null,
            viaProteccionInput, roles),
        ViaProteccionOutput.class, viaProteccionId);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ViaProteccionOutput viaProteccionOutput = response.getBody();

    Assertions.assertThat(viaProteccionOutput.getId()).as("id").isNotNull();
    Assertions.assertThat(viaProteccionOutput.getNombre()).as("nombre").isEqualTo("nombre-via-proteccion");
    Assertions.assertThat(viaProteccionOutput.getDescripcion()).as("descripcion")
        .isEqualTo("descricion-via-proteccion-modificado");

  }

  /**
   * Función que devuelve un objeto ViaProteccionInput
   * 
   * @return el objeto ViaProteccionInput
   */
  private ViaProteccionInput generarMockViaProteccionInput() {
    ViaProteccionInput viaProteccionInput = new ViaProteccionInput();
    viaProteccionInput.setNombre("nombre-via-proteccion");
    viaProteccionInput.setDescripcion("descripcion-via-proteccion");
    viaProteccionInput.setExtensionInternacional(Boolean.FALSE);
    viaProteccionInput.setMesesPrioridad(1);
    viaProteccionInput.setPaisEspecifico(Boolean.FALSE);
    viaProteccionInput.setTipoPropiedad(TipoPropiedad.INDUSTRIAL);
    viaProteccionInput.setVariosPaises(Boolean.FALSE);

    return viaProteccionInput;
  }

}
