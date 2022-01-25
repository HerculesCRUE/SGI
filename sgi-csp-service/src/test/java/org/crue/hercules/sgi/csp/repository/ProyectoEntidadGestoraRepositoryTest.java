package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProyectoEntidadGestoraRepositoryTest
 */
@DataJpaTest
public class ProyectoEntidadGestoraRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoEntidadGestoraRepository repository;

  @Test
  public void existsProyectoEntidadGestoraByProyectoIdAndEntidadRef_ReturnsTRUE() throws Exception {

    // given: dos registros ProyectoEntidadGestora.
    ProyectoEntidadGestora proyectoEntidadGestora1 = generarMockProyectoEntidadGestora("-001");
    generarMockProyectoEntidadGestora("-002");

    Long idProyectoBusqueda = proyectoEntidadGestora1.getProyectoId();
    String entidadRefBusqueda = proyectoEntidadGestora1.getEntidadRef();

    // when: comprueba la existencia del ProyectoEntidadGestora para un proyecto y
    // entidadRef
    boolean existeProyectoEntidadGestora = repository
        .existsProyectoEntidadGestoraByProyectoIdAndEntidadRef(idProyectoBusqueda, entidadRefBusqueda);

    // then: Confirma la existencia del ProyectoEntidadGestora buscado
    Assertions.assertThat(existeProyectoEntidadGestora).isTrue();

  }

  @Test
  public void existsProyectoEntidadGestoraByProyectoIdAndEntidadRef_ReturnsFALSE() throws Exception {

    // given: dos registros ProyectoEntidadGestora.
    ProyectoEntidadGestora proyectoEntidadGestora1 = generarMockProyectoEntidadGestora("-001");
    ProyectoEntidadGestora proyectoEntidadGestora2 = generarMockProyectoEntidadGestora("-002");

    Long idProyectoBusqueda = proyectoEntidadGestora1.getProyectoId();
    String entidadRefBusqueda = proyectoEntidadGestora2.getEntidadRef();

    // when: comprueba la existencia del ProyectoEntidadGestora para un proyecto y
    // entidadRef que no existe
    boolean existeProyectoEntidadGestora = repository
        .existsProyectoEntidadGestoraByProyectoIdAndEntidadRef(idProyectoBusqueda, entidadRefBusqueda);

    // then: Confirma que no existe el ProyectoEntidadGestora buscado
    Assertions.assertThat(existeProyectoEntidadGestora).isFalse();
  }

  @Test
  public void existsProyectoEntidadGestoraByIdNotAndProyectoIdAndEntidadRef_ReturnsTRUE() throws Exception {

    // given: dos registros ProyectoEntidadGestora.
    ProyectoEntidadGestora proyectoEntidadGestora1 = generarMockProyectoEntidadGestora("-001");
    ProyectoEntidadGestora proyectoEntidadGestora2 = generarMockProyectoEntidadGestora("-002");

    Long idProyectoEntidadGestoraExcluidoBusqueda = proyectoEntidadGestora2.getId();
    Long idProyectoBusqueda = proyectoEntidadGestora1.getProyectoId();
    String entidadRefBusqueda = proyectoEntidadGestora1.getEntidadRef();

    // when: comprueba la existencia del ProyectoEntidadGestora para un proyecto y
    // entidadRef sin tener en cuenta el ProyectoEntidadGestoraExcluido indicado
    boolean existeProyectoEntidadGestora = repository.existsProyectoEntidadGestoraByIdNotAndProyectoIdAndEntidadRef(
        idProyectoEntidadGestoraExcluidoBusqueda, idProyectoBusqueda, entidadRefBusqueda);

    // then: Confirma la existencia del ProyectoEntidadGestora buscado
    Assertions.assertThat(existeProyectoEntidadGestora).isTrue();

  }

  @Test
  public void existsProyectoEntidadGestoraByIdNotAndProyectoIdAndEntidadRef_ReturnsFALSE() throws Exception {

    // given: dos registros ProyectoEntidadGestora.
    ProyectoEntidadGestora proyectoEntidadGestora1 = generarMockProyectoEntidadGestora("-001");
    generarMockProyectoEntidadGestora("-002");

    Long idProyectoEntidadGestoraExcluidoBusqueda = proyectoEntidadGestora1.getId();
    Long idProyectoBusqueda = proyectoEntidadGestora1.getProyectoId();
    String entidadRefBusqueda = proyectoEntidadGestora1.getEntidadRef();

    // when: comprueba la existencia del ProyectoEntidadGestora para un proyecto y
    // entidadRef sin tener en cuenta el ProyectoEntidadGestoraExcluido indicado
    boolean existeProyectoEntidadGestora = repository.existsProyectoEntidadGestoraByIdNotAndProyectoIdAndEntidadRef(
        idProyectoEntidadGestoraExcluidoBusqueda, idProyectoBusqueda, entidadRefBusqueda);

    // then: Confirma que no existe el ProyectoEntidadGestora buscado
    Assertions.assertThat(existeProyectoEntidadGestora).isFalse();
  }

  /**
   * Función que genera ProyectoEntidadGestora
   * 
   * @param suffix
   * @return el objeto ProyectoEntidadGestora
   */
  private ProyectoEntidadGestora generarMockProyectoEntidadGestora(String suffix) {

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

    ProyectoEntidadGestora proyectoEntidadGestora = ProyectoEntidadGestora.builder()
        .proyectoId(proyecto.getId())
        .entidadRef("entidadRef-" + suffix)
        .build();
    // @formatter:on
    return entityManager.persistAndFlush(proyectoEntidadGestora);
  }

}
