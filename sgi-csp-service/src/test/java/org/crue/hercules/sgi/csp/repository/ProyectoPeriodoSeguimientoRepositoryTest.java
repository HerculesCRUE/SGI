package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.TipoSeguimiento;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoObservaciones;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProyectoPeriodoSeguimientoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoPeriodoSeguimientoRepository repository;

  @Test
  void findAllByProyectoIdOrderByFechaInicial_ReturnsProyectoPeriodoSeguimientoList() {
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion"));

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre(nombreModeloEjecucion)
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimiento with same ProyectoId
    Set<ProyectoTitulo> tituloProyecto1 = new HashSet<>();
    tituloProyecto1.add(new ProyectoTitulo(Language.ES, "PRO1"));

    Proyecto proyecto1 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto1)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto1);

    Set<ProyectoTitulo> tituloProyecto2 = new HashSet<>();
    tituloProyecto2.add(new ProyectoTitulo(Language.ES, "PRO2"));
    Proyecto proyecto2 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto2)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto2);

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
      // @formatter:on
      entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);
    }
    Long proyectoIdBuscado = proyecto1.getId();

    // when: se busca ProyectoPeriodoSeguimiento por ProyectoId
    // ordenadas por Fecha Inicio
    List<ProyectoPeriodoSeguimiento> dataFound = repository.findByProyectoIdOrderByFechaInicio(proyectoIdBuscado);

    // then: Se recupera ProyectoPeriodoSeguimiento con el
    // ProyectoId ordenados por Fecha Inicio
    Assertions.assertThat(dataFound)
        .isNotNull()
        .size().isEqualTo(5);

    for (int i = 0; i < dataFound.size(); i++) {
      Assertions.assertThat(dataFound.get(i).getNumPeriodo()).as("getNumPeriodo()").isEqualTo(i + 1);
    }
  }

  @Test
  void findAllByProyectoIdOrderByFechaInicio_ReturnsNull() {
    // given: 10 ProyectoPeriodoSeguimiento
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombreModeloEjecucion"));

    ModeloEjecucion modeloEjecucion = ModeloEjecucion.builder()
        .nombre(nombreModeloEjecucion)
        .activo(Boolean.TRUE)
        .contrato(Boolean.FALSE)
        .externo(Boolean.FALSE)
        .build();
    entityManager.persistAndFlush(modeloEjecucion);

    // given: 10 ProyectoPeriodoSeguimiento with same ProyectoId
    Set<ProyectoTitulo> tituloProyecto1 = new HashSet<>();
    tituloProyecto1.add(new ProyectoTitulo(Language.ES, "PRO1"));

    Proyecto proyecto1 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto1)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto1);

    Set<ProyectoTitulo> tituloProyecto2 = new HashSet<>();
    tituloProyecto2.add(new ProyectoTitulo(Language.ES, "PRO2"));

    Proyecto proyecto2 = Proyecto.builder()
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion)
        .titulo(tituloProyecto2)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(3)))).activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(proyecto2);

    for (int i = 11; i > 1; i--) {
      Set<ProyectoPeriodoSeguimientoObservaciones> observaciones = new HashSet<>();
      observaciones.add(new ProyectoPeriodoSeguimientoObservaciones(Language.ES, "obs-" + i));
      // @formatter:off
      ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoCientifico = ProyectoPeriodoSeguimiento
          .builder()
          .proyectoId((i % 2 == 0) ? proyecto2.getId() : proyecto1.getId())
          .numPeriodo(i / 2)
          .tipoSeguimiento(TipoSeguimiento.FINAL)
          .fechaInicio(Instant.now())
          .fechaFin(Instant.from(Instant.now().atZone(ZoneOffset.UTC).plus(Period.ofMonths(1))))
          .observaciones(observaciones)
          .build();
        // @formatter:on

      if (i % 2 == 0) {
        entityManager.persistAndFlush(proyectoPeriodoSeguimientoCientifico);
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
