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
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoAbreviatura;
import org.crue.hercules.sgi.csp.model.RolProyectoDescripcion;
import org.crue.hercules.sgi.csp.model.RolProyectoNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProyectoEquipoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoEquipoRepository repository;

  @Test
  void findAllByProyectoSocioId_ReturnsProyectoSocioEquipo() {

    // given: 2 ProyectoEquipo de los que 1 coincide con el
    // idProyecto
    // buscado

    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion1 = entityManager
        .persistAndFlush(
            new ModeloEjecucion(null, nombreModeloEjecucion, descripcionModeloEjecucion1, true, false, false, false));

    Set<ProyectoTitulo> tituloProyecto1 = new HashSet<>();
    tituloProyecto1.add(new ProyectoTitulo(Language.ES, "proyecto 1"));

    Proyecto proyecto1 = entityManager.persistAndFlush(Proyecto.builder()
        .titulo(tituloProyecto1)
        .acronimo("PR1")
        .fechaInicio(Instant.parse("2020-11-20T00:00:00Z"))
        .fechaFin(Instant.parse("2021-11-20T23:59:59Z"))
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion1)
        .activo(Boolean.TRUE)
        .build());

    Set<ProyectoTitulo> tituloProyecto2 = new HashSet<>();
    tituloProyecto2.add(new ProyectoTitulo(Language.ES, "proyecto 2"));

    Proyecto proyecto2 = entityManager.persistAndFlush(Proyecto.builder()
        .titulo(tituloProyecto2)
        .acronimo("PR2")
        .fechaInicio(Instant.parse("2020-11-20T00:00:00Z"))
        .fechaFin(Instant.parse("2021-11-20T23:59:59Z"))
        .unidadGestionRef("2")
        .modeloEjecucion(modeloEjecucion1)
        .activo(Boolean.TRUE)
        .build());

    Set<RolProyectoNombre> nombre = new HashSet<>();
    nombre.add(new RolProyectoNombre(Language.ES, "nombre-001"));

    Set<RolProyectoDescripcion> descripcion = new HashSet<>();
    descripcion.add(new RolProyectoDescripcion(Language.ES, "descripcion-001"));

    Set<RolProyectoAbreviatura> abreviatura = new HashSet<>();
    abreviatura.add(new RolProyectoAbreviatura(Language.ES, "001"));

    RolProyecto rolProyecto = entityManager.persistAndFlush(RolProyecto.builder()
        .abreviatura(abreviatura)
        .nombre(nombre)
        .descripcion(descripcion)
        .rolPrincipal(Boolean.FALSE)
        .equipo(RolProyecto.Equipo.INVESTIGACION)
        .activo(Boolean.TRUE)
        .build());

    ProyectoEquipo proyectoEquipo1 = entityManager.persistAndFlush(
        ProyectoEquipo.builder()
            .proyectoId(proyecto1.getId())
            .rolProyecto(rolProyecto)
            .fechaInicio(Instant.now())
            .fechaFin(Instant.now())
            .personaRef("001")
            .build());

    entityManager.persistAndFlush(ProyectoEquipo.builder()
        .proyectoId(proyecto2.getId())
        .rolProyecto(rolProyecto)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now())
        .personaRef("002")
        .build());

    entityManager.persistAndFlush(ProyectoEquipo.builder()
        .proyectoId(proyecto2.getId())
        .rolProyecto(rolProyecto)
        .fechaInicio(Instant.now())
        .fechaFin(Instant.now())
        .personaRef("002")
        .build());

    // when: se buscan los ProyectoEquipo por proyecto id
    List<ProyectoEquipo> proyectoEquipoEncontrados = repository.findAllByProyectoId(proyecto1.getId());

    // then: Se recupera el ProyectoEquipo con el id socio
    // buscado
    Assertions.assertThat(proyectoEquipoEncontrados.get(0).getId()).as("getId").isNotNull();
    Assertions.assertThat(proyectoEquipoEncontrados.get(0).getPersonaRef()).as("getPersonaRef")
        .isEqualTo(proyectoEquipo1.getPersonaRef());

  }

}
