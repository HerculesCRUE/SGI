package org.crue.hercules.sgi.csp.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumentoComentario;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumentoNombre;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProrrogaObservaciones;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoDocumentoDescripcion;
import org.crue.hercules.sgi.csp.model.TipoDocumentoNombre;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidadNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProrrogaDocumentoRepositoryTest
 */
@DataJpaTest
class ProrrogaDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProrrogaDocumentoRepository repository;

  @Test
  void deleteByProyectoProrrogaId_ReturnsListProrrogaDocumento() {

    // given: registros ProyectoProrroga con documentos asociados
    Proyecto proyecto = generarMockProyecto("-001");
    TipoDocumento tipoDocumento1 = generarMockTipoDocumento("-001");
    TipoDocumento tipoDocumento2 = generarMockTipoDocumento("-002");
    ProyectoProrroga proyectoProrroga1 = generarMockProyectoProrroga("-001", proyecto,
        Instant.parse("2020-01-01T00:00:00Z"));
    ProrrogaDocumento prorrogaDocumento1 = generarMockProrrogaDocumento("-001", proyectoProrroga1, tipoDocumento1);
    ProrrogaDocumento prorrogaDocumento2 = generarMockProrrogaDocumento("-002", proyectoProrroga1, tipoDocumento2);
    ProyectoProrroga proyectoProrroga2 = generarMockProyectoProrroga("-002", proyecto,
        Instant.parse("2020-02-01T23:59:59Z"));
    ProrrogaDocumento prorrogaDocumento3 = generarMockProrrogaDocumento("-003", proyectoProrroga2, tipoDocumento1);
    ProrrogaDocumento prorrogaDocumento4 = generarMockProrrogaDocumento("-004", proyectoProrroga2, tipoDocumento2);

    Long idProyectoProrroga = proyectoProrroga2.getId();

    // when: Se eliminan los proyectos de la prorroga2
    List<ProrrogaDocumento> result = repository.deleteByProyectoProrrogaId(idProyectoProrroga);

    // then: retorna la lista de documentos eliminados
    Assertions.assertThat(result)
        .hasSize(2)
        .doesNotContain(prorrogaDocumento1)
        .doesNotContain(prorrogaDocumento2)
        .contains(prorrogaDocumento3)
        .contains(prorrogaDocumento4);

  }

  @Test
  void deleteByProyectoProrrogaId_ReturnsEmptyList() {

    Proyecto proyecto = generarMockProyecto("-001");
    TipoDocumento tipoDocumento1 = generarMockTipoDocumento("-001");
    TipoDocumento tipoDocumento2 = generarMockTipoDocumento("-002");
    ProyectoProrroga proyectoProrroga1 = generarMockProyectoProrroga("-001", proyecto,
        Instant.parse("2020-01-01T00:00:00Z"));
    generarMockProrrogaDocumento("-001", proyectoProrroga1, tipoDocumento1);
    generarMockProrrogaDocumento("-002", proyectoProrroga1, tipoDocumento2);
    ProyectoProrroga proyectoProrroga2 = generarMockProyectoProrroga("-002", proyecto,
        Instant.parse("2020-02-01T23:59:59Z"));

    Long idProyectoProrroga = proyectoProrroga2.getId();

    // when: Se eliminan los proyectos de la prorroga2
    List<ProrrogaDocumento> result = repository.deleteByProyectoProrrogaId(idProyectoProrroga);

    // then: retorna la lista de documentos eliminados
    Assertions.assertThat(result).isEmpty();
  }

  /**
   * Función que genera Proyecto
   * 
   * @param suffix
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(String suffix) {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion" + suffix));

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre(nombreModeloEjecucion)
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    Set<TipoFinalidadNombre> nombreTipoFinalidad = new HashSet<>();
    nombreTipoFinalidad.add(new TipoFinalidadNombre(Language.ES, "nombreTipoFinalidad" + suffix));

    TipoFinalidad tipoFinalidad = TipoFinalidad.builder()
        .nombre(nombreTipoFinalidad)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoFinalidad);

    Set<TipoAmbitoGeograficoNombre> tipoAmbitoGeograficoNombre = new HashSet<>();
    tipoAmbitoGeograficoNombre.add(new TipoAmbitoGeograficoNombre(Language.ES, "nombreTipoAmbitoGeografico" + suffix));

    TipoAmbitoGeografico tipoAmbitoGeografico = TipoAmbitoGeografico.builder()
        .nombre(tipoAmbitoGeograficoNombre)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoAmbitoGeografico);

    ModeloUnidad modeloUnidad = ModeloUnidad.builder()
        .modeloEjecucion(modeloEjecucion)
        .unidadGestionRef("2")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloUnidad);

    Set<ProyectoTitulo> tituloProyecto = new HashSet<>();
    tituloProyecto.add(new ProyectoTitulo(Language.ES, "titulo" + suffix));

    Proyecto proyecto = Proyecto.builder()
        .acronimo("PR" + suffix)
        .codigoExterno("COD" + suffix)
        .titulo(tituloProyecto)
        .unidadGestionRef("2")
        .modeloEjecucion(modeloEjecucion)
        .finalidad(tipoFinalidad)
        .ambitoGeografico(tipoAmbitoGeografico)
        .fechaInicio(Instant.parse("2020-01-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-12-31T23:59:59Z"))
        .permitePaquetesTrabajo(Boolean.TRUE)
        .activo(Boolean.TRUE)
        .build();

    return entityManager.persistAndFlush(proyecto);

  }

  /**
   * Función que genera ProyectoProrroga
   * 
   * @param suffix
   * @param fechaConcesion fecha concesión
   * @return el objeto ProyectoProrroga
   */
  private ProyectoProrroga generarMockProyectoProrroga(String suffix, Proyecto proyecto, Instant fechaConcesion) {
    Set<ProyectoProrrogaObservaciones> proyectoProrrogaObservaciones = new HashSet<>();
    proyectoProrrogaObservaciones
        .add(new ProyectoProrrogaObservaciones(Language.ES, "observaciones-proyecto-prorroga" + suffix));

    // @formatter:off
    ProyectoProrroga proyectoProrroga = ProyectoProrroga.builder()
        .proyectoId(proyecto.getId())
        .numProrroga(1)
        .fechaConcesion(fechaConcesion)
        .tipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE)
        .fechaFin(Instant.parse("2020-12-31T23:59:59Z"))
        .importe(BigDecimal.valueOf(123.45))
        .observaciones(proyectoProrrogaObservaciones)
        .build();
    // @formatter:on
    return entityManager.persistAndFlush(proyectoProrroga);
  }

  /**
   * Función que devuelve un objeto ProrrogaDocumento
   * 
   * @param suffix
   * @param proyectoProrroga ProyectoProrroga
   * @param tipoDocumento    TipoDocumento
   * @return el objeto ProrrogaDocumento
   */
  private ProrrogaDocumento generarMockProrrogaDocumento(String suffix, ProyectoProrroga proyectoProrroga,
      TipoDocumento tipoDocumento) {
    Set<ProrrogaDocumentoNombre> prorrogaDocumentoNombre = new HashSet<>();
    prorrogaDocumentoNombre.add(new ProrrogaDocumentoNombre(Language.ES, "prorroga-documento" + suffix));

    Set<ProrrogaDocumentoComentario> prorrogaDocumentoComentario = new HashSet<>();
    prorrogaDocumentoComentario
        .add(new ProrrogaDocumentoComentario(Language.ES, "comentario-prorroga-documento" + suffix));

    // @formatter:off
    ProrrogaDocumento prorrogaDocumento = ProrrogaDocumento.builder()
        .proyectoProrrogaId(proyectoProrroga.getId())
        .nombre(prorrogaDocumentoNombre)
        .documentoRef("documentoRef" + suffix)
        .tipoDocumento(tipoDocumento)
        .comentario(prorrogaDocumentoComentario)
        .visible(Boolean.TRUE)
        .build();
    // @formatter:on

    return entityManager.persistAndFlush(prorrogaDocumento);
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param suffix
   * @return el objeto ModeloTipoFase
   */
  private TipoDocumento generarMockTipoDocumento(String suffix) {
    Set<TipoDocumentoNombre> nombreTipoDocumento = new HashSet<>();
    nombreTipoDocumento.add(new TipoDocumentoNombre(Language.ES, "tipo-documento-" + suffix));

    Set<TipoDocumentoDescripcion> descripcionTipoDocumento = new HashSet<>();
    descripcionTipoDocumento.add(new TipoDocumentoDescripcion(Language.ES, "descripcion-tipo-documento-" + suffix));

    // @formatter:off
    TipoDocumento tipoDocumento = TipoDocumento.builder()
        .nombre(nombreTipoDocumento)
        .descripcion(descripcionTipoDocumento)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on

    return entityManager.persistAndFlush(tipoDocumento);
  }
}
