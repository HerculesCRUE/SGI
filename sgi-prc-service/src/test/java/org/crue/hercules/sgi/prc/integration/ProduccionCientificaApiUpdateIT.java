
package org.crue.hercules.sgi.prc.integration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiCreateInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiFullOutput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.AcreditacionInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.CampoProduccionCientificaInput;
import org.crue.hercules.sgi.prc.dto.ProduccionCientificaApiInput.IndiceImpactoInput;
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
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
class ProduccionCientificaApiUpdateIT extends ProduccionCientificaBaseIT {

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

  @Test
  void update_ReturnsProduccionCientificaRefException() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();

    callUpdateAndValidateResponse(produccionCientifica, "NOT_VALID", HttpStatus.NOT_FOUND);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_ReturnsEstadoException() throws Exception {
    ProduccionCientificaApiCreateInput produccionCientifica = generarMockProduccionCientificaApiInput();

    callUpdateAndValidateResponse(produccionCientifica, PRODUCCION_CIENTIFICA_REF_VALUE + "003",
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_without_campos() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    produccionCientificaApiInput.setCampos(null);

    callUpdateAndValidateResponse(produccionCientificaApiInput, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_ko_without_campos_codigoCVN() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    produccionCientificaApiInput.getCampos().stream().forEach(campo -> campo.setCodigoCVN(null));

    callUpdateAndValidateResponse(produccionCientificaApiInput, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_ko_without_campos_valores() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    produccionCientificaApiInput.getCampos().stream().forEach(campo -> campo.setValores(null));

    callUpdateAndValidateResponse(produccionCientificaApiInput, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_ko_with_duplicated_campos() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    produccionCientificaApiInput.getCampos().addAll(produccionCientificaApiInput.getCampos());

    callUpdateAndValidateResponse(produccionCientificaApiInput, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_ko_with_campos_duplicated_valores() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    produccionCientificaApiInput.getCampos().stream().forEach(campo -> campo.getValores().addAll(campo.getValores()));

    callUpdateAndValidateResponse(produccionCientificaApiInput, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_without_autores() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    produccionCientificaApiInput.setAutores(null);

    callUpdateAndValidateResponse(produccionCientificaApiInput, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_ko_with_duplicated_autores() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    produccionCientificaApiInput.getAutores().addAll(produccionCientificaApiInput.getAutores());

    callUpdateAndValidateResponse(produccionCientificaApiInput, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_ko_with_duplicated_indices_impacto() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    produccionCientificaApiInput.getIndicesImpacto().addAll(produccionCientificaApiInput.getIndicesImpacto());

    callUpdateAndValidateResponse(produccionCientificaApiInput, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_ko_with_duplicated_acreditaciones() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    produccionCientificaApiInput.getAcreditaciones().addAll(produccionCientificaApiInput.getAcreditaciones());

    callUpdateAndValidateResponse(produccionCientificaApiInput, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_ko_with_duplicated_proyectos() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    produccionCientificaApiInput.getProyectos().addAll(produccionCientificaApiInput.getProyectos());

    callUpdateAndValidateResponse(produccionCientificaApiInput, produccionCientifica.getProduccionCientificaRef(),
        HttpStatus.BAD_REQUEST);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/acreditacion.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_without_changes() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF, HttpMethod.PUT,
        buildRequest(null, produccionCientificaApiInput),
        ProduccionCientificaApiFullOutput.class, produccionCientifica.getProduccionCientificaRef());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(7);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_030, "Título de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_140, "2021-01-20T00:00:00Z");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_210, "Nombre de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_010, "032");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_160, "84-20412147");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_390, "22932567");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(1);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(2);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/acreditacion.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_change_valor_campo() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    CodigoCVN codigoCVN = CodigoCVN.E060_010_010_030;
    final String newValor = "PRUEBA";
    changeValorCampo(produccionCientificaApiInput, codigoCVN, newValor);

    changeValorCampo(produccionCientificaApiInput, CodigoCVN.E060_010_010_140, "2021-01-10T00:00:00Z");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF, HttpMethod.PUT,
        buildRequest(null, produccionCientificaApiInput),
        ProduccionCientificaApiFullOutput.class, produccionCientifica.getProduccionCientificaRef());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(7);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_030, newValor);
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_140, "2021-01-10T00:00:00Z");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_210, "Nombre de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_010, "032");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_160, "84-20412147");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_390, "22932567");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(1);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(2);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/acreditacion.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_change_valor_one_campo() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);

    CodigoCVN codigoCVN = CodigoCVN.E060_010_010_030;
    CampoProduccionCientificaInput campo = findCamposByCodigoCVN(produccionCientificaApiInput, codigoCVN);

    produccionCientificaApiInput.getCampos().clear();
    produccionCientificaApiInput.getCampos().addAll(Arrays.asList(campo));

    final String newValor = "PRUEBA";
    changeValorCampo(produccionCientificaApiInput, codigoCVN, newValor);

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF, HttpMethod.PUT,
        buildRequest(null, produccionCientificaApiInput),
        ProduccionCientificaApiFullOutput.class, produccionCientifica.getProduccionCientificaRef());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(7);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_030, newValor);
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_140, "2021-01-20T00:00:00Z");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_210, "Nombre de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_010, "032");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_160, "84-20412147");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_390, "22932567");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(1);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(2);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/acreditacion.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_delete_campo() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    CodigoCVN codigoCVN = CodigoCVN.E060_010_010_030;
    produccionCientificaApiInput.setCampos(findCamposWithoutCampo(produccionCientificaApiInput, codigoCVN));

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF, HttpMethod.PUT,
        buildRequest(null, produccionCientificaApiInput),
        ProduccionCientificaApiFullOutput.class, produccionCientifica.getProduccionCientificaRef());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(7);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_030, "Título de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_140, "2021-01-20T00:00:00Z");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_210, "Nombre de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_010, "032");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_160, "84-20412147");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_390, "22932567");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(1);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(2);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/acreditacion.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_add_campo() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);
    CodigoCVN codigoCVN = CodigoCVN.E060_010_010_030;
    produccionCientificaApiInput.setCampos(findCamposWithoutCampo(produccionCientificaApiInput, codigoCVN));

    CampoProduccionCientificaInput newCampo = CampoProduccionCientificaInput.builder()
        .codigoCVN(CodigoCVN.E060_010_020_080.getCode())
        .valores(Arrays.asList("020"))
        .build();
    produccionCientificaApiInput.getCampos().add(newCampo);

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF, HttpMethod.PUT,
        buildRequest(null, produccionCientificaApiInput),
        ProduccionCientificaApiFullOutput.class, produccionCientifica.getProduccionCientificaRef());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(8);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_020_080, "020");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_030, "Título de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_140, "2021-01-20T00:00:00Z");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_210, "Nombre de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_010, "032");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_160, "84-20412147");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_390, "22932567");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(1);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(2);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/acreditacion.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_change_indicesImpacto() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);

    IndiceImpactoInput indiceImpacto = IndiceImpactoInput.builder()
        .anio(2022)
        .fuenteImpacto(TipoFuenteImpacto.OTHERS.getCode())
        .build();
    produccionCientificaApiInput.getIndicesImpacto().add(indiceImpacto);

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF, HttpMethod.PUT,
        buildRequest(null, produccionCientificaApiInput),
        ProduccionCientificaApiFullOutput.class, produccionCientifica.getProduccionCientificaRef());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(7);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_030, "Título de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_140, "2021-01-20T00:00:00Z");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_210, "Nombre de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_010, "032");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_160, "84-20412147");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_390, "22932567");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(2);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(2);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/acreditacion.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_change_acreditaciones() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);

    AcreditacionInput acreditacion = AcreditacionInput.builder()
        .url("url3")
        .build();
    produccionCientificaApiInput.getAcreditaciones().add(acreditacion);

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF, HttpMethod.PUT,
        buildRequest(null, produccionCientificaApiInput),
        ProduccionCientificaApiFullOutput.class, produccionCientifica.getProduccionCientificaRef());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(7);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_030, "Título de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_140, "2021-01-20T00:00:00Z");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_210, "Nombre de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_010, "032");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_160, "84-20412147");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_390, "22932567");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(1);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(3);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(2);
  }

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/acreditacion.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/alias_enumerado.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  void update_prc_publicacion_change_proyectos() throws Exception {
    Long produccionCientificaId = 1L;

    ProduccionCientifica produccionCientifica = getProduccionCientificaRepository().findById(produccionCientificaId)
        .get();

    ProduccionCientificaApiInput produccionCientificaApiInput = createApiInputFromProduccionCientificaById(
        produccionCientificaId);

    produccionCientificaApiInput.getProyectos().add(33L);

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF, HttpMethod.PUT,
        buildRequest(null, produccionCientificaApiInput),
        ProduccionCientificaApiFullOutput.class, produccionCientifica.getProduccionCientificaRef());

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<CampoProduccionCientifica> campos = getCampoProduccionCientificaRepository()
        .findAllByProduccionCientificaId(produccionCientificaId);
    Assertions.assertThat(campos).as("number of campos created").hasSize(7);

    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_030, "Título de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_140, "2021-01-20T00:00:00Z");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_210, "Nombre de la publicación1");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_010, "032");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_160, "84-20412147");
    checkValueByCodigoCVN(produccionCientificaId, CodigoCVN.E060_010_010_390, "22932567");

    Assertions.assertThat(getAutorRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of autores created").hasSize(3);
    Assertions.assertThat(getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of indicesImpacto created").hasSize(1);
    Assertions.assertThat(getAcreditacionRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of acreditaciones created").hasSize(2);
    Assertions.assertThat(getProyectoRepository().findAllByProduccionCientificaId(produccionCientificaId))
        .as("number of proyectos created").hasSize(3);
  }

  private void changeValorCampo(ProduccionCientificaApiInput produccionCientificaApiInput, CodigoCVN codigoCVN,
      final String newValor) {
    produccionCientificaApiInput.getCampos().stream()
        .filter(campo -> campo.getCodigoCVN().equals(codigoCVN.getCode()))
        .forEach(campo -> {
          campo.getValores().clear();
          campo.getValores().addAll(Arrays.asList(newValor));
        });
  }

  private List<CampoProduccionCientificaInput> findCamposWithoutCampo(
      ProduccionCientificaApiInput produccionCientificaApiInput,
      CodigoCVN codigoCVN) {
    return produccionCientificaApiInput.getCampos().stream()
        .filter(campo -> !campo.getCodigoCVN().equals(codigoCVN.getCode()))
        .map(campo -> campo)
        .collect(Collectors.toList());
  }

  private CampoProduccionCientificaInput findCamposByCodigoCVN(
      ProduccionCientificaApiInput produccionCientificaApiInput,
      CodigoCVN codigoCVN) {
    return produccionCientificaApiInput.getCampos().stream()
        .filter(campo -> campo.getCodigoCVN().equals(codigoCVN.getCode()))
        .findFirst().orElse(null);
  }

  private void callUpdateAndValidateResponse(ProduccionCientificaApiInput produccionCientifica,
      String produccionCientificaRef,
      HttpStatus httpStatus) throws Exception {
    final ResponseEntity<ProduccionCientificaApiFullOutput> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH_API + PATH_PARAMETER_PRODUCCION_CIENTIFICA_REF, HttpMethod.PUT,
        buildRequest(null, produccionCientifica),
        ProduccionCientificaApiFullOutput.class, produccionCientificaRef);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(httpStatus);
  }

}
