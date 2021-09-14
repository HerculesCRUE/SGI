package org.crue.hercules.sgi.csp.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProyectoProrrogaRepositoryTest
 */
@DataJpaTest
public class ProyectoProrrogaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoProrrogaRepository repository;

  @Test
  public void findFirstByProyectoIdOrderByFechaConcesionDesc_ReturnsProyectoProrroga() throws Exception {

    // given: dos registros ProyectoProrroga.
    Proyecto proyecto = generarMockProyecto("-001");
    generarMockProyectoProrroga("-001", proyecto, Instant.parse("2020-01-01T00:00:00Z"));
    ProyectoProrroga proyectoProrroga2 = generarMockProyectoProrroga("-002", proyecto,
        Instant.parse("2020-02-01T23:59:59Z"));

    Long idProyectoBusqueda = proyecto.getId();

    // when: recupera el ProyectoProrroga para un proyecto con la fecha de concesión
    // más reciente
    Optional<ProyectoProrroga> result = repository.findFirstByProyectoIdOrderByFechaConcesionDesc(idProyectoBusqueda);

    // then: retorna el ProyectoProrroga con la fecha de concesión más reciente
    Assertions.assertThat(result.isPresent()).isTrue();
    Assertions.assertThat(result.get().getId()).isEqualTo(proyectoProrroga2.getId());

  }

  @Test
  public void findFirstByProyectoIdOrderByFechaConcesionDesc_ReturnsEmpty() throws Exception {

    // given: dos registros ProyectoProrroga.
    Proyecto proyecto = generarMockProyecto("-001");
    generarMockProyectoProrroga("-001", proyecto, Instant.parse("2020-01-01T00:00:00Z"));
    generarMockProyectoProrroga("-002", proyecto, Instant.parse("2020-02-01T23:59:59Z"));

    Long idProyectoBusqueda = 2L;

    // when: recupera el ProyectoProrroga para un proyecto con la fecha de concesión
    // más reciente
    Optional<ProyectoProrroga> result = repository.findFirstByProyectoIdOrderByFechaConcesionDesc(idProyectoBusqueda);

    // then: No encuentra ProyectoProrroga buscado
    Assertions.assertThat(result.isPresent()).isFalse();
  }

  @Test
  public void findFirstByIdNotAndProyectoIdOrderByFechaConcesionDesc_ReturnsProyectoProrroga() throws Exception {

    // given: dos registros ProyectoProrroga.
    Proyecto proyecto = generarMockProyecto("-001");
    ProyectoProrroga proyectoProrroga1 = generarMockProyectoProrroga("-001", proyecto,
        Instant.parse("2020-01-01T00:00:00Z"));
    ProyectoProrroga proyectoProrroga2 = generarMockProyectoProrroga("-002", proyecto,
        Instant.parse("2020-02-01T23:59:59Z"));

    Long idProyectoProrrogaExcluido = proyectoProrroga2.getId();
    Long idProyectoBusqueda = proyecto.getId();

    // when: recupera el ProyectoProrroga para un proyecto con la fecha de concesión
    // más reciente
    Optional<ProyectoProrroga> result = repository
        .findFirstByIdNotAndProyectoIdOrderByFechaConcesionDesc(idProyectoProrrogaExcluido, idProyectoBusqueda);

    // then: retorna el ProyectoProrroga con la fecha de concesión más reciente
    Assertions.assertThat(result.isPresent()).isTrue();
    Assertions.assertThat(result.get().getId()).isEqualTo(proyectoProrroga1.getId());

  }

  @Test
  public void findFirstByIdNotAndProyectoIdOrderByFechaConcesionDesc_ReturnsEmpty() throws Exception {

    // given: dos registros ProyectoProrroga.
    Proyecto proyecto = generarMockProyecto("-001");
    ProyectoProrroga proyectoProrroga1 = generarMockProyectoProrroga("-001", proyecto,
        Instant.parse("2020-01-01T00:00:00Z"));

    Long idProyectoProrrogaExcluido = proyectoProrroga1.getId();
    Long idProyectoBusqueda = proyecto.getId();

    // when: recupera el ProyectoProrroga para un proyecto con la fecha de concesión
    // más reciente
    Optional<ProyectoProrroga> result = repository
        .findFirstByIdNotAndProyectoIdOrderByFechaConcesionDesc(idProyectoProrrogaExcluido, idProyectoBusqueda);

    // then: No encuentra ProyectoProrroga buscado
    Assertions.assertThat(result.isPresent()).isFalse();
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
}
