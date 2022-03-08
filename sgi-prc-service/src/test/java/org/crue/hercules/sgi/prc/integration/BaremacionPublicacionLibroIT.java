package org.crue.hercules.sgi.prc.integration;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

/**
 * Test de integracion de Baremacion de publicaciones libros.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaremacionPublicacionLibroIT extends BaremacionBaseIT {

  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
      // @formatter:off 
      "classpath:scripts/produccion_cientifica.sql",
      "classpath:scripts/campo_produccion_cientifica.sql",
      "classpath:scripts/valor_campo.sql",
      "classpath:scripts/autor.sql",
      "classpath:scripts/indice_impacto.sql",
      "classpath:scripts/configuracion_baremo.sql",
      "classpath:scripts/configuracion_campo.sql",
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 1L, 3L })
  @ParameterizedTest
  public void baremacion_publicacion_nacional(Long produccionCientificaId) throws Exception {
    Long idBaremacion = 1L;

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequest(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    Assertions.assertThat(puntuacionBaremoItems.size()).as("numPuntuaciones").isEqualTo(1);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("6.00"));
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 1L, 2L, 3L })
  @ParameterizedTest
  public void baremacion_publicacion_without_indice_impacto_anio(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setAnio(2100);
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithoutPuntuaciones(idBaremacion);
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 2L })
  @ParameterizedTest
  public void baremacion_publicacion_without_indice_impacto_tipo_fuente(Long produccionCientificaId)
      throws Exception {
    Long idBaremacion = 1L;

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    // Cambiamos el tipo de fuente para que no cumpla las condifiones
    updateTipoFuenteIndiceImpacto(produccionCientificaId, TipoFuenteImpacto.OTHERS);

    baremacionWithoutPuntuaciones(idBaremacion);
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 1L, 2L, 3L })
  @ParameterizedTest
  public void baremacion_publicacion_without_tipo_produccion(Long produccionCientificaId) throws Exception {
    Long idBaremacion = 1L;

    // Cambiamos el valor de tipo produccion para que no cumpla las condiciones
    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010, "xxx");

    baremacionWithoutPuntuaciones(idBaremacion);
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void baremacion_publicacion_nacional_and_extranjera() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    // AUTORIA_BCI_EDITORIAL_NACIONAL
    // AUTORIA_BCI_EDITORIAL_EXTRANJERA
    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);
    updateEstadoProduccionCientifica(2L, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(2L, CodigoCVN.E060_010_010_010, "032");

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequest(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    Assertions.assertThat(puntuacionBaremoItems.size()).as("numPuntuaciones").isEqualTo(2);
    int i = 0;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId" + i).isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos" + i)
        .isEqualTo(new BigDecimal("6.00"));
    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId" + i).isEqualTo(5L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos" + i)
        .isEqualTo(new BigDecimal("5.00"));
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @Test
  public void baremacion_publicacion_nacional_and_extranjera_2020_and_2021() throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);
    updateEstadoProduccionCientifica(2L, TipoEstadoProduccion.VALIDADO);
    updateEstadoProduccionCientifica(3L, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(2L, CodigoCVN.E060_010_010_010, "032");

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequest(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    Assertions.assertThat(puntuacionBaremoItems.size()).as("numPuntuaciones").isEqualTo(3);
    int i = 0;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId" + i).isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos" + i)
        .isEqualTo(new BigDecimal("6.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId" + i).isEqualTo(6L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos" + i)
        .isEqualTo(new BigDecimal("6.00"));

    i++;
    Assertions.assertThat(puntuacionBaremoItems.get(i).getBaremoId()).as("BaremoId" + i).isEqualTo(5L);
    Assertions.assertThat(puntuacionBaremoItems.get(i).getPuntos()).as("Puntos" + i)
        .isEqualTo(new BigDecimal("5.00"));
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "AUTORIA_BCI_EDITORIAL_EXTRANJERA#032#5",
      "CAP_LIBRO_BCI_EDITORIAL_EXTRANJERA#004#11",
      "EDICION_BCI_EDITORIAL_EXTRANJERA#208#17",
      "COMENTARIO_BCI_EDITORIAL_EXTRANJERA#COMENTARIO_SISTEMATICO_NORMAS#23" })
  @ParameterizedTest
  public void baremacion_publicacion_extranjera(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 2L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        arrParams[1]);

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "AUTORIA_BCI_EDITORIAL_NACIONAL#032#6",
      "CAP_LIBRO_BCI_EDITORIAL_NACIONAL#004#12",
      "EDICION_BCI_EDITORIAL_NACIONAL#208#18",
      "COMENTARIO_BCI_EDITORIAL_NACIONAL#COMENTARIO_SISTEMATICO_NORMAS#24"
  })
  @ParameterizedTest
  public void baremacion_publicacion_nacional(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        arrParams[1]);

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(longs = { 48L, 50L, 1L })
  @ParameterizedTest
  public void baremacion_publicacion_autoria_icee_q1(Long posicionPublicacion) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    // AUTORIA_ICEE_Q1
    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.ICEE);
      entity.setRevista25(Boolean.TRUE);
      getIndiceImpactoRepository().save(entity);
    });

    final ResponseEntity<Void> response = restTemplate.exchange(
        CONTROLLER_BASE_PATH + PATH_PARAMETER_ID, HttpMethod.POST, buildRequest(null, null),
        Void.class, idBaremacion);

    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    List<PuntuacionBaremoItem> puntuacionBaremoItems = getPuntuacionBaremoItemRepository().findAll();

    Assertions.assertThat(puntuacionBaremoItems.size()).as("numPuntuaciones").isEqualTo(1);

    Assertions.assertThat(puntuacionBaremoItems.get(0).getBaremoId()).as("BaremoId").isEqualTo(7L);
    Assertions.assertThat(puntuacionBaremoItems.get(0).getPuntos()).as("Puntos").isEqualTo(new BigDecimal("7.00"));
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "AUTORIA_Q1_RESTO_CUARTILES#032#7",
      "CAP_LIBRO_Q1_RESTO_CUARTILES#004#13",
      "EDICION_Q1_RESTO_CUARTILES#208#19",
      "COMENTARIO_Q1_RESTO_CUARTILES#COMENTARIO_SISTEMATICO_NORMAS#25" })
  @ParameterizedTest
  public void baremacion_publicacion_icee_q1(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        arrParams[1]);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.ICEE);
      entity.setPosicionPublicacion(new BigDecimal(50));
      entity.setNumeroRevistas(new BigDecimal("2"));
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "AUTORIA_ICEE_RESTO_CUARTILES#032#8",
      "CAP_LIBRO_ICEE_RESTO_CUARTILES#004#14",
      "EDICION_ICEE_RESTO_CUARTILES#208#20",
      "COMENTARIO_ICEE_RESTO_CUARTILES#COMENTARIO_SISTEMATICO_NORMAS#26" })
  @ParameterizedTest
  public void baremacion_publicacion_icee_resto_cuartiles(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        arrParams[1]);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.ICEE);
      entity.setPosicionPublicacion(new BigDecimal("55.0"));
      entity.setNumeroRevistas(new BigDecimal("2"));
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "AUTORIA_DIALNET#032#9",
      "CAP_LIBRO_DIALNET#004#15",
      "EDICION_DIALNET#208#21",
      "COMENTARIO_DIALNET#COMENTARIO_SISTEMATICO_NORMAS#27" })
  @ParameterizedTest
  public void baremacion_publicacion_dialnet(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        arrParams[1]);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.DIALNET);
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
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
      "classpath:scripts/convocatoria_baremacion.sql",
      "classpath:scripts/baremo.sql",
      "classpath:scripts/modulador.sql",
      "classpath:scripts/rango.sql",
      // @formatter:on  
  })
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:cleanup.sql")
  @ValueSource(strings = { "AUTORIA_OTRAS#032#10",
      "CAP_LIBRO_OTRAS#004#16",
      "EDICION_OTRAS#208#22",
      "COMENTARIO_OTRAS#COMENTARIO_SISTEMATICO_NORMAS#28" })
  @ParameterizedTest
  public void baremacion_publicacion_otras(String parameters) throws Exception {
    Long idBaremacion = 1L;
    Long produccionCientificaId = 1L;

    String[] arrParams = parameters.split("#");

    // AUTORIA_ICEE_OTRAS
    updateEstadoProduccionCientifica(produccionCientificaId, TipoEstadoProduccion.VALIDADO);

    updateValorCampoByCodigoCVNAndProduccionCientificaId(produccionCientificaId, CodigoCVN.E060_010_010_010,
        arrParams[1]);

    getIndiceImpactoRepository().findAllByProduccionCientificaId(produccionCientificaId).stream().forEach(entity -> {
      entity.setFuenteImpacto(TipoFuenteImpacto.OTHERS);
      getIndiceImpactoRepository().save(entity);
    });

    baremacionWithOnePuntuacion(idBaremacion, arrParams[2], arrParams[2] + ".00");
  }

}
