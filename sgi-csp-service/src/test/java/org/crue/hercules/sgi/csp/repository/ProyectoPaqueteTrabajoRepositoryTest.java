package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajoDescripcion;
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
 * ProyectoPaqueteTrabajoRepositoryTest
 */
@DataJpaTest
class ProyectoPaqueteTrabajoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoPaqueteTrabajoRepository repository;

  @Test
  void existsProyectoPaqueteTrabajoByProyectoIdAndNombre_ReturnsTRUE() {

    // given: dos registros ProyectoPaqueteTrabajo.
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo1 = generarMockProyectoPaqueteTrabajo("-001");
    generarMockProyectoPaqueteTrabajo("-002");

    Long idProyectoBusqueda = proyectoPaqueteTrabajo1.getProyectoId();
    String nombreBusqueda = proyectoPaqueteTrabajo1.getNombre();

    // when: comprueba la existencia del ProyectoPaqueteTrabajo para un proyecto y
    // nombre
    boolean existeProyectoPaqueteTrabajo = repository
        .existsProyectoPaqueteTrabajoByProyectoIdAndNombre(idProyectoBusqueda, nombreBusqueda);

    // then: Confirma la existencia del ProyectoPaqueteTrabajo buscado
    Assertions.assertThat(existeProyectoPaqueteTrabajo).isTrue();

  }

  @Test
  void existsProyectoPaqueteTrabajoByProyectoIdAndNombre_ReturnsFALSE() {

    // given: dos registros ProyectoPaqueteTrabajo.
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo1 = generarMockProyectoPaqueteTrabajo("-001");
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo2 = generarMockProyectoPaqueteTrabajo("-002");

    Long idProyectoBusqueda = proyectoPaqueteTrabajo1.getProyectoId();
    String nombreBusqueda = proyectoPaqueteTrabajo2.getNombre();

    // when: comprueba la existencia del ProyectoPaqueteTrabajo para un proyecto y
    // nombre que no existe
    boolean existeProyectoPaqueteTrabajo = repository
        .existsProyectoPaqueteTrabajoByProyectoIdAndNombre(idProyectoBusqueda, nombreBusqueda);

    // then: Confirma que no existe el ProyectoPaqueteTrabajo buscado
    Assertions.assertThat(existeProyectoPaqueteTrabajo).isFalse();
  }

  @Test
  void existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre_ReturnsTRUE() {

    // given: dos registros ProyectoPaqueteTrabajo.
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo1 = generarMockProyectoPaqueteTrabajo("-001");
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo2 = generarMockProyectoPaqueteTrabajo("-002");

    Long idProyectoPaqueteTrabajoExcluidoBusqueda = proyectoPaqueteTrabajo2.getId();
    Long idProyectoBusqueda = proyectoPaqueteTrabajo1.getProyectoId();
    String nombreBusqueda = proyectoPaqueteTrabajo1.getNombre();

    // when: comprueba la existencia del ProyectoPaqueteTrabajo para un proyecto y
    // nombre sin tener en cuenta el ProyectoPaqueteTrabajoExcluido indicado
    boolean existeProyectoPaqueteTrabajo = repository.existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre(
        idProyectoPaqueteTrabajoExcluidoBusqueda, idProyectoBusqueda, nombreBusqueda);

    // then: Confirma la existencia del ProyectoPaqueteTrabajo buscado
    Assertions.assertThat(existeProyectoPaqueteTrabajo).isTrue();

  }

  @Test
  void existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre_ReturnsFALSE() {

    // given: dos registros ProyectoPaqueteTrabajo.
    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo1 = generarMockProyectoPaqueteTrabajo("-001");
    generarMockProyectoPaqueteTrabajo("-002");

    Long idProyectoPaqueteTrabajoExcluidoBusqueda = proyectoPaqueteTrabajo1.getId();
    Long idProyectoBusqueda = proyectoPaqueteTrabajo1.getProyectoId();
    String nombreBusqueda = proyectoPaqueteTrabajo1.getNombre();

    // when: comprueba la existencia del ProyectoPaqueteTrabajo para un proyecto y
    // nombre sin tener en cuenta el ProyectoPaqueteTrabajoExcluido indicado
    boolean existeProyectoPaqueteTrabajo = repository.existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre(
        idProyectoPaqueteTrabajoExcluidoBusqueda, idProyectoBusqueda, nombreBusqueda);

    // then: Confirma que no existe el ProyectoPaqueteTrabajo buscado
    Assertions.assertThat(existeProyectoPaqueteTrabajo).isFalse();
  }

  /**
   * Función que genera ProyectoPaqueteTrabajo
   * 
   * @param suffix
   * @return el objeto ProyectoPaqueteTrabajo
   */
  private ProyectoPaqueteTrabajo generarMockProyectoPaqueteTrabajo(String suffix) {
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
    entityManager.persistAndFlush(proyecto);

    Set<ProyectoPaqueteTrabajoDescripcion> proyectoPaqueteTrabajoDescripcion = new HashSet<>();
    proyectoPaqueteTrabajoDescripcion.add(
        new ProyectoPaqueteTrabajoDescripcion(Language.ES, "descripcionProyectoPaqueteTrabajo-" + suffix));

    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = ProyectoPaqueteTrabajo.builder()
        .proyectoId(proyecto.getId())
        .nombre("proyectoPaquete" + suffix)
        .fechaInicio(Instant.parse("2020-01-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-01-15T23:59:59Z"))
        .personaMes(1D)
        .descripcion(proyectoPaqueteTrabajoDescripcion)
        .build();

    return entityManager.persistAndFlush(proyectoPaqueteTrabajo);
  }

}
