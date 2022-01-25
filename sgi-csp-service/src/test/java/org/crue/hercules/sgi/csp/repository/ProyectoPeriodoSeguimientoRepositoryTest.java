package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProyectoPeriodoSeguimientoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoPeriodoSeguimientoRepository repository;

  @Test
  public void findAllByProyectoIdOrderByFechaInicial_ReturnsProyectoPeriodoSeguimientoList() throws Exception {

    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre("nombreModeloEjecucion")
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimiento with same ProyectoId
    Proyecto proyecto1 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO1")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto1);

    Proyecto proyecto2 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO2")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto2);
    // @formatter:on

    for (int i = 11; i > 1; i--) {
      // @formatter:off
      ProyectoPeriodoSeguimiento ProyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento
          .builder()
          .proyectoId((i % 2 == 0) ? proyecto2.getId() : proyecto1.getId())
          .numPeriodo(i / 2)
          .tipoSeguimiento(TipoSeguimiento.FINAL)
          .fechaInicio(Instant.now().plus(Period.ofDays(i - 1)))
          .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(i))))
          .build();
      // @formatter:on
      entityManager.persistAndFlush(ProyectoPeriodoSeguimientoCientifico);
    }
    Long proyectoIdBuscado = proyecto1.getId();

    // when: se busca ProyectoPeriodoSeguimiento por ProyectoId
    // ordenadas por Fecha Inicio
    List<ProyectoPeriodoSeguimiento> dataFound = repository.findByProyectoIdOrderByFechaInicio(proyectoIdBuscado);

    // then: Se recupera ProyectoPeriodoSeguimiento con el
    // ProyectoId ordenados por Fecha Inicio
    Assertions.assertThat(dataFound).isNotNull();
    Assertions.assertThat(dataFound).size().isEqualTo(5);

    for (int i = 0; i < dataFound.size(); i++) {
      Assertions.assertThat(dataFound.get(i).getNumPeriodo()).as("getNumPeriodo()").isEqualTo(i + 1);
    }
  }

  @Test
  public void findAllByProyectoIdOrderByFechaInicio_ReturnsNull() throws Exception {
    // given: 10 ProyectoPeriodoSeguimiento
    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre("nombreModeloEjecucion")
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimiento with same ProyectoId
    Proyecto proyecto1 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO1")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto1);

    Proyecto proyecto2 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO2")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto2);
    // @formatter:on

    for (int i = 11; i > 1; i--) {
      // @formatter:off
      ProyectoPeriodoSeguimiento ProyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento
          .builder()
          .proyectoId((i % 2 == 0) ? proyecto2.getId() : proyecto1.getId())
          .numPeriodo(i / 2)
          .tipoSeguimiento(TipoSeguimiento.FINAL)
          .fechaInicio(Instant.now())
          .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(1))))
          .observaciones("obs-" + i)
          .build();
        // @formatter:on

      if (i % 2 == 0) {
        entityManager.persistAndFlush(ProyectoPeriodoSeguimientoCientifico);
      }
    }
    Long proyectoIdBuscado = proyecto1.getId();

    // when: se busca ProyectoPeriodoSeguimiento por ProyectoId
    // ordenadas por Fecha Inicio
    List<ProyectoPeriodoSeguimiento> dataFound = repository.findByProyectoIdOrderByFechaInicio(proyectoIdBuscado);

    // then: No encuentra ProyectoPeriodoSeguimiento para
    // ProyectoId
    Assertions.assertThat(dataFound).isEmpty();
  }

}
