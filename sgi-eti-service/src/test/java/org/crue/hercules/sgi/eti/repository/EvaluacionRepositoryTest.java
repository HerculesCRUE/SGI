package org.crue.hercules.sgi.eti.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ComiteNombre;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunionLugar;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunionOrdenDia;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.EvaluadorResumen;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.MemoriaTitulo;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionDisMetodologico;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionFuenteFinanciacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionObjetivos;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionOtroValorSocial;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionResumen;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacionTitulo;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class EvaluacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private EvaluacionRepository repository;

  @Test
  public void findAllActivasByConvocatoriaReunionId_WithPaging_ReturnsPage() throws Exception {

    // given: Datos existentes con convocatoriaReunionId = 1

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion c1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion c2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());
    Dictamen dictamen = entityManager.persistFlushFind(generarMockDictamen(tipoEvaluacion));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    TipoInvestigacionTutelada tipoInvestigacionTutelada = entityManager
        .persistAndFlush(generarMockTipoInvestigacionTutelada());
    PeticionEvaluacion peticionEvaluacion = entityManager
        .persistAndFlush(generarMockPeticionEvaluacion(tipoActividad, tipoInvestigacionTutelada));
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoEstadoMemoria, retrospectiva, formulario));

    CargoComite cargoComite = entityManager.persistFlushFind(generarMockCargoComite(1L));
    Evaluador evaluador1 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));
    Evaluador evaluador2 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));

    List<Evaluacion> response = new LinkedList<Evaluacion>();
    response.add(entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2)));
    response.add(entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2)));
    response.add(entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2)));
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response.subList(2, 3), pageable, response.size());

    // when: Se buscan los datos paginados
    Page<Evaluacion> result = repository.findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(c1.getId(),
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getNumber()).isEqualTo(pageResponse.getNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageResponse.getSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(pageResponse.getTotalElements());
    Assertions.assertThat(result.getContent()).isEqualTo(pageResponse.getContent());
  }

  @Test
  public void findAllActivasByConvocatoriaReunionId_WithPaging_ReturnsEmptyPage() throws Exception {

    // given: Sin datos existentes con convocatoriaReunionId = 2

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion c1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion c2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());
    Dictamen dictamen = entityManager.persistFlushFind(generarMockDictamen(tipoEvaluacion));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    TipoInvestigacionTutelada tipoInvestigacionTutelada = entityManager
        .persistAndFlush(generarMockTipoInvestigacionTutelada());
    PeticionEvaluacion peticionEvaluacion = entityManager
        .persistAndFlush(generarMockPeticionEvaluacion(tipoActividad, tipoInvestigacionTutelada));
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoEstadoMemoria, retrospectiva, formulario));

    CargoComite cargoComite = entityManager.persistFlushFind(generarMockCargoComite(1L));
    Evaluador evaluador1 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));
    Evaluador evaluador2 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));

    List<Evaluacion> response = new LinkedList<Evaluacion>();
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));

    // página 1 con 2 elementos por página
    Pageable pageable = PageRequest.of(1, 2);
    Page<Evaluacion> pageResponse = new PageImpl<>(response, pageable, response.size());

    // when: Se buscan los datos paginados
    Page<Evaluacion> result = repository.findAllByActivoTrueAndConvocatoriaReunionIdAndEsRevMinimaFalse(c2.getId(),
        pageable);

    // then: Se recuperan los datos correctamente según la paginación solicitada
    Assertions.assertThat(result.getNumber()).isEqualTo(pageResponse.getNumber());
    Assertions.assertThat(result.getSize()).isEqualTo(pageResponse.getSize());
    Assertions.assertThat(result.getTotalElements()).isEqualTo(pageResponse.getTotalElements());
    Assertions.assertThat(result.getContent()).isEmpty();
  }

  @Test
  public void findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId_ReturnsList() throws Exception {

    // given: Datos existentes con convocatoriaReunionId = 1, tipoEvaluacionId= 1,
    // esRevMinima = true y activa.

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion c1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion c2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());
    Dictamen dictamen = entityManager.persistFlushFind(generarMockDictamen(tipoEvaluacion));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    TipoInvestigacionTutelada tipoInvestigacionTutelada = entityManager
        .persistAndFlush(generarMockTipoInvestigacionTutelada());
    PeticionEvaluacion peticionEvaluacion = entityManager
        .persistAndFlush(generarMockPeticionEvaluacion(tipoActividad, tipoInvestigacionTutelada));
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoEstadoMemoria, retrospectiva, formulario));

    CargoComite cargoComite = entityManager.persistFlushFind(generarMockCargoComite(1L));
    Evaluador evaluador1 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));
    Evaluador evaluador2 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));

    List<Evaluacion> response = new LinkedList<Evaluacion>();
    response.add(entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2)));
    response.add(entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2)));
    response.add(entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2)));
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));

    // when: Se buscan los datos
    List<Evaluacion> result = repository.findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
        tipoEvaluacion.getId(), Boolean.FALSE, c1.getId());

    // then: Se recuperan los datos correctamente
    Assertions.assertThat(result.size()).isEqualTo(response.size());

  }

  @Test
  public void findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId_ReturnsEmptyList()
      throws Exception {

    // given: Sin datos con convocatoriaReunionId = 1, tipoEvaluacionId= 1,
    // esRevMinima = true y activa.

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion c1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion c2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());
    Dictamen dictamen = entityManager.persistFlushFind(generarMockDictamen(tipoEvaluacion));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    TipoInvestigacionTutelada tipoInvestigacionTutelada = entityManager
        .persistAndFlush(generarMockTipoInvestigacionTutelada());
    PeticionEvaluacion peticionEvaluacion = entityManager
        .persistAndFlush(generarMockPeticionEvaluacion(tipoActividad, tipoInvestigacionTutelada));
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoEstadoMemoria, retrospectiva, formulario));

    CargoComite cargoComite = entityManager.persistFlushFind(generarMockCargoComite(1L));
    Evaluador evaluador1 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));
    Evaluador evaluador2 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));

    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 2));
    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 2));
    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 2));
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));
    entityManager.persist(
        generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.FALSE, 2));

    // when: Se buscan los datos
    List<Evaluacion> result = repository.findByActivoTrueAndTipoEvaluacionIdAndEsRevMinimaAndConvocatoriaReunionId(
        tipoEvaluacion.getId(), Boolean.FALSE, c1.getId());

    // then: Se comprueba que no se recupera ningún registro
    Assertions.assertThat(result.isEmpty()).isTrue();

  }

  @Test
  public void findFirstByMemoriaIdOrderByVersionDesc_ReturnsEvaluacion() throws Exception {

    // given: Datos con Memoria = 1 y Evaluacion activa.

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion c1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion c2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());
    Dictamen dictamen = entityManager.persistFlushFind(generarMockDictamen(tipoEvaluacion));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    TipoInvestigacionTutelada tipoInvestigacionTutelada = entityManager
        .persistAndFlush(generarMockTipoInvestigacionTutelada());
    PeticionEvaluacion peticionEvaluacion = entityManager
        .persistAndFlush(generarMockPeticionEvaluacion(tipoActividad, tipoInvestigacionTutelada));
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoEstadoMemoria, retrospectiva, formulario));

    CargoComite cargoComite = entityManager.persistFlushFind(generarMockCargoComite(1L));
    Evaluador evaluador1 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));
    Evaluador evaluador2 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));

    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 1));
    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 2));

    // when: Se buscan los datos
    Optional<Evaluacion> result = repository
        .findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(memoria.getId(), 1L);

    // then: Se comprueba que se recupera la misma memoria y la útlima versión
    Assertions.assertThat(result.get().getMemoria().getId()).isEqualTo(memoria.getId());
    Assertions.assertThat(result.get().getVersion()).isEqualTo(2);

  }

  @Test
  public void findFirstByMemoriaIdOrderByVersionDesc_ReturnsEmpty() throws Exception {

    // given: Datos con Memoria = 1 y Evaluacion activa.

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));
    TipoConvocatoriaReunion tipoConvocatoriaReunion = entityManager
        .persistFlushFind(generarMockTipoConvocatoriaReunion());
    ConvocatoriaReunion c1 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    ConvocatoriaReunion c2 = entityManager
        .persistFlushFind(generarMockConvocatoriaReunion(comite, tipoConvocatoriaReunion));
    TipoEvaluacion tipoEvaluacion = entityManager.persistFlushFind(generarMockTipoEvaluacion());
    Dictamen dictamen = entityManager.persistFlushFind(generarMockDictamen(tipoEvaluacion));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    TipoInvestigacionTutelada tipoInvestigacionTutelada = entityManager
        .persistAndFlush(generarMockTipoInvestigacionTutelada());
    PeticionEvaluacion peticionEvaluacion = entityManager
        .persistAndFlush(generarMockPeticionEvaluacion(tipoActividad, tipoInvestigacionTutelada));
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoEstadoMemoria, retrospectiva, formulario));

    CargoComite cargoComite = entityManager.persistFlushFind(generarMockCargoComite(1L));
    Evaluador evaluador1 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));
    Evaluador evaluador2 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));

    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 1));
    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 2));

    // then: Se comprueba que no recupera ninguna evaluación.
    Optional<Evaluacion> resultEmpty = repository
        .findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(111L, 1L);
    Assertions.assertThat(resultEmpty).isEmpty();

  }

  /**
   * Función que devuelve un objeto ConvocatoriaReunion
   * 
   * @param comite                  el objeto Comite
   * @param tipoConvocatoriaReunion el objeto TipoConvocatoriaReunion
   * @return ConvocatoriaReunion
   */
  private ConvocatoriaReunion generarMockConvocatoriaReunion(Comite comite,
      TipoConvocatoriaReunion tipoConvocatoriaReunion) {

    Set<ConvocatoriaReunionLugar> lugar = new HashSet<>();
    lugar.add(new ConvocatoriaReunionLugar(Language.ES, "Lugar"));
    Set<ConvocatoriaReunionOrdenDia> ordenDia = new HashSet<>();
    ordenDia.add(new ConvocatoriaReunionOrdenDia(Language.ES,
        "Orden del día convocatoria reunión"));
    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.now());
    convocatoriaReunion.setFechaLimite(Instant.now());
    convocatoriaReunion.setVideoconferencia(false);
    convocatoriaReunion.setVideoconferencia(false);
    convocatoriaReunion.setLugar(lugar);
    convocatoriaReunion.setOrdenDia(ordenDia);
    convocatoriaReunion.setAnio(2020);
    convocatoriaReunion.setNumeroActa(100L);
    convocatoriaReunion.setTipoConvocatoriaReunion(tipoConvocatoriaReunion);
    convocatoriaReunion.setHoraInicio(7);
    convocatoriaReunion.setMinutoInicio(30);
    convocatoriaReunion.setFechaEnvio(Instant.now());
    convocatoriaReunion.setActivo(Boolean.TRUE);

    return convocatoriaReunion;
  }

  /**
   * Función que devuelve un objeto Formulario
   * 
   * @return el objeto Formulario
   */
  private Formulario generarMockFormulario() {
    Formulario formulario = new Formulario();
    formulario.setTipo(Formulario.Tipo.MEMORIA);
    formulario.setCodigo("M10/2020/002");

    return formulario;
  }

  /**
   * Función que devuelve un objeto Comite
   * 
   * @param formulario el formulario
   * @return el objeto Comite
   */
  private Comite generarMockComite(Formulario formulario) {
    Set<ComiteNombre> nombre = new HashSet<>();
    nombre.add(new ComiteNombre(Language.ES, "NombreComite1", ComiteNombre.Genero.M));
    Comite comite = new Comite();
    comite.setCodigo("Comite1");
    comite.setNombre(nombre);
    comite.setFormularioMemoriaId(formulario.getId());
    comite.setFormularioSeguimientoAnualId(formulario.getId());
    comite.setFormularioSeguimientoFinalId(formulario.getId());
    comite.setRequiereRetrospectiva(Boolean.FALSE);
    comite.setPermitirRatificacion(Boolean.FALSE);
    comite.setPrefijoReferencia("M10");
    comite.setTareaNombreLibre(Boolean.TRUE);
    comite.setTareaExperienciaLibre(Boolean.TRUE);
    comite.setTareaExperienciaDetalle(Boolean.TRUE);
    comite.setMemoriaTituloLibre(Boolean.TRUE);
    comite.setActivo(Boolean.TRUE);

    return comite;
  }

  /**
   * Función que devuelve un objeto TipoConvocatoriaReunionluacion
   * 
   * @return el objeto TipoConvocatoriaReunion
   */
  private TipoConvocatoriaReunion generarMockTipoConvocatoriaReunion() {
    return new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @param tipoEvaluacion el objeto TipoEvaluacion
   * @return Dictamen
   */
  private Dictamen generarMockDictamen(TipoEvaluacion tipoEvaluacion) {
    return new Dictamen(1L, "Dictamen", tipoEvaluacion, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @return el objeto TipoEvaluacion
   */
  private TipoEvaluacion generarMockTipoEvaluacion() {
    return new TipoEvaluacion(1L, "TipoEvaluacion", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @return el objeto TipoActividad
   */
  private TipoActividad generarMockTipoActividad() {
    return new TipoActividad(1L, "TipoActividad", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoInvestigacionTutelada
   * 
   * @return el objeto TipoInvestigacionTutelada
   */
  private TipoInvestigacionTutelada generarMockTipoInvestigacionTutelada() {
    return new TipoInvestigacionTutelada(1L, "TipoInvestigacionTutelada", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param tipoActividad el objeto TipoActividad
   * @return PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(TipoActividad tipoActividad,
      TipoInvestigacionTutelada tipoInvestigacionTutelada) {
    Set<PeticionEvaluacionTitulo> titulo = new HashSet<>();
    titulo.add(new PeticionEvaluacionTitulo(Language.ES, "PeticionEvaluacion"));
    Set<PeticionEvaluacionResumen> resumen = new HashSet<>();
    resumen.add(new PeticionEvaluacionResumen(Language.ES, "Resumen"));
    Set<PeticionEvaluacionOtroValorSocial> otroValorSocial = new HashSet<>();
    otroValorSocial.add(new PeticionEvaluacionOtroValorSocial(Language.ES, "Otro valor social"));
    Set<PeticionEvaluacionObjetivos> objetivos = new HashSet<>();
    objetivos.add(new PeticionEvaluacionObjetivos(Language.ES, "Objetivos"));
    Set<PeticionEvaluacionDisMetodologico> disMetodologico = new HashSet<>();
    disMetodologico.add(new PeticionEvaluacionDisMetodologico(Language.ES, "DiseñoMetodologico"));
    Set<PeticionEvaluacionFuenteFinanciacion> fuenteFinanciacion = new HashSet<>();
    fuenteFinanciacion.add(new PeticionEvaluacionFuenteFinanciacion(Language.ES, "Fuente financiación"));
    return new PeticionEvaluacion(null, "Referencia solicitud convocatoria", "Codigo", titulo,
        tipoActividad, tipoInvestigacionTutelada, false, fuenteFinanciacion, null, null, Instant.now(),
        Instant.now(), resumen, TipoValorSocial.ENSENIANZA_SUPERIOR, otroValorSocial, objetivos,
        disMetodologico, Boolean.FALSE, "user-001", null, null, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria
   * 
   * @return el objeto TipoEstadoMemoria
   */
  private TipoEstadoMemoria generarMockTipoEstadoMemoria() {
    return new TipoEstadoMemoria(1L, "TipoEstadoMemoria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto EstadoRetrospectiva
   * 
   * @return el objeto EstadoRetrospectiva
   */
  private EstadoRetrospectiva generarMockEstadoRetrospectiva() {
    return new EstadoRetrospectiva(1L, "EstadoRetrospectiva", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto Retrospectiva
   * 
   * @param estadoRetrospectiva el objeto EstadoRetrospectiva
   * @return Retrospectiva
   */
  private Retrospectiva generarMockRetrospectiva(EstadoRetrospectiva estadoRetrospectiva) {
    return new Retrospectiva(null, estadoRetrospectiva, Instant.now());
  }

  /**
   * Función que devuelve un objeto Memoria
   * 
   * @param peticionEvaluacion el objeto PeticionEvaluacion
   * @param comite             el objeto Comite
   * @param tipoMemoria        el objeto TipoMemoria
   * @param tipoEstadoMemoria  el objeto TipoEstadoMemoria
   * @param retrospectiva      el objeto Retrospectiva
   * @return Memoria
   */
  private Memoria generarMockMemoria(PeticionEvaluacion peticionEvaluacion, Comite comite,
      TipoEstadoMemoria tipoEstadoMemoria, Retrospectiva retrospectiva, Formulario formulario) {
    Set<MemoriaTitulo> mTitulo = new HashSet<>();
    mTitulo.add(new MemoriaTitulo(Language.ES, "Memoria"));
    Memoria memoria = new Memoria();
    memoria.setId(null);
    memoria.setNumReferencia("numRef-001");
    memoria.setPeticionEvaluacion(peticionEvaluacion);
    memoria.setComite(comite);
    memoria.setFormulario(formulario);
    memoria.setFormularioSeguimientoAnual(formulario);
    memoria.setFormularioSeguimientoFinal(formulario);
    memoria.setTitulo(mTitulo);
    memoria.setPersonaRef("user-001");
    memoria.setTipo(Memoria.Tipo.NUEVA);
    memoria.setEstadoActual(tipoEstadoMemoria);
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setRequiereRetrospectiva(Boolean.TRUE);
    memoria.setRetrospectiva(retrospectiva);
    memoria.setVersion(3);
    memoria.setActivo(Boolean.TRUE);

    return memoria;
  }

  /**
   * Función que devuelve un objeto cargocomite
   * 
   * @param id id del cargocomite
   * @return un cargocomite
   */
  private CargoComite generarMockCargoComite(Long id) {
    return new CargoComite(id, "CargoComite", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto evaluador
   * 
   * @param cargoComite cargoComite
   * @param comite      comite
   * @return un evaluador
   */
  private Evaluador generarMockEvaluador(CargoComite cargoComite, Comite comite) {
    Set<EvaluadorResumen> res = new HashSet<>();
    res.add(new EvaluadorResumen(Language.ES, "resumen"));
    Evaluador evaluador = new Evaluador();
    evaluador.setCargoComite(cargoComite);
    evaluador.setComite(comite);
    evaluador.setFechaAlta(Instant.now());
    evaluador.setFechaBaja(Instant.now());
    evaluador.setResumen(res);
    evaluador.setPersonaRef("persona_ref");
    evaluador.setActivo(Boolean.TRUE);

    return evaluador;
  }

  /**
   * Función que devuelve un objeto Evaluacion
   * 
   * @param dictamen
   * @param memoria
   * @param convocatoriaReunion
   * @param tipoEvaluacion
   * @param evaluador1          evaluador 1
   * @param evaluador2          evaluador 2
   * @return el objeto Evaluacion
   */
  private Evaluacion generarMockEvaluacion(Dictamen dictamen, Memoria memoria, ConvocatoriaReunion convocatoriaReunion,
      TipoEvaluacion tipoEvaluacion, Evaluador evaluador1, Evaluador evaluador2, Boolean esRevMinima, Integer version) {

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setDictamen(dictamen);
    evaluacion.setEsRevMinima(esRevMinima);
    evaluacion.setFechaDictamen(Instant.now());
    evaluacion.setMemoria(memoria);
    evaluacion.setConvocatoriaReunion(convocatoriaReunion);
    evaluacion.setVersion(version);
    evaluacion.setTipoEvaluacion(tipoEvaluacion);
    evaluacion.setEvaluador1(evaluador1);
    evaluacion.setEvaluador2(evaluador2);
    evaluacion.setActivo(Boolean.TRUE);

    return evaluacion;
  }
}