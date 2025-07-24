package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.*;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProyectoSocioEquipoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoSocioEquipoRepository repository;

  @Test
  void findAllByProyectoSocioId_ReturnsProyectoSocioEquipo() {

    // given: 2 ProyectoSocioEquipo de los que 1 coincide con el
    // idProyectoSocio
    // buscado
    Set<ModeloEjecucionNombre> nombreModeloEjecucion1 = new HashSet<>();
    nombreModeloEjecucion1.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion1 = entityManager
        .persistAndFlush(
            new ModeloEjecucion(null, nombreModeloEjecucion1, descripcionModeloEjecucion1, true, false, false, false));

    Set<ProyectoTitulo> tituloProyecto = new HashSet<>();
    tituloProyecto.add(new ProyectoTitulo(Language.ES, "proyecto 1"));

    Proyecto proyecto1 = entityManager.persistAndFlush(Proyecto.builder()
        .titulo(tituloProyecto)
        .acronimo("PR1")
        .fechaInicio(Instant.parse("2020-11-20T00:00:00Z"))
        .fechaFin(Instant.parse("2021-11-20T23:59:59Z"))
        .unidadGestionRef("2").modeloEjecucion(modeloEjecucion1)
        .activo(Boolean.TRUE)
        .build());

    Set<RolSocioAbreviatura> rolSocioAbreviatura = new HashSet<>();
    rolSocioAbreviatura.add(new RolSocioAbreviatura(Language.ES, "001"));

    Set<RolSocioNombre> rolSocioNombre = new HashSet<>();
    rolSocioNombre.add(new RolSocioNombre(Language.ES, "nombre-001"));

    Set<RolSocioDescripcion> rolSocioDescripcion = new HashSet<>();
    rolSocioDescripcion.add(new RolSocioDescripcion(Language.ES, "descripcion-001"));

    RolSocio rolSocio = entityManager.persistAndFlush(RolSocio.builder()
        .abreviatura(rolSocioAbreviatura)
        .nombre(rolSocioNombre)
        .descripcion(rolSocioDescripcion)
        .coordinador(Boolean.FALSE)
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
        .equipo(RolProyecto.Equipo.INVESTIGACION).activo(Boolean.TRUE)
        .build());

    ProyectoSocio proyectoSocio1 = entityManager.persistAndFlush(ProyectoSocio.builder()
        .proyectoId(proyecto1.getId())
        .empresaRef("empresa-0041")
        .rolSocio(rolSocio)
        .build());

    ProyectoSocio proyectoSocio2 = entityManager.persistAndFlush(ProyectoSocio.builder()
        .proyectoId(proyecto1.getId())
        .empresaRef("empresa-0025")
        .rolSocio(rolSocio)
        .build());

    ProyectoSocioEquipo proyectoSocioEquipo1 = entityManager.persistAndFlush(new ProyectoSocioEquipo(null,
        proyectoSocio1.getId(), rolProyecto, "001", Instant.parse("2021-04-10T00:00:00Z"), null));

    entityManager.persistAndFlush(new ProyectoSocioEquipo(null, proyectoSocio2.getId(), rolProyecto, "003",
        Instant.parse("2021-04-10T00:00:00Z"), null));
    entityManager.persistAndFlush(new ProyectoSocioEquipo(null, proyectoSocio2.getId(), rolProyecto, "004",
        Instant.parse("2021-04-10T00:00:00Z"), null));

    Long proyectoSocioId = proyectoSocio1.getId();

    // when: se buscan los ProyectoSocioEquipo por proyecto socio id
    List<ProyectoSocioEquipo> proyectoSocioEquipoEncontrados = repository.findAllByProyectoSocioId(proyectoSocioId);

    // then: Se recupera el ProyectoSocioEquipo con el id proyecto socio
    // buscado
    Assertions.assertThat(proyectoSocioEquipoEncontrados.get(0).getId()).as("getId").isNotNull();
    Assertions.assertThat(proyectoSocioEquipoEncontrados.get(0).getPersonaRef()).as("getPersonaRef")
        .isEqualTo(proyectoSocioEquipo1.getPersonaRef());

  }

}
