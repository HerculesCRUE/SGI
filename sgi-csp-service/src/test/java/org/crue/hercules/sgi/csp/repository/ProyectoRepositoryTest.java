package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.crue.hercules.sgi.csp.model.Proyecto;
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
 * ProyectoRepositoryTest
 */
@DataJpaTest
class ProyectoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoRepository repository;

  @Test
  void existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual_ReturnsTRUE() {
    // given: un proyecto comprencido entre las fechas 2020-01-01 y 2020-12/31
    Proyecto proyecto1 = generarMockProyecto("-001");

    Long idProyectoBusqueda = proyecto1.getId();
    Instant fechaIniBusqueda = proyecto1.getFechaInicio().plus(Period.ofDays(1));
    Instant fechaFinBusqueda = proyecto1.getFechaFin().minus(Period.ofDays(1));

    // when: comprueba si rango de fechas indicadas están dentro del proyecto
    boolean isDentroProyecto = repository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        idProyectoBusqueda, fechaIniBusqueda, fechaFinBusqueda);

    // then: Confirma que las fechas están dentro del proyecto
    Assertions.assertThat(isDentroProyecto).isTrue();

  }

  @Test
  void existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual_ReturnsFALSE() {
    // given: un proyecto comprencido entre las fechas 2020-01-01 y 2020-12/31
    Proyecto proyecto1 = generarMockProyecto("-001");

    Long idProyectoBusqueda = proyecto1.getId();

    Instant fechaIniBusquedaCaso1 = proyecto1.getFechaInicio().minus(Period.ofDays(10));
    Instant fechaFinBusquedaCaso1 = proyecto1.getFechaInicio().minus(Period.ofDays(9));
    // when: comprueba si rango de fechas indicadas están dentro del proyecto
    boolean isDentroProyectoCaso1 = repository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        idProyectoBusqueda, fechaIniBusquedaCaso1, fechaFinBusquedaCaso1);
    // then: Confirma que las fechas están fuera del proyecto
    Assertions.assertThat(isDentroProyectoCaso1).isFalse();

    Instant fechaIniBusquedaCaso2 = proyecto1.getFechaInicio().minus(Period.ofDays(1));
    Instant fechaFinBusquedaCaso2 = proyecto1.getFechaFin().minus(Period.ofDays(1));
    // when: comprueba si rango de fechas indicadas están dentro del proyecto
    boolean isDentroProyectoCaso2 = repository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        idProyectoBusqueda, fechaIniBusquedaCaso2, fechaFinBusquedaCaso2);
    // then: Confirma que las fechas están fuera del proyecto
    Assertions.assertThat(isDentroProyectoCaso2).isFalse();

    Instant fechaIniBusquedaCaso3 = proyecto1.getFechaInicio();
    Instant fechaFinBusquedaCaso3 = proyecto1.getFechaFin().plus(Period.ofDays(1));
    // when: comprueba si rango de fechas indicadas están dentro del proyecto
    boolean isDentroProyectoCaso3 = repository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        idProyectoBusqueda, fechaIniBusquedaCaso3, fechaFinBusquedaCaso3);
    // then: Confirma que las fechas están fuera del proyecto
    Assertions.assertThat(isDentroProyectoCaso3).isFalse();

    Instant fechaIniBusquedaCaso4 = proyecto1.getFechaFin().plus(Period.ofDays(9));
    Instant fechaFinBusquedaCaso4 = proyecto1.getFechaFin().plus(Period.ofDays(10));
    // when: comprueba si rango de fechas indicadas están dentro del proyecto
    boolean isDentroProyectoCaso4 = repository.existsProyectoByIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        idProyectoBusqueda, fechaIniBusquedaCaso4, fechaFinBusquedaCaso4);
    // then: Confirma que las fechas están fuera del proyecto
    Assertions.assertThat(isDentroProyectoCaso4).isFalse();
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
        .activo(Boolean.TRUE)
        .build();

    return entityManager.persistAndFlush(proyecto);
  }

}
