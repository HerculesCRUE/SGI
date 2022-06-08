
package org.crue.hercules.sgi.prc.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.dto.EpigrafeCVNOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiFullOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Test de integracion de ProduccionCientifica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProduccionCientificaApiIT extends ProduccionCientificaBaseIT {

  // @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts =
  // "classpath:cleanup.sql")
  // @Test
  void create_ReturnsProduccionCientifica_load_test() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    String idRef = produccionCientifica.getIdRef();
    IntStream.range(0, 40000)
        .forEach(i -> {
          produccionCientifica.setIdRef(idRef + "_" + i);
          try {
            createProduccionCientifica(produccionCientifica);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  private void createProduccionCientifica(ProduccionCientificaApiCreateInput produccionCientifica) throws Exception {
    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH_API,
        HttpMethod.POST,
        buildRequestProduccionCientificaApi(null, produccionCientifica), ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProduccionCientificaApiFullOutput produccionCientificaCreado = response.getBody();
    Assertions.assertThat(produccionCientificaCreado.getIdRef()).as("getIdRef()")
        .isEqualTo(produccionCientifica.getIdRef());

    Long produccionCientificaId = getProduccionCientificaRepository()
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaCreado.getIdRef()).get()
        .getId();
    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    int numCampos = campos.size();
    Assertions.assertThat(numCampos).as("number of campos created").isEqualTo(3);

    campos.stream().forEach(campo -> {
      int numElements = getValorCampoRepository().findAllByCampoProduccionCientificaId(campo.getId()).size();
      Assertions
          .assertThat(numElements)
          .as(String.format("number of valores created of campo [%s]", campo.getCodigoCVN().getCode()))
          .isEqualTo(2);
    });
    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(3);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(3);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(3);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProduccionCientifica() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();

    createProduccionCientifica(produccionCientifica);

  }

  @Test
  void create_ReturnsValidationExceptionProduccionCientificaRefEmpty() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.setIdRef(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionCampoEmpty() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.setCampos(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionCampoInvalid() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getCampos().get(0).setCodigoCVN("NOT_VALID");

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionCampoRequired() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getCampos().get(0).setCodigoCVN(CodigoCVN.E060_010_010_300.getCode());

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionValorCampoEmpty() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getCampos().forEach(campo -> campo.setValores(null));

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionCampoDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getCampos().add(produccionCientifica.getCampos().get(0));

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionAutorDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().add(produccionCientifica.getAutores().get(0));

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionAutorFirmaEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().get(0).setFirma(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionAutorPersonaRefEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().get(1).setPersonaRef(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionAutorNombreEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().get(2).setNombre(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionAutorFirmaDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().get(1).setPersonaRef(null);
    produccionCientifica.getAutores().get(1).setFirma(produccionCientifica.getAutores().get(0).getFirma());

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionAutorPersonaRefDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAutores().get(0).setFirma(null);
    produccionCientifica.getAutores().get(0).setPersonaRef(produccionCientifica.getAutores().get(1).getPersonaRef());

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionIndiceImpactoTipoFuenteEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getIndicesImpacto().get(0).setFuenteImpacto(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionIndiceImpactoTipoFuenteDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getIndicesImpacto().add(produccionCientifica.getIndicesImpacto().get(0));
    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionAcreditacionUrlEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAcreditaciones().get(0).setUrl(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionAcreditacionDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAcreditaciones().add(produccionCientifica.getAcreditaciones().get(0));
    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionAcreditacionDuplicated2() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getAcreditaciones().get(0).setUrl(null);
    produccionCientifica.getAcreditaciones().get(0).setDocumentoRef(
        produccionCientifica.getAcreditaciones().get(1).getDocumentoRef());

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionProyectoRefEmpty() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getProyectos().clear();
    produccionCientifica.getProyectos().add(null);

    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Test
  void create_ReturnsValidationExceptionProyectoDuplicated() throws Exception {
    ProduccionCientificaApiInput produccionCientifica = generarMockProduccionCientificaApiInput();
    produccionCientifica.getProyectos().add(produccionCientifica.getProyectos().get(0));
    callCreateAndValidateResponse(produccionCientifica, HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_prc_publicacion_libro_from_json() throws Exception {

    String produccionCientificaJson = "publicacion-libro.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    Long produccionCientificaId = createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores,
        numIndicesImpacto);
    assertNotNull(produccionCientificaId);

    checkNumAcreditacionesAndProyectosCreated(produccionCientificaId, 1, 1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_prc_publicacion_articulo_from_json() throws Exception {
    String produccionCientificaJson = "publicacion-articulo.json";
    Integer numCampos = 18;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    Long produccionCientificaId = createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores,
        numIndicesImpacto);
    assertNotNull(produccionCientificaId);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.INDICE_NORMALIZADO, "000000000000001.50");

    checkNumAcreditacionesAndProyectosCreated(produccionCientificaId, 1, 1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_prc_congreso_from_json() throws Exception {
    String produccionCientificaJson = "congreso.json";
    Integer numCampos = 11;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    Long produccionCientificaId = createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores,
        numIndicesImpacto);
    assertNotNull(produccionCientificaId);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_020_080, "010");

    checkNumAcreditacionesAndProyectosCreated(produccionCientificaId, 1, 1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_prc_comite_editorial_from_json() throws Exception {
    String produccionCientificaJson = "comite-editorial.json";
    Integer numCampos = 4;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 2;

    Long produccionCientificaId = createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores,
        numIndicesImpacto);
    assertNotNull(produccionCientificaId);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_030_030_020, "724");

    checkNumAcreditacionesAndProyectosCreated(produccionCientificaId, 1, 1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_prc_direccion_tesis_from_json() throws Exception {

    String produccionCientificaJson = "direccion-tesis.json";
    Integer numCampos = 9;
    Integer numAutores = 2;
    Integer numIndicesImpacto = 0;

    Long produccionCientificaId = createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores,
        numIndicesImpacto);
    assertNotNull(produccionCientificaId);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E030_040_000_170, "true");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E030_040_000_190, "false");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E030_040_000_010, "067");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.MENCION_INDUSTRIAL, "true");

    checkNumAcreditacionesAndProyectosCreated(produccionCientificaId, 1, 1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_prc_obra_artistica_from_json() throws Exception {

    String produccionCientificaJson = "obra-artistica.json";
    Integer numCampos = 10;
    Integer numAutores = 3;
    Integer numIndicesImpacto = 0;

    Long produccionCientificaId = createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores,
        numIndicesImpacto);
    assertNotNull(produccionCientificaId);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.TIPO_OBRA, "EXPOSICION");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E050_020_030_040, "724");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E050_020_030_050, "ES12");

    checkNumAcreditacionesAndProyectosCreated(produccionCientificaId, 1, 1);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_prc_organizacion_actividad_from_json() throws Exception {
    String produccionCientificaJson = "organizacion-actividad.json";
    Integer numCampos = 5;
    Integer numAutores = 1;
    Integer numIndicesImpacto = 0;

    Long produccionCientificaId = createProduccionCientificaFromJson(produccionCientificaJson, numCampos, numAutores,
        numIndicesImpacto);
    assertNotNull(produccionCientificaId);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_020_030_030, "724");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_020_030_110, "ORGANIZATIVO_COMITE");

    checkNumAcreditacionesAndProyectosCreated(produccionCientificaId, 1, 1);
  }

  private void callCreateAndValidateResponse(ProduccionCientificaApiInput produccionCientifica, HttpStatus httpStatus)
      throws Exception {
    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API,
        HttpMethod.POST, buildRequestProduccionCientificaApi(null, produccionCientifica),
        ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(httpStatus);
  }

  /**************************
   * DELETE
   **************************/

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/configuracion_baremo.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void deleteByProduccionCientificaRef_Return204() throws Exception {
    String produccionCientificaRef = PRODUCCION_CIENTIFICA_REF_VALUE + "001";
    // when: exists by produccionCientificaRef
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF,
        HttpMethod.DELETE, buildRequestProduccionCientificaApi(null, null), Void.class, produccionCientificaRef);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void delete_ReturnProduccionCientificaRefNotFoundException() throws Exception {
    String produccionCientificaRef = "ANY";

    // when: exists by produccionCientificaRef
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF,
        HttpMethod.DELETE, buildRequestProduccionCientificaApi(null, null), Void.class, produccionCientificaRef);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    // @formatter:off
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
    // @formatter:on
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void findListadoEpigrafes()
      throws Exception {
    final Long produccionCientificaId = 1L;
    // first page, 3 elements per page sorted
    HttpHeaders headers = new HttpHeaders();

    // when: find epigrafes
    URI uri = UriComponentsBuilder
        .fromUriString(CONTROLLER_BASE_PATH_API + "/epigrafes")
        .buildAndExpand(produccionCientificaId).toUri();

    final ResponseEntity<List<EpigrafeCVNOutput>> response = restTemplate.exchange(uri,
        HttpMethod.GET,
        buildRequestProduccionCientificaApi(headers, null),
        new ParameterizedTypeReference<List<EpigrafeCVNOutput>>() {
        });

    // given:
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    final List<EpigrafeCVNOutput> responseData = response.getBody();
    Assertions.assertThat(responseData).isNotNull();
  }
}
