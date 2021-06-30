package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProyectoPeriodoSeguimientoDocumentoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoPeriodoSeguimientoDocumentoRepository repository;

  @Test
  public void existsByProyectoSeguimiento_ReturnsTrue() throws Exception {

    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre("nombreModeloEjecucion")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimientoDocumento with same ProyectoSeguimientoId
    // @formatter:off
    Proyecto proyecto1 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO1")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3))))
        .activo(Boolean.TRUE)
        .fechaBase(Instant.now())
        .build();
    // @formatter:on
    entityManager.persistAndFlush(proyecto1);

    // @formatter:off
    Proyecto proyecto2 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO2")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .fechaBase(Instant.now())
        .build();
    // @formatter:on
    entityManager.persistAndFlush(proyecto2);

    ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento.builder()
        .proyectoId(proyecto1.getId()).numPeriodo(1).tipoSeguimiento(TipoSeguimiento.FINAL)
        .fechaInicio(Instant.now().plus(Period.ofDays(1)))
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(1)))).build();

    entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);

    ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = ProyectoPeriodoSeguimientoDocumento
        .builder().proyectoPeriodoSeguimientoId(proyectoPeriodoSeguimientoCientifico.getId()).documentoRef("doc-1")
        .nombre("nombre-1").build();

    entityManager.persistAndFlush(proyectoPeriodoSeguimientoDocumento);
    // @formatter:on

    // when: se busca ProyectoPeriodoSeguimientoDocumento por ProyectoSeguimientoId
    // ordenadas por Fecha Inicio
    repository.existsByProyectoPeriodoSeguimientoId(1L);
  }

  @Test
  public void existsByProyectoSeguimiento_ReturnsFalse() throws Exception {

    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre("nombreModeloEjecucion")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimientoDocumento with same ProyectoSeguimientoId
    Proyecto proyecto1 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO1")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .fechaBase(Instant.now())
        .build();
    entityManager.persistAndFlush(proyecto1);

    Proyecto proyecto2 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO2")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .fechaBase(Instant.now())
        .build();
    entityManager.persistAndFlush(proyecto2);
    // @formatter:on

    for (int i = 11; i > 1; i--) {
      // @formatter:off
      ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento
          .builder()
          .proyectoId((i % 2 == 0) ? proyecto2.getId() : proyecto1.getId())
          .numPeriodo(i / 2)
          .tipoSeguimiento(TipoSeguimiento.FINAL)
          .fechaInicio(Instant.now().plus(Period.ofDays(i - 1)))
          .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(i))))
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);

      ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = ProyectoPeriodoSeguimientoDocumento
          .builder()
          .proyectoPeriodoSeguimientoId(proyectoPeriodoSeguimientoCientifico.getId())
          .documentoRef("doc-" + i)
          .nombre("nombre-" + i)
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoDocumento);
      // @formatter:on
    }

    // when: se busca ProyectoPeriodoSeguimientoDocumento por ProyectoSeguimientoId
    // ordenadas por Fecha Inicio
    Boolean dataFound = repository.existsByProyectoPeriodoSeguimientoId(111L);

    // then: Se recupera ProyectoPeriodoSeguimientoDocumento con el
    // ProyectoSeguimientoId ordenados por Fecha Inicio
    Assertions.assertThat(dataFound).isFalse();
  }

  @Test
  public void deleteByProyectoSeguimiento_WithExistingId_NoReturnsAnyException() throws Exception {

    // @formatter:off
    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre("nombreModeloEjecucion")
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimientoDocumento with same ProyectoSeguimientoId
    Proyecto proyecto1 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO1")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .fechaBase(Instant.now())
        .build();
    entityManager.persistAndFlush(proyecto1);

    Proyecto proyecto2 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo("PRO2")
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .fechaBase(Instant.now())
        .build();
    entityManager.persistAndFlush(proyecto2);
    // @formatter:on

    for (int i = 11; i > 1; i--) {
      // @formatter:off
      ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento
          .builder()
          .proyectoId((i % 2 == 0) ? proyecto2.getId() : proyecto1.getId())
          .numPeriodo(i / 2)
          .tipoSeguimiento(TipoSeguimiento.FINAL)
          .fechaInicio(Instant.now().plus(Period.ofDays(i - 1)))
          .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(i))))
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);

      ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento = ProyectoPeriodoSeguimientoDocumento
          .builder()
          .proyectoPeriodoSeguimientoId(proyectoPeriodoSeguimientoCientifico.getId())
          .documentoRef("doc-" + i)
          .nombre("nombre-" + i)
          .build();

      entityManager.persistAndFlush(proyectoPeriodoSeguimientoDocumento);
      // @formatter:on
    }

    // when: se busca ProyectoPeriodoSeguimientoDocumento por ProyectoSeguimientoId
    // ordenadas por Fecha Inicio
    repository.deleteByProyectoPeriodoSeguimientoId(1L);
  }

}
