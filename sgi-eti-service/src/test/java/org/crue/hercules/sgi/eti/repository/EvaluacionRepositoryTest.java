package org.crue.hercules.sgi.eti.repository;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.Comite.Genero;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
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
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

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
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

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

  /**
   * Función que devuelve un objeto ConvocatoriaReunion
   * 
   * @param comite                  el objeto Comite
   * @param tipoConvocatoriaReunion el objeto TipoConvocatoriaReunion
   * @return ConvocatoriaReunion
   */
  public ConvocatoriaReunion generarMockConvocatoriaReunion(Comite comite,
      TipoConvocatoriaReunion tipoConvocatoriaReunion) {

    ConvocatoriaReunion convocatoriaReunion = new ConvocatoriaReunion();
    convocatoriaReunion.setComite(comite);
    convocatoriaReunion.setFechaEvaluacion(Instant.now());
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

    return convocatoriaReunion;
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
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

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
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

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
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

    CargoComite cargoComite = entityManager.persistFlushFind(generarMockCargoComite(1L));
    Evaluador evaluador1 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));
    Evaluador evaluador2 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));

    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 1));
    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 2));

    // when: Se buscan los datos
    Optional<Evaluacion> result = repository.findFirstByMemoriaIdAndActivoTrueOrderByVersionDesc(memoria.getId());

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
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));

    CargoComite cargoComite = entityManager.persistFlushFind(generarMockCargoComite(1L));
    Evaluador evaluador1 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));
    Evaluador evaluador2 = entityManager.persistFlushFind(generarMockEvaluador(cargoComite, comite));

    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c1, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 1));
    entityManager
        .persist(generarMockEvaluacion(dictamen, memoria, c2, tipoEvaluacion, evaluador1, evaluador2, Boolean.TRUE, 2));

    // then: Se comprueba que no recupera ninguna evaluación.
    Optional<Evaluacion> resultEmpty = repository.findFirstByMemoriaIdAndActivoTrueOrderByVersionDesc(111L);
    Assertions.assertThat(resultEmpty).isEmpty();

  }

  /**
   * Función que devuelve un objeto Formulario
   * 
   * @return el objeto Formulario
   */
  public Formulario generarMockFormulario() {
    Formulario formulario = new Formulario(1L, "M10", "Descripcion");
    return formulario;
  }

  /**
   * Función que devuelve un objeto Comite
   * 
   * @param formulario el formulario
   * @return el objeto Comite
   */
  public Comite generarMockComite(Formulario formulario) {
    return new Comite(null, "Comite1", "nombreSecretario", "nombreInvestigacion", Genero.M, "nombreDecreto", "articulo",
        formulario, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoConvocatoriaReunionluacion
   * 
   * @return el objeto TipoConvocatoriaReunion
   */
  public TipoConvocatoriaReunion generarMockTipoConvocatoriaReunion() {
    return new TipoConvocatoriaReunion(1L, "Ordinaria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @param tipoEvaluacion el objeto TipoEvaluacion
   * @return Dictamen
   */
  public Dictamen generarMockDictamen(TipoEvaluacion tipoEvaluacion) {
    return new Dictamen(1L, "Dictamen", tipoEvaluacion, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEvaluacion
   * 
   * @return el objeto TipoEvaluacion
   */
  public TipoEvaluacion generarMockTipoEvaluacion() {
    return new TipoEvaluacion(1L, "TipoEvaluacion", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @return el objeto TipoActividad
   */
  public TipoActividad generarMockTipoActividad() {
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
  public PeticionEvaluacion generarMockPeticionEvaluacion(TipoActividad tipoActividad,
      TipoInvestigacionTutelada tipoInvestigacionTutelada) {
    return new PeticionEvaluacion(null, "Referencia solicitud convocatoria", "Codigo", "PeticionEvaluacion",
        tipoActividad, tipoInvestigacionTutelada, false, "Fuente financiación", null, null, Instant.now(),
        Instant.now(), "Resumen", TipoValorSocial.ENSENIANZA_SUPERIOR, "Otro valor social", "Objetivos",
        "DiseñoMetodologico", Boolean.FALSE, Boolean.FALSE, "user-001", null, Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria
   * 
   * @return el objeto TipoEstadoMemoria
   */
  public TipoEstadoMemoria generarMockTipoEstadoMemoria() {
    return new TipoEstadoMemoria(1L, "TipoEstadoMemoria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto TipoMemoria
   * 
   * @return el objeto TipoMemoria
   */
  public TipoMemoria generarMockTipoMemoria() {
    return new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto EstadoRetrospectiva
   * 
   * @return el objeto EstadoRetrospectiva
   */
  public EstadoRetrospectiva generarMockEstadoRetrospectiva() {
    return new EstadoRetrospectiva(1L, "EstadoRetrospectiva", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto Retrospectiva
   * 
   * @param estadoRetrospectiva el objeto EstadoRetrospectiva
   * @return Retrospectiva
   */
  public Retrospectiva generarMockRetrospectiva(EstadoRetrospectiva estadoRetrospectiva) {
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
  public Memoria generarMockMemoria(PeticionEvaluacion peticionEvaluacion, Comite comite, TipoMemoria tipoMemoria,
      TipoEstadoMemoria tipoEstadoMemoria, Retrospectiva retrospectiva) {
    return new Memoria(null, "numRef-001", peticionEvaluacion, comite, "Memoria", "user-001", tipoMemoria,
        tipoEstadoMemoria, Instant.now(), Boolean.TRUE, retrospectiva, 3, "CodOrganoCompetente", Boolean.TRUE, null);
  }

  /**
   * Función que devuelve un objeto cargocomite
   * 
   * @param id id del cargocomite
   * @return un cargocomite
   */
  public CargoComite generarMockCargoComite(Long id) {
    return new CargoComite(id, "CargoComite", Boolean.TRUE);
  }

  /**
   * Función que devuelve un objeto evaluador
   * 
   * @param cargoComite cargoComite
   * @param comite      comite
   * @return un evaluador
   */
  public Evaluador generarMockEvaluador(CargoComite cargoComite, Comite comite) {
    return new Evaluador(null, cargoComite, comite, Instant.now(), Instant.now(), "resumen", "persona_ref",
        Boolean.TRUE);
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
  public Evaluacion generarMockEvaluacion(Dictamen dictamen, Memoria memoria, ConvocatoriaReunion convocatoriaReunion,
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