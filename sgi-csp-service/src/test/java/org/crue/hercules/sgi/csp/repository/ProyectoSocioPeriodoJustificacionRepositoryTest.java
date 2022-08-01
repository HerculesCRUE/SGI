package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProyectoSocioPeriodoJustificacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoSocioPeriodoJustificacionRepository repository;

  @Test
  void findAllByProyectoSocioId_ReturnsProyectoSocioPeriodoJustificacion() throws Exception {

    // given: 1 ProyectoSocioPeriodoJustificacion para el ProyectoSocioId buscado
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, "nombre-1", "descripcion-1", true, false, false);
    entityManager.persistAndFlush(modeloEjecucion);

    // @formatter:off
    RolSocio rolSocio = RolSocio.builder()
        .abreviatura("001")
        .nombre("nombre-001")
        .descripcion("descripcion-001")
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(rolSocio);

    Proyecto proyecto = Proyecto.builder()
        .titulo("proyecto")
        .fechaInicio(Instant.parse("2020-09-18T00:00:00Z"))
        .fechaFin(Instant.parse("2022-10-11T23:59:59Z"))
        .unidadGestionRef("2")
        .modeloEjecucion(modeloEjecucion)
        .activo(Boolean.TRUE)
        .build();

    entityManager.persistAndFlush(proyecto);

    ProyectoSocio proyectoSocio1 = ProyectoSocio.builder()
        .proyectoId(proyecto.getId())
        .empresaRef("codigo-1")
        .rolSocio(rolSocio).build();
    entityManager.persistAndFlush(proyectoSocio1);

    ProyectoSocio proyectoSocio2 = ProyectoSocio.builder()
        .proyectoId(proyecto.getId())
        .empresaRef("codigo-1")
        .rolSocio(rolSocio).build();
    entityManager.persistAndFlush(proyectoSocio2);
    // @formatter:on

    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion1 = new ProyectoSocioPeriodoJustificacion(null,
        proyectoSocio1.getId(), 1, Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
        Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"), "observaciones-1", Boolean.TRUE,
        Instant.parse("2020-11-20T00:00:00Z"), null);
    entityManager.persistAndFlush(proyectoSocioPeriodoJustificacion1);
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion2 = new ProyectoSocioPeriodoJustificacion(null,
        proyectoSocio2.getId(), 1, Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
        Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"), "observaciones-1", Boolean.TRUE,
        Instant.parse("2020-11-20T00:00:00Z"), null);
    entityManager.persistAndFlush(proyectoSocioPeriodoJustificacion2);

    Long proyectoSocioIdBuscado = proyectoSocio1.getId();

    // when: se buscan los ProyectoSocioPeriodoJustificacion por ProyectoSocioId
    List<ProyectoSocioPeriodoJustificacion> dataFound = repository.findAllByProyectoSocioId(proyectoSocioIdBuscado);

    // then: Se recuperan los ProyectoSocioPeriodoJustificacion con el
    // ProyectoSocioId
    // buscado
    Assertions.assertThat(dataFound).hasSize(1);
    Assertions.assertThat(dataFound.get(0).getId()).isEqualTo(proyectoSocioPeriodoJustificacion1.getId());
    Assertions.assertThat(dataFound.get(0).getProyectoSocioId())
        .isEqualTo(proyectoSocioPeriodoJustificacion1.getProyectoSocioId());
    Assertions.assertThat(dataFound.get(0).getObservaciones())
        .isEqualTo(proyectoSocioPeriodoJustificacion1.getObservaciones());
  }

}
