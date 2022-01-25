package org.crue.hercules.sgi.csp.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProrrogaDocumentoRepositoryTest
 */
@DataJpaTest
public class ProrrogaDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProrrogaDocumentoRepository repository;

  @Test
  public void deleteByProyectoProrrogaId_ReturnsListProrrogaDocumento() throws Exception {

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
    Assertions.assertThat(result.size()).isEqualTo(2);
    Assertions.assertThat(result.contains(prorrogaDocumento1)).isFalse();
    Assertions.assertThat(result.contains(prorrogaDocumento2)).isFalse();
    Assertions.assertThat(result.contains(prorrogaDocumento3)).isTrue();
    Assertions.assertThat(result.contains(prorrogaDocumento4)).isTrue();

  }

  @Test
  public void deleteByProyectoProrrogaId_ReturnsEmptyList() throws Exception {

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
    Assertions.assertThat(result.isEmpty()).isTrue();
  }

  /**
   * Función que genera Proyecto
   * 
   * @param suffix
   * @return el objeto Proyecto
   */
  private Proyecto generarMockProyecto(String suffix) {

    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre("nombreModeloEjecucion" + suffix)
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    TipoFinalidad tipoFinalidad = TipoFinalidad.builder()
        .nombre("nombreTipoFinalidad" + suffix)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoFinalidad);

    TipoAmbitoGeografico tipoAmbitoGeografico = TipoAmbitoGeografico.builder()
        .nombre("nombreTipoAmbitoGeografico" + suffix)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(tipoAmbitoGeografico);

    ModeloUnidad modeloUnidad = ModeloUnidad.builder()
        .modeloEjecucion(modeloEjecucion)
        .unidadGestionRef("2")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloUnidad);

    Proyecto proyecto = Proyecto.builder()
        .acronimo("PR" + suffix)
        .codigoExterno("COD" + suffix)
        .titulo("titulo" + suffix)
        .unidadGestionRef("2")
        .modeloEjecucion(modeloEjecucion)
        .finalidad(tipoFinalidad)
        .ambitoGeografico(tipoAmbitoGeografico)
        .fechaInicio(Instant.parse("2020-01-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-12-31T23:59:59Z"))
        .permitePaquetesTrabajo(Boolean.TRUE)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on

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

    // @formatter:off
    ProyectoProrroga proyectoProrroga = ProyectoProrroga.builder()
        .proyectoId(proyecto.getId())
        .numProrroga(1)
        .fechaConcesion(fechaConcesion)
        .tipo(ProyectoProrroga.Tipo.TIEMPO_IMPORTE)
        .fechaFin(Instant.parse("2020-12-31T23:59:59Z"))
        .importe(BigDecimal.valueOf(123.45))
        .observaciones("observaciones-proyecto-prorroga" + suffix)
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

    // @formatter:off
    ProrrogaDocumento prorrogaDocumento = ProrrogaDocumento.builder()
        .proyectoProrrogaId(proyectoProrroga.getId())
        .nombre("prorroga-documento" + suffix)
        .documentoRef("documentoRef" + suffix)
        .tipoDocumento(tipoDocumento)
        .comentario("comentario-prorroga-documento" + suffix)
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

    // @formatter:off
    TipoDocumento tipoDocumento = TipoDocumento.builder()
        .nombre("tipo-documento-" + suffix)
        .descripcion("descripcion-tipo-documento-" + suffix)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on

    return entityManager.persistAndFlush(tipoDocumento);
  }
}
