package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.controller.ModuladorController;
import org.crue.hercules.sgi.prc.dto.ModuladorInput;
import org.crue.hercules.sgi.prc.dto.ModuladorOutput;
import org.crue.hercules.sgi.prc.model.Modulador.TipoModulador;
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

/**
 * Test de integracion de Modulador.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ModuladorIT extends BaseIT {

  protected static final String CONTROLLER_BASE_PATH = ModuladorController.MAPPING;
  protected static final String PATH_ID = ModuladorController.PATH_ID;
  protected static final String PATH_FIND_CONVOCATORIA_TIPO = ModuladorController.PATH_FIND_CONVOCATORIA_TIPO;

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

  @Test
  void find_without_data() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    final ResponseEntity<ModuladorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_FIND_CONVOCATORIA_TIPO, HttpMethod.GET,
        buildRequest(null, null, roles),
        ModuladorOutput.class, convocatoriaBaremacionId, TipoModulador.AREAS);

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
      "classpath:scripts/modulador.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void find_without_convocatoria() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 14L;

    final ResponseEntity<ModuladorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_FIND_CONVOCATORIA_TIPO, HttpMethod.GET,
        buildRequest(null, null, roles),
        ModuladorOutput.class, convocatoriaBaremacionId, TipoModulador.AREAS);

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
      "classpath:scripts/modulador.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void find_with_data() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    final ResponseEntity<List<ModuladorOutput>> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_FIND_CONVOCATORIA_TIPO, HttpMethod.GET,
        buildRequest(null, null, roles), new ParameterizedTypeReference<List<ModuladorOutput>>() {
        }, convocatoriaBaremacionId, TipoModulador.AREAS);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<ModuladorOutput> moduladores = response.getBody();
    int numModuladors = moduladores.size();
    Assertions.assertThat(numModuladors).as("numModuladors").isEqualTo(5);

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
      "classpath:scripts/modulador.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_without_convocatoria() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 144L;

    ModuladorInput input = ModuladorInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .areaRef("H")
        .valor1(new BigDecimal(1))
        .valor2(new BigDecimal(1))
        .valor3(new BigDecimal(1))
        .valor4(new BigDecimal(1))
        .valor5(new BigDecimal(1))
        .tipo(TipoModulador.AREAS)
        .build();

    final ResponseEntity<ModuladorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.PUT,
        buildRequest(null, input, roles),
        ModuladorOutput.class, convocatoriaBaremacionId, TipoModulador.AREAS);

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
      "classpath:scripts/modulador.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_and_delete_ok() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    ModuladorInput input = ModuladorInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .areaRef("H1")
        .valor1(new BigDecimal(1))
        .valor2(new BigDecimal(1))
        .valor3(new BigDecimal(1))
        .valor4(new BigDecimal(1))
        .valor5(new BigDecimal(1))
        .tipo(TipoModulador.AREAS)
        .build();

    final ResponseEntity<ModuladorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles),
        ModuladorOutput.class, convocatoriaBaremacionId, TipoModulador.AREAS);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    final ResponseEntity<ModuladorOutput> responseGet = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.GET,
        buildRequest(null, null, roles),
        ModuladorOutput.class, response.getBody().getId());

    Assertions.assertThat(responseGet.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(responseGet.getBody().getId()).isNotNull();

    final ResponseEntity<ModuladorOutput> responseDelete = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.DELETE,
        buildRequest(null, null, roles),
        ModuladorOutput.class, response.getBody().getId());

    Assertions.assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    final ResponseEntity<ModuladorOutput> responseGetAfterDelete = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.GET,
        buildRequest(null, null, roles),
        ModuladorOutput.class, response.getBody().getId());

    Assertions.assertThat(responseGetAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
      "classpath:scripts/modulador.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_validation_ko() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    ModuladorInput input = ModuladorInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .valor1(new BigDecimal(1))
        .valor2(new BigDecimal(1))
        .valor3(new BigDecimal(1))
        .valor4(new BigDecimal(1))
        .valor5(new BigDecimal(1))
        .tipo(TipoModulador.AREAS)
        .build();

    final ResponseEntity<ModuladorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles),
        ModuladorOutput.class, convocatoriaBaremacionId, TipoModulador.AREAS);

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
      "classpath:scripts/modulador.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ko_repeated() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    ModuladorInput input = ModuladorInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .areaRef("H")
        .valor1(new BigDecimal(1))
        .valor2(new BigDecimal(1))
        .valor3(new BigDecimal(1))
        .valor4(new BigDecimal(1))
        .valor5(new BigDecimal(1))
        .tipo(TipoModulador.AREAS)
        .build();

    final ResponseEntity<ModuladorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH, HttpMethod.POST,
        buildRequest(null, input, roles),
        ModuladorOutput.class, convocatoriaBaremacionId, TipoModulador.AREAS);

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
      "classpath:scripts/modulador.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ok() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    ModuladorInput input = ModuladorInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .areaRef("H")
        .valor1(new BigDecimal(1))
        .valor2(new BigDecimal(1))
        .valor3(new BigDecimal(1))
        .valor4(new BigDecimal(1))
        .valor5(new BigDecimal(1))
        .tipo(TipoModulador.AREAS)
        .build();

    final ResponseEntity<ModuladorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.PUT,
        buildRequest(null, input, roles),
        ModuladorOutput.class, convocatoriaBaremacionId, TipoModulador.AREAS);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
      "classpath:scripts/modulador.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_validation_ko() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    ModuladorInput input = ModuladorInput.builder()
        .id(null)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .valor1(new BigDecimal(1))
        .valor2(new BigDecimal(1))
        .valor3(new BigDecimal(1))
        .valor4(new BigDecimal(1))
        .valor5(new BigDecimal(1))
        .tipo(TipoModulador.AREAS)
        .build();

    final ResponseEntity<ModuladorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.PUT,
        buildRequest(null, input, roles),
        ModuladorOutput.class, convocatoriaBaremacionId, TipoModulador.AREAS);

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
      "classpath:scripts/modulador.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_validation_ko2() throws Exception {
    String roles = "PRC-CON-E";
    Long convocatoriaBaremacionId = 1L;

    ModuladorInput input = ModuladorInput.builder()
        .id(null)
        .areaRef("H")
        .valor1(new BigDecimal(1))
        .valor2(new BigDecimal(1))
        .valor3(new BigDecimal(1))
        .valor4(new BigDecimal(1))
        .valor5(new BigDecimal(1))
        .tipo(TipoModulador.AREAS)
        .build();

    final ResponseEntity<ModuladorOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_ID, HttpMethod.PUT,
        buildRequest(null, input, roles),
        ModuladorOutput.class, convocatoriaBaremacionId, TipoModulador.AREAS);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

}
