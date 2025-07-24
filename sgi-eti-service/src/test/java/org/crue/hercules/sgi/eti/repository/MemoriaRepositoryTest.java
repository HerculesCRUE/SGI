package org.crue.hercules.sgi.eti.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ComiteNombre;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
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
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class MemoriaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private MemoriaRepository repository;

  @Test
  public void findByIdAndActivoTrue_ReturnsData() throws Exception {

    // given: Datos existentes para la memoria activa

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));
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

    // when: Se buscan los datos
    Optional<Memoria> result = repository.findByIdAndActivoTrue(memoria.getId());

    // then: Se recuperan los datos correctamente
    Assertions.assertThat(result.get()).isNotNull();

  }

  @Test
  public void findByComiteIdAndActivoTrueAndComiteActivoTrue_ReturnsData() throws Exception {

    // given: Datos existentes para la memoria activa

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));

    // when: Se buscan los datos
    Page<Memoria> result = repository
        .findByComiteIdAndPeticionEvaluacionIdAndActivoTrueAndComiteActivoTrue(comite.getId(), 1L, Pageable.unpaged());

    // then: Se recuperan los datos correctamente
    Assertions.assertThat(result.get()).isNotNull();

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
   * @param tipoActividad             el objeto TipoActividad
   * @param tipoInvestigacionTutelada el objeto TipoInvestigacionTutelada
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

}