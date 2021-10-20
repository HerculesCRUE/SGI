package org.crue.hercules.sgi.eti.repository;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class DocumentacionMemoriaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private DocumentacionMemoriaRepository repository;

  @Test
  public void findByMemoriaIdAndMemoriaActivoTrue_ReturnsData() throws Exception {

    // given: Datos existentes mmemoria activa y documentación.

    Formulario formulario = entityManager.persistFlushFind(generarMockFormulario());
    Comite comite = entityManager.persistFlushFind(generarMockComite(formulario));
    TipoActividad tipoActividad = entityManager.persistAndFlush(generarMockTipoActividad());
    PeticionEvaluacion peticionEvaluacion = entityManager.persistAndFlush(generarMockPeticionEvaluacion(tipoActividad));
    TipoMemoria tipoMemoria = entityManager.persistAndFlush(generarMockTipoMemoria());
    TipoEstadoMemoria tipoEstadoMemoria = entityManager.persistAndFlush(generarMockTipoEstadoMemoria());
    EstadoRetrospectiva estadoRetrospectiva = entityManager.persistAndFlush(generarMockEstadoRetrospectiva());
    Retrospectiva retrospectiva = entityManager.persistAndFlush(generarMockRetrospectiva(estadoRetrospectiva));
    Memoria memoria = entityManager
        .persistAndFlush(generarMockMemoria(peticionEvaluacion, comite, tipoMemoria, tipoEstadoMemoria, retrospectiva));
    TipoDocumento tipoDocumento = entityManager.persistAndFlush(generarMockTipoDocumento(formulario));
    entityManager.persistAndFlush(generarMockDocumentacionMemoria(memoria, tipoDocumento));

    // when: Se buscan los datos
    Page<DocumentacionMemoria> result = repository.findByMemoriaIdAndMemoriaActivoTrue(memoria.getId(),
        Pageable.unpaged());
    // then: Se recuperan los datos correctamente
    Assertions.assertThat(result.getContent()).isNotEmpty();

  }

  /**
   * Función que devuelve un objeto Comite
   * 
   * @param formulario el formulario
   * @return el objeto Comite
   */
  public Comite generarMockComite(Formulario formulario) {
    return new Comite(null, "Comite1", "nombreSecretario", "nombreInvestigacion", "nombreDecreto", "articulo", formulario, Boolean.TRUE);
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
   * Función que devuelve un objeto PeticionEvaluacion
   * 
   * @param tipoActividad el objeto TipoActividad
   * @return PeticionEvaluacion
   */
  private PeticionEvaluacion generarMockPeticionEvaluacion(TipoActividad tipoActividad) {
    return new PeticionEvaluacion(null, "Referencia solicitud convocatoria", "Codigo", "PeticionEvaluacion",
        tipoActividad, null, false, "Fuente financiación", null, null, Instant.now(), Instant.now(), "Resumen",
        TipoValorSocial.ENSENIANZA_SUPERIOR, "otro valor social", "Objetivos", "DiseñoMetodologico", Boolean.FALSE,
        Boolean.FALSE, "user-001", null, Boolean.TRUE);
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
   * Función que devuelve un objeto TipoMemoria
   * 
   * @return el objeto TipoMemoria
   */
  private TipoMemoria generarMockTipoMemoria() {
    return new TipoMemoria(1L, "TipoMemoria", Boolean.TRUE);
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
  private Memoria generarMockMemoria(PeticionEvaluacion peticionEvaluacion, Comite comite, TipoMemoria tipoMemoria,
      TipoEstadoMemoria tipoEstadoMemoria, Retrospectiva retrospectiva) {
    return new Memoria(null, "numRef-001", peticionEvaluacion, comite, "Memoria", "user-001", tipoMemoria,
        tipoEstadoMemoria, Instant.now(), Boolean.TRUE, retrospectiva, 3, "CodOrganoCompetente", Boolean.TRUE, null);
  }

  /**
   * Función que devuelve un objeto Tipo Documento
   * 
   * @param formulario el objeto formulario
   * @return TipoDocumento
   */
  private Formulario generarMockFormulario() {
    return new Formulario(1L, "M10", "Descripcion1");

  }

  /**
   * Función que devuelve un objeto Tipo Documento
   * 
   * @param formulario el objeto formulario
   * @return TipoDocumento
   */
  private TipoDocumento generarMockTipoDocumento(Formulario formulario) {
    return new TipoDocumento(1L, "Seguimiento Anual", formulario, Boolean.TRUE);

  }

  /**
   * Función que devuelve un objeto Documentacion Memoria
   * 
   * @param peticionEvaluacion el objeto PeticionEvaluacion
   * @param comite             el objeto Comite
   * @param tipoMemoria        el objeto TipoMemoria
   * @param tipoEstadoMemoria  el objeto TipoEstadoMemoria
   * @param retrospectiva      el objeto Retrospectiva
   * @return Memoria
   */
  private DocumentacionMemoria generarMockDocumentacionMemoria(Memoria memoria, TipoDocumento tipoDocumento) {
    return new DocumentacionMemoria(null, memoria, tipoDocumento, "docRef001", "doc");
  }

}