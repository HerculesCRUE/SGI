package org.crue.hercules.sgi.eti.converter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.dto.EvaluacionWithIsEliminable;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * EvaluacionConverterTest
 */
public class EvaluacionConverterTest extends BaseServiceTest {
  @Mock
  private ComentarioRepository comentarioRepository;

  private EvaluacionConverter evaluacionConverter;

  @BeforeEach
  public void setUp() throws Exception {
    evaluacionConverter = new EvaluacionConverter(comentarioRepository);
  }

  @Test
  public void evaluacionToEvaluacionWithIsEliminable_ReturnOk() {

    Evaluacion evaluacion = generarMockEvaluacion(1L, "Eva1", 1L, 1L);

    EvaluacionWithIsEliminable evaluacionWithIsEliminable = evaluacionConverter
        .evaluacionToEvaluacionWithIsEliminable(evaluacion);

    Assertions.assertThat(evaluacionWithIsEliminable).isNotNull();
    Assertions.assertThat(evaluacionWithIsEliminable.getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacionWithIsEliminable.getMemoria().getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacionWithIsEliminable.getConvocatoriaReunion().getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacionWithIsEliminable.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");
    Assertions.assertThat(evaluacionWithIsEliminable.getDictamen().getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacionWithIsEliminable.getEvaluador1().getId()).isEqualTo(1L);
    Assertions.assertThat(evaluacionWithIsEliminable.getEvaluador2().getId()).isEqualTo(2L);
    Assertions.assertThat(evaluacionWithIsEliminable.getFechaDictamen()).isEqualTo(evaluacion.getFechaDictamen());
    Assertions.assertThat(evaluacionWithIsEliminable.getVersion()).isEqualTo(2);
    Assertions.assertThat(evaluacionWithIsEliminable.getEsRevMinima()).isEqualTo(Boolean.TRUE);
    Assertions.assertThat(evaluacionWithIsEliminable.getActivo()).isEqualTo(Boolean.TRUE);
  }

  @Test
  public void evaluacionToEvaluacionWithIsEliminable_EvaluacionNull_ReturnNull() {

    EvaluacionWithIsEliminable evaluacionWithIsEliminable = evaluacionConverter
        .evaluacionToEvaluacionWithIsEliminable(null);

    Assertions.assertThat(evaluacionWithIsEliminable).isNull();
  }

  @Test
  public void evalucionIsEliminable_FechaConvocatoriaInferiorActual_ReturnFalse() {
    // La fecha de la convocatoria es anterior a la actual
    Evaluacion evaluacion = generarMockEvaluacion(1L, "Eva1", 1L, 1L);

    evaluacionConverter.isEliminable(evaluacion);

    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getFechaEvaluacion()).isBefore(Instant.now());
  }

  @Test
  public void evalucionIsEliminable_WithDictamen_ReturnFalse() {
    // La evaluación tiene un dictamen.
    Evaluacion evaluacion = generarMockEvaluacion(1L, "Eva1", 1L, 1L);
    // La fecha de la convocatoria es posterior a la actual
    Instant today = Instant.now();
    evaluacion.getConvocatoriaReunion().setFechaEvaluacion(Instant.from(today.atZone(ZoneOffset.UTC).plusYears(1)));

    evaluacionConverter.isEliminable(evaluacion);

    Assertions.assertThat(evaluacion.getDictamen()).isNotNull();
  }

  @Test
  public void evalucionIsEliminable_FechaConvocatoriaIsAfterToFechaActual_WithoutDictamen_WithoutComentarios_ReturnTrue() {

    Evaluacion evaluacion = generarMockEvaluacion(1L, "Eva1", 1L, 1L);

    Instant today = Instant.now();
    evaluacion.getConvocatoriaReunion().setFechaEvaluacion(Instant.from(today.atZone(ZoneOffset.UTC).plusYears(1)));
    evaluacion.setDictamen(null);

    evaluacionConverter.isEliminable(evaluacion);

    Assertions.assertThat(evaluacion.getConvocatoriaReunion().getFechaEvaluacion()).isAfter(Instant.now());
    Assertions.assertThat(evaluacion.getDictamen()).isNull();
    Assertions.assertThat(comentarioRepository.countByEvaluacionId(evaluacion.getId())).isEqualTo(0);
  }

  @Test
  public void evaluacionesToEvaluacionesWithIsEliminable_ReturnEvaluacionWithIsEliminableList() {
    ArrayList<Evaluacion> evaluaciones = new ArrayList<>();
    evaluaciones.add(generarMockEvaluacion(1L, "Eva1", 1L, 1L));
    evaluaciones.add(generarMockEvaluacion(2L, "Eva2", 1L, 1L));

    List<EvaluacionWithIsEliminable> result = evaluacionConverter
        .evaluacionesToEvaluacionesWithIsEliminable(evaluaciones);

    Assertions.assertThat(result.size()).isEqualTo(evaluaciones.size());

  }

  @Test
  public void evaluacionesToEvaluacionesWithIsEliminable_ReturnArrayList() {
    List<Evaluacion> evaluaciones = null;

    List<EvaluacionWithIsEliminable> result = evaluacionConverter
        .evaluacionesToEvaluacionesWithIsEliminable(evaluaciones);

    Assertions.assertThat(result.size()).isEqualTo(0);

  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param id                    id del Evaluacion
   * @param sufijo                el sufijo para título y nombre
   * @param idTipoEstadoMemoria   id del tipo de estado de la memoria
   * @param idEstadoRetrospectiva id del estado de la retrospectiva
   * @return el objeto Evaluacion
   */

  public Evaluacion generarMockEvaluacion(Long id, String sufijo, Long idTipoEstadoMemoria,
      Long idEstadoRetrospectiva) {

    String sufijoStr = (sufijo == null ? id.toString() : sufijo);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre("Dictamen" + sufijoStr);
    dictamen.setActivo(Boolean.TRUE);

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(1L);
    tipoActividad.setNombre("TipoActividad1");
    tipoActividad.setActivo(Boolean.TRUE);

    PeticionEvaluacion peticionEvaluacion = new PeticionEvaluacion();
    peticionEvaluacion.setId(id);
    peticionEvaluacion.setCodigo("Codigo1");
    peticionEvaluacion.setDisMetodologico("DiseñoMetodologico1");
    peticionEvaluacion.setExterno(Boolean.FALSE);
    peticionEvaluacion.setFechaFin(Instant.now());
    peticionEvaluacion.setFechaInicio(Instant.now());
    peticionEvaluacion.setExisteFinanciacion(false);
    peticionEvaluacion.setObjetivos("Objetivos1");
    peticionEvaluacion.setResumen("Resumen");
    peticionEvaluacion.setSolicitudConvocatoriaRef("Referencia solicitud convocatoria");
    peticionEvaluacion.setTieneFondosPropios(Boolean.FALSE);
    peticionEvaluacion.setTipoActividad(tipoActividad);
    peticionEvaluacion.setTitulo("PeticionEvaluacion1");
    peticionEvaluacion.setPersonaRef("user-001");
    peticionEvaluacion.setValorSocial(TipoValorSocial.ENSENIANZA_SUPERIOR);
    peticionEvaluacion.setActivo(Boolean.TRUE);

    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    Comite comite = new Comite(1L, "Comite1", "nombreSecretario", "nombreInvestigacion", "nombreDecreto", "articulo",
        formulario, Boolean.TRUE);

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(1L);
    tipoMemoria.setNombre("TipoMemoria1");
    tipoMemoria.setActivo(Boolean.TRUE);

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(idTipoEstadoMemoria);

    EstadoRetrospectiva estadoRetrospectiva = new EstadoRetrospectiva();
    estadoRetrospectiva.setId(idEstadoRetrospectiva);

    Memoria memoria = new Memoria(1L, "numRef-001", peticionEvaluacion, comite, "Memoria" + sufijoStr, "user-00" + id,
        tipoMemoria, tipoEstadoMemoria, Instant.now(), Boolean.TRUE,
        new Retrospectiva(id, estadoRetrospectiva, Instant.now()), 3, "CodOrganoCompetente", Boolean.TRUE, null);

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setId(1L);
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.parse("2020-05-10T00:00:00Z"));
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setLugar("Lugar");
    convocatoriaReunion.setOrdenDia("Orden del día convocatoria reunión");
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(Instant.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Evaluador evaluador1 = new Evaluador();
    evaluador1.setId(1L);

    Evaluador evaluador2 = new Evaluador();
    evaluador2.setId(2L);

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setId(id);
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(Boolean.TRUE);
    evaluacion.setFechaDictamen(Instant.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(2);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setEvaluador1(evaluador1);
    evaluacion.setEvaluador2(evaluador2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }
}