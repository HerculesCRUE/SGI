package org.crue.hercules.sgi.csp.integration;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConceptoGastoDescripcion;
import org.crue.hercules.sgi.csp.model.ConceptoGastoNombre;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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
 * Test de integracion de ConceptoGasto.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConceptoGastoIT extends BaseIT {

  private static final String PATH_PARAMETER_ID = "/{id}";
  private static final String PATH_PARAMETER_DESACTIVAR = "/desactivar";
  private static final String PATH_PARAMETER_REACTIVAR = "/reactivar";
  private static final String CONTROLLER_BASE_PATH = "/conceptogastos";
  private static final String PATH_NO_PROYECTO_AGRUPACION = "/no-proyectoagrupacion";

  private HttpEntity<ConceptoGasto> buildRequest(HttpHeaders headers, ConceptoGasto entity) throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s", tokenBuilder.buildToken("user", "CSP-CON-E", "CSP-CON-V", "CSP-CON-INV-V",
            "CSP-SOL-E", "CSP-SOL-V", "CSP-TGTO-V", "CSP-TGTO-C", "CSP-TGTO-E", "CSP-TGTO-B", "CSP-TGTO-R", "CSP-PRO-E",
            "CSP-SJUS-E")));

    HttpEntity<ConceptoGasto> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsConceptoGasto() throws Exception {
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(null);

    final ResponseEntity<ConceptoGasto> response = restTemplate.exchange(CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, conceptoGasto), ConceptoGasto.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ConceptoGasto conceptoGastoCreado = response.getBody();
    Assertions.assertThat(conceptoGastoCreado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(conceptoGastoCreado.getNombre()).as("getNombre()").isEqualTo(conceptoGasto.getNombre());
    Assertions.assertThat(conceptoGastoCreado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(conceptoGasto.getDescripcion());
    Assertions.assertThat(conceptoGastoCreado.getActivo()).as("getActivo()").isTrue();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsConceptoGasto() throws Exception {
    Long idConceptoGasto = 1L;
    ConceptoGasto conceptoGasto = generarMockConceptoGasto(idConceptoGasto, "nombre-actualizado");

    final ResponseEntity<ConceptoGasto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.PUT, buildRequest(null, conceptoGasto), ConceptoGasto.class, idConceptoGasto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConceptoGasto conceptoGastoActualizado = response.getBody();
    Assertions.assertThat(conceptoGastoActualizado.getId()).as("getId()").isNotNull();
    Assertions.assertThat(conceptoGastoActualizado.getNombre()).as("getNombre()").isEqualTo(conceptoGasto.getNombre());
    Assertions.assertThat(conceptoGastoActualizado.getDescripcion()).as("getDescripcion()")
        .isEqualTo(conceptoGasto.getDescripcion());
    Assertions.assertThat(conceptoGastoActualizado.getActivo()).as("getActivo()").isEqualTo(conceptoGasto.getActivo());
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void desactivar_ReturnConceptoGasto() throws Exception {
    Long idConceptoGasto = 1L;

    final ResponseEntity<ConceptoGasto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_DESACTIVAR, HttpMethod.PATCH,
        buildRequest(null, null), ConceptoGasto.class, idConceptoGasto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConceptoGasto conceptoGasto = response.getBody();
    Assertions.assertThat(conceptoGasto.getId()).as("getId()").isNotNull();
    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGasto.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("nombre-001");
    Assertions.assertThat(conceptoGasto.getDescripcion()).as("descripcion-001")
        .isEqualTo(conceptoGasto.getDescripcion());
    Assertions.assertThat(conceptoGasto.getActivo()).as("getActivo()").isFalse();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void reactivar_ReturnConceptoGasto() throws Exception {
    Long idConceptoGasto = 1L;

    final ResponseEntity<ConceptoGasto> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_PARAMETER_REACTIVAR, HttpMethod.PATCH, buildRequest(null, null),
        ConceptoGasto.class, idConceptoGasto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConceptoGasto conceptoGasto = response.getBody();
    Assertions.assertThat(conceptoGasto.getId()).as("getId()").isNotNull();
    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGasto.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("nombre-001");
    Assertions.assertThat(conceptoGasto.getDescripcion()).as("descripcion-001")
        .isEqualTo(conceptoGasto.getDescripcion());
    Assertions.assertThat(conceptoGasto.getActivo()).as("getActivo()").isTrue();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findById_ReturnsConceptoGasto() throws Exception {
    Long idConceptoGasto = 1L;

    final ResponseEntity<ConceptoGasto> response = restTemplate.exchange(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID,
        HttpMethod.GET, buildRequest(null, null), ConceptoGasto.class, idConceptoGasto);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    ConceptoGasto conceptoGasto = response.getBody();
    Assertions.assertThat(conceptoGasto.getId()).as("getId()").isNotNull();
    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGasto.getNombre(), Language.ES)).as("getNombre()")
        .isEqualTo("nombre-001");
    Assertions.assertThat(conceptoGasto.getDescripcion()).as("descripcion-001")
        .isEqualTo(conceptoGasto.getDescripcion());
    Assertions.assertThat(conceptoGasto.getActivo()).as("getActivo()").isTrue();
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAll_WithPagingSortingAndFiltering_ReturnsConceptoGastoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "descripcion.value=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH).queryParam("s", sort).queryParam("q", filter)
        .build(false).toUri();

    final ResponseEntity<List<ConceptoGasto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConceptoGasto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConceptoGasto> conceptoGastos = response.getBody();
    Assertions.assertThat(conceptoGastos).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGastos.get(0).getNombre(), Language.ES))
        .as("get(0).getNombre())").isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGastos.get(1).getNombre(), Language.ES))
        .as("get(1).getNombre())").isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGastos.get(2).getNombre(), Language.ES))
        .as("get(2).getNombre())").isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllTodos_WithPagingSortingAndFiltering_ReturnsConceptoGastoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "descripcion.value=ke=00";

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + "/todos").queryParam("s", sort)
        .queryParam("q", filter).build(false).toUri();

    final ResponseEntity<List<ConceptoGasto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConceptoGasto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConceptoGasto> conceptoGastos = response.getBody();
    Assertions.assertThat(conceptoGastos).hasSize(3);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("3");

    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGastos.get(0).getNombre(), Language.ES))
        .as("get(0).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 3));
    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGastos.get(1).getNombre(), Language.ES))
        .as("get(1).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGastos.get(2).getNombre(), Language.ES))
        .as("get(2).getNombre())")
        .isEqualTo("nombre-" + String.format("%03d", 1));
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:scripts/concepto_gasto.sql")
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findAllNotInAgrupacion_WithPagingSortingAndFiltering_ReturnsConceptoGastoSubList() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Page", "0");
    headers.add("X-Page-Size", "10");
    String sort = "id,desc";
    String filter = "descripcion.value=ke=00";

    Long proyectoId = 1L;

    URI uri = UriComponentsBuilder.fromUriString(CONTROLLER_BASE_PATH + PATH_PARAMETER_ID + PATH_NO_PROYECTO_AGRUPACION)
        .queryParam("s", sort)
        .queryParam("q", filter).buildAndExpand(proyectoId).toUri();

    final ResponseEntity<List<ConceptoGasto>> response = restTemplate.exchange(uri, HttpMethod.GET,
        buildRequest(headers, null), new ParameterizedTypeReference<List<ConceptoGasto>>() {
        });

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    final List<ConceptoGasto> conceptoGastos = response.getBody();
    Assertions.assertThat(conceptoGastos).hasSize(2);
    HttpHeaders responseHeaders = response.getHeaders();
    Assertions.assertThat(responseHeaders.getFirst("X-Page")).as("X-Page").isEqualTo("0");
    Assertions.assertThat(responseHeaders.getFirst("X-Page-Size")).as("X-Page-Size").isEqualTo("10");
    Assertions.assertThat(responseHeaders.getFirst("X-Total-Count")).as("X-Total-Count").isEqualTo("2");

    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGastos.get(0).getNombre(), Language.ES))
        .as("get(0).getNombre())").isEqualTo("nombre-" + String.format("%03d", 2));
    Assertions.assertThat(I18nHelper.getValueForLanguage(conceptoGastos.get(1).getNombre(), Language.ES))
        .as("get(1).getNombre())").isEqualTo("nombre-" + String.format("%03d", 1));
  }

  /**
   * Función que devuelve un objeto ConceptoGasto
   * 
   * @param id id del ConceptoGasto
   * @return el objeto ConceptoGasto
   */
  private ConceptoGasto generarMockConceptoGasto(Long id) {
    return generarMockConceptoGasto(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto ConceptoGasto
   * 
   * @param id     id del ConceptoGasto
   * @param nombre nombre del ConceptoGasto
   * @return el objeto ConceptoGasto
   */
  private ConceptoGasto generarMockConceptoGasto(Long id, String nombre) {
    Set<ConceptoGastoNombre> nombreConceptoGasto = new HashSet<>();
    nombreConceptoGasto.add(new ConceptoGastoNombre(Language.ES, nombre));

    Set<ConceptoGastoDescripcion> descripcionConceptoGasto = new HashSet<>();
    descripcionConceptoGasto.add(new ConceptoGastoDescripcion(Language.ES, "descripcion-" + id));

    ConceptoGasto conceptoGasto = new ConceptoGasto();
    conceptoGasto.setId(id);
    conceptoGasto.setNombre(nombreConceptoGasto);
    conceptoGasto.setDescripcion(descripcionConceptoGasto);
    conceptoGasto.setActivo(true);
    conceptoGasto.setCostesIndirectos(true);

    return conceptoGasto;
  }

}
