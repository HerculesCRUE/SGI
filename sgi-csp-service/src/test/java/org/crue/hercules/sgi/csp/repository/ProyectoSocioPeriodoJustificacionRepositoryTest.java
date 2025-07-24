package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionDescripcion;
import org.crue.hercules.sgi.csp.model.ModeloEjecucionNombre;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionObservaciones;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.RolSocioAbreviatura;
import org.crue.hercules.sgi.csp.model.RolSocioDescripcion;
import org.crue.hercules.sgi.csp.model.RolSocioNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProyectoSocioPeriodoJustificacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoSocioPeriodoJustificacionRepository repository;

  @Test
  void findAllByProyectoSocioId_ReturnsProyectoSocioPeriodoJustificacion() {

    // given: 1 ProyectoSocioPeriodoJustificacion para el ProyectoSocioId buscado
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion = new ModeloEjecucion(null, nombreModeloEjecucion, descripcionModeloEjecucion1,
        true, false,
        false, false);
    entityManager.persistAndFlush(modeloEjecucion);

    Set<RolSocioAbreviatura> abreviatura = new HashSet<>();
    abreviatura.add(new RolSocioAbreviatura(Language.ES, "001"));

    Set<RolSocioNombre> nombre = new HashSet<>();
    nombre.add(new RolSocioNombre(Language.ES, "nombre-001"));

    Set<RolSocioDescripcion> descripcion = new HashSet<>();
    descripcion.add(new RolSocioDescripcion(Language.ES, "descripcion-001"));

    RolSocio rolSocio = RolSocio.builder()
        .abreviatura(abreviatura)
        .nombre(nombre)
        .descripcion(descripcion)
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(rolSocio);

    Set<ProyectoTitulo> tituloProyecto = new HashSet<>();
    tituloProyecto.add(new ProyectoTitulo(Language.ES, "proyecto"));

    Proyecto proyecto = Proyecto.builder()
        .titulo(tituloProyecto)
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

    Set<ProyectoSocioPeriodoJustificacionObservaciones> observaciones1 = new HashSet<>();
    observaciones1.add(new ProyectoSocioPeriodoJustificacionObservaciones(Language.ES, "observaciones1"));
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion1 = new ProyectoSocioPeriodoJustificacion(null,
        proyectoSocio1.getId(), 1, Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
        Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
        observaciones1, Boolean.TRUE, Instant.parse("2020-11-20T00:00:00Z"), null);
    entityManager.persistAndFlush(proyectoSocioPeriodoJustificacion1);
    ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion2 = new ProyectoSocioPeriodoJustificacion(null,
        proyectoSocio2.getId(), 1, Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
        Instant.parse("2020-10-10T00:00:00Z"), Instant.parse("2020-11-20T00:00:00Z"),
        observaciones1, Boolean.TRUE, Instant.parse("2020-11-20T00:00:00Z"), null);
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
