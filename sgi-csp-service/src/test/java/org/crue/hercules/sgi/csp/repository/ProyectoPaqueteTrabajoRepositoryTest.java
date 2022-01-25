package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProyectoPaqueteTrabajoRepositoryTest
 */
@DataJpaTest
public class ProyectoPaqueteTrabajoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoPaqueteTrabajoRepository repository;

  @Test
  public void existsProyectoPaqueteTrabajoByProyectoIdAndNombre_ReturnsTRUE() throws Exception {

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
  public void existsProyectoPaqueteTrabajoByProyectoIdAndNombre_ReturnsFALSE() throws Exception {

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
  public void existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre_ReturnsTRUE() throws Exception {

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
  public void existsProyectoPaqueteTrabajoByIdNotAndProyectoIdAndNombre_ReturnsFALSE() throws Exception {

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
        .titulo("titulo-" + suffix)
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

    ProyectoPaqueteTrabajo proyectoPaqueteTrabajo = ProyectoPaqueteTrabajo.builder()
        .proyectoId(proyecto.getId())
        .nombre("proyectoPaquete" + suffix)
        .fechaInicio(Instant.parse("2020-01-01T00:00:00Z"))
        .fechaFin(Instant.parse("2020-01-15T23:59:59Z"))
        .personaMes(1D)
        .descripcion("descripcionProyectoPaqueteTrabajo-" + suffix)
        .build();
    // @formatter:on
    return entityManager.persistAndFlush(proyectoPaqueteTrabajo);
  }

}