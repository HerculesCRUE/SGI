package org.crue.hercules.sgi.csp.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.model.ProyectoProrrogaObservaciones;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeograficoNombre;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidadNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProyectoProrrogaRepositoryTest
 */
@DataJpaTest
class ProyectoProrrogaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoProrrogaRepository repository;

  @Test
  void findFirstByProyectoIdOrderByFechaConcesionDesc_ReturnsProyectoProrroga() {

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
    Assertions.assertThat(result).isPresent();
    Assertions.assertThat(result.get().getId()).isEqualTo(proyectoProrroga2.getId());

  }

  @Test
  void findFirstByProyectoIdOrderByFechaConcesionDesc_ReturnsEmpty() {

    // given: dos registros ProyectoProrroga.
    Proyecto proyecto = generarMockProyecto("-001");
    generarMockProyectoProrroga("-001", proyecto, Instant.parse("2020-01-01T00:00:00Z"));
    generarMockProyectoProrroga("-002", proyecto, Instant.parse("2020-02-01T23:59:59Z"));

    Long idProyectoBusqueda = 2000L;

    // when: recupera el ProyectoProrroga para un proyecto con la fecha de concesión
    // más reciente
    Optional<ProyectoProrroga> result = repository.findFirstByProyectoIdOrderByFechaConcesionDesc(idProyectoBusqueda);

    // then: No encuentra ProyectoProrroga buscado
    Assertions.assertThat(result).isNotPresent();
  }

  @Test
  void findFirstByIdNotAndProyectoIdOrderByFechaConcesionDesc_ReturnsProyectoProrroga() {

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
    Assertions.assertThat(result).isPresent();
    Assertions.assertThat(result.get().getId()).isEqualTo(proyectoProrroga1.getId());

  }

  @Test
  void findFirstByIdNotAndProyectoIdOrderByFechaConcesionDesc_ReturnsEmpty() {

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
    Assertions.assertThat(result).isNotPresent();
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
}
