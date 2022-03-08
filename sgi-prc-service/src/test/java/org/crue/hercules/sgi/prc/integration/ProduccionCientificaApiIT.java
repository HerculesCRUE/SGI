
package org.crue.hercules.sgi.prc.integration;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiFullOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de ProduccionCientifica.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProduccionCientificaApiIT extends ProduccionCientificaBaseIT {

  private HttpEntity<ProduccionCientificaApiInput> buildRequest(HttpHeaders headers,
      ProduccionCientificaApiInput entity)
      throws Exception {
    headers = (headers != null ? headers : new HttpHeaders());
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization",
        String.format("bearer %s",
            tokenBuilder.buildToken("user", "AUTH")));

    HttpEntity<ProduccionCientificaApiInput> request = new HttpEntity<>(entity, headers);
    return request;

  }

  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void create_ReturnsProduccionCientifica() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH_API,
        HttpMethod.POST, buildRequest(null, produccionCientifica), ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProduccionCientificaApiFullOutput produccionCientificaCreado = response.getBody();
    Assertions.assertThat(produccionCientificaCreado.getIdRef()).as("getIdRef()")
        .isEqualTo(produccionCientifica.getIdRef());

    Long produccionCientificaId = getProduccionCientificaRepository()
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaCreado.getIdRef()).get()
        .getId();
    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(3);

    campos.stream()
        .forEach(
            campo -> Assertions
                .assertThat(getValorCampoRepository().findAllByCampoProduccionCientificaId(campo.getId()))
                .as(String.format("number of valores created of campo [%s]", campo.getCodigoCVN().getInternValue()))
                .hasSize(2));
    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(3);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(3);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(3);
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
    ProduccionCientificaApiCreateInput produccionCientifica = getProduccionCientificaApiCreateInputFromJson(
        "prc/publicacion-libro.json");

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH_API,
        HttpMethod.POST, buildRequest(null, produccionCientifica), ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProduccionCientificaApiFullOutput produccionCientificaCreado = response.getBody();
    Assertions.assertThat(produccionCientificaCreado.getIdRef()).as("getIdRef()")
        .isEqualTo(produccionCientifica.getIdRef());

    Long produccionCientificaId = getProduccionCientificaRepository()
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaCreado.getIdRef()).get()
        .getId();
    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(18);

    // checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_140,
    // "2020-02-10T00:00:00Z");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(2);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(1);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(1);
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
    ProduccionCientificaApiCreateInput produccionCientifica = getProduccionCientificaApiCreateInputFromJson(
        "prc/publicacion-articulo.json");

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH_API,
        HttpMethod.POST, buildRequest(null, produccionCientifica), ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProduccionCientificaApiFullOutput produccionCientificaCreado = response.getBody();
    Assertions.assertThat(produccionCientificaCreado.getIdRef()).as("getIdRef()")
        .isEqualTo(produccionCientifica.getIdRef());

    Long produccionCientificaId = getProduccionCientificaRepository()
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaCreado.getIdRef()).get()
        .getId();
    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(18);

    // checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_140,
    // "2019-12-12T00:00:00Z");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.INDICE_NORMALIZADO, "000000000000001.50");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(2);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(1);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(1);
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
    ProduccionCientificaApiCreateInput produccionCientifica = getProduccionCientificaApiCreateInputFromJson(
        "prc/congreso.json");

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH_API,
        HttpMethod.POST, buildRequest(null, produccionCientifica), ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProduccionCientificaApiFullOutput produccionCientificaCreado = response.getBody();
    Assertions.assertThat(produccionCientificaCreado.getIdRef()).as("getIdRef()")
        .isEqualTo(produccionCientifica.getIdRef());

    Long produccionCientificaId = getProduccionCientificaRepository()
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaCreado.getIdRef()).get()
        .getId();
    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(11);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_020_080, "AMBITO.010");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(2);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(1);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(1);
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
    ProduccionCientificaApiCreateInput produccionCientifica = getProduccionCientificaApiCreateInputFromJson(
        "prc/comite-editorial.json");

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH_API,
        HttpMethod.POST, buildRequest(null, produccionCientifica), ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProduccionCientificaApiFullOutput produccionCientificaCreado = response.getBody();
    Assertions.assertThat(produccionCientificaCreado.getIdRef()).as("getIdRef()")
        .isEqualTo(produccionCientifica.getIdRef());

    Long produccionCientificaId = getProduccionCientificaRepository()
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaCreado.getIdRef()).get()
        .getId();
    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(4);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_030_030_020, "PAIS.724");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(2);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(1);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(1);
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
    ProduccionCientificaApiCreateInput produccionCientifica = getProduccionCientificaApiCreateInputFromJson(
        "prc/direccion-tesis.json");

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH_API,
        HttpMethod.POST, buildRequest(null, produccionCientifica), ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProduccionCientificaApiFullOutput produccionCientificaCreado = response.getBody();
    Assertions.assertThat(produccionCientificaCreado.getIdRef()).as("getIdRef()")
        .isEqualTo(produccionCientifica.getIdRef());

    Long produccionCientificaId = getProduccionCientificaRepository()
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaCreado.getIdRef()).get()
        .getId();
    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(10);

    // checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E030_040_000_140,
    // "2021-02-17T00:00:00Z");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E030_040_000_170, "true");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E030_040_000_190, "false");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E030_040_000_160, "");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E030_040_000_010, "067");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.MENCION_INDUSTRIAL, "true");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(2);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").isEmpty();
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(1);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(1);
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
    ProduccionCientificaApiCreateInput produccionCientifica = getProduccionCientificaApiCreateInputFromJson(
        "prc/obra-artistica.json");

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH_API,
        HttpMethod.POST, buildRequest(null, produccionCientifica), ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProduccionCientificaApiFullOutput produccionCientificaCreado = response.getBody();
    Assertions.assertThat(produccionCientificaCreado.getIdRef()).as("getIdRef()")
        .isEqualTo(produccionCientifica.getIdRef());

    Long produccionCientificaId = getProduccionCientificaRepository()
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaCreado.getIdRef()).get()
        .getId();
    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(10);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.TIPO_OBRA, "EXPOSICION");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E050_020_030_040, "PAIS.724");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E050_020_030_050, "COMUNIDAD.ES12");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").isEmpty();
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(1);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(1);
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
    ProduccionCientificaApiCreateInput produccionCientifica = getProduccionCientificaApiCreateInputFromJson(
        "prc/organizacion-actividad.json");

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(CONTROLLER_BASE_PATH_API,
        HttpMethod.POST, buildRequest(null, produccionCientifica), ProduccionCientificaApiFullOutput.class);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ProduccionCientificaApiFullOutput produccionCientificaCreado = response.getBody();
    Assertions.assertThat(produccionCientificaCreado.getIdRef()).as("getIdRef()")
        .isEqualTo(produccionCientifica.getIdRef());

    Long produccionCientificaId = getProduccionCientificaRepository()
        .findByProduccionCientificaRefAndConvocatoriaBaremacionIdIsNull(produccionCientificaCreado.getIdRef()).get()
        .getId();
    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(5);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_020_030_030, "PAIS.724");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_020_030_110, "ORGANIZATIVO_COMITE");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(1);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").isEmpty();
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(1);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(1);
  }

  private void callCreateAndValidateResponse(ProduccionCientificaApiInput produccionCientifica, HttpStatus httpStatus)
      throws Exception {
    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API,
        HttpMethod.POST, buildRequest(null, produccionCientifica),
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
        HttpMethod.DELETE, buildRequest(null, null), Void.class, produccionCientificaRef);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void delete_ReturnProduccionCientificaRefNotFoundException() throws Exception {
    String produccionCientificaRef = "ANY";

    // when: exists by produccionCientificaRef
    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF,
        HttpMethod.DELETE, buildRequest(null, null), Void.class, produccionCientificaRef);
    // then: 204 NO CONTENT
    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
