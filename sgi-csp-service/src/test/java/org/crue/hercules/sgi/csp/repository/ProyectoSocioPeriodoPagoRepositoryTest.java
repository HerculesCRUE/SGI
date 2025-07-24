package org.crue.hercules.sgi.csp.repository;

import java.math.BigDecimal;
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
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.ProyectoTitulo;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.RolSocioAbreviatura;
import org.crue.hercules.sgi.csp.model.RolSocioDescripcion;
import org.crue.hercules.sgi.csp.model.RolSocioNombre;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * ProyectoSocioPeriodoPagoRepositoryTest
 */
@DataJpaTest
class ProyectoSocioPeriodoPagoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ProyectoSocioPeriodoPagoRepository repository;

  @Test
  void findAllByProyectoSocioId_ReturnsProyectoSocioPeriodoPago() {

    // given: 2 ProyectoSocioPeriodoPago de los que 1 coincide con el
    // idProyectoSocio
    // buscado
    Set<ModeloEjecucionNombre> nombreModeloEjecucion = new HashSet<>();
    nombreModeloEjecucion.add(new ModeloEjecucionNombre(Language.ES, "nombre-1"));
    Set<ModeloEjecucionDescripcion> descripcionModeloEjecucion1 = new HashSet<>();
    descripcionModeloEjecucion1.add(new ModeloEjecucionDescripcion(Language.ES, "descripcion-1"));
    ModeloEjecucion modeloEjecucion1 = entityManager
        .persistAndFlush(
            new ModeloEjecucion(null, nombreModeloEjecucion, descripcionModeloEjecucion1, true, false, false, false));

    Set<ProyectoTitulo> tituloProyecto = new HashSet<>();
    tituloProyecto.add(new ProyectoTitulo(Language.ES, "proyecto 1"));

    Proyecto proyecto1 = entityManager.persistAndFlush(
        Proyecto.builder()
            .titulo(tituloProyecto)
            .acronimo("PR1")
            .fechaInicio(Instant.parse("2020-11-20T00:00:00Z"))
            .fechaFin(Instant.parse("2021-11-20T23:59:59Z"))
            .unidadGestionRef("2")
            .modeloEjecucion(modeloEjecucion1)
            .activo(Boolean.TRUE)
            .build());

    Set<RolSocioAbreviatura> abreviatura = new HashSet<>();
    abreviatura.add(new RolSocioAbreviatura(Language.ES, "001"));

    Set<RolSocioNombre> nombre = new HashSet<>();
    nombre.add(new RolSocioNombre(Language.ES, "nombre-001"));

    Set<RolSocioDescripcion> descripcion = new HashSet<>();
    descripcion.add(new RolSocioDescripcion(Language.ES, "descripcion-001"));

    RolSocio rolSocio = entityManager.persistAndFlush(RolSocio.builder().abreviatura(abreviatura).nombre(nombre)
        .descripcion(descripcion).coordinador(Boolean.FALSE).activo(Boolean.TRUE).build());

    ProyectoSocio proyectoSocio1 = entityManager.persistAndFlush(
        ProyectoSocio.builder().proyectoId(proyecto1.getId()).empresaRef("empresa-0041").rolSocio(rolSocio).build());

    ProyectoSocio proyectoSocio2 = entityManager.persistAndFlush(
        ProyectoSocio.builder().proyectoId(proyecto1.getId()).empresaRef("empresa-0025").rolSocio(rolSocio).build());

    ProyectoSocioPeriodoPago proyectoSocioPeriodoPago1 = entityManager.persistAndFlush(new ProyectoSocioPeriodoPago(
        null, proyectoSocio1.getId(), 1, new BigDecimal(3500), Instant.parse("2021-04-10T00:00:00Z"), null));

    entityManager.persistAndFlush(new ProyectoSocioPeriodoPago(null, proyectoSocio2.getId(), 1, new BigDecimal(2750),
        Instant.parse("2021-01-10T00:00:00Z"), null));

    entityManager.persistAndFlush(new ProyectoSocioPeriodoPago(null, proyectoSocio2.getId(), 1, new BigDecimal(1500),
        Instant.parse("2021-02-10T00:00:00Z"), null));

    Long proyectoSocioId = proyectoSocio1.getId();

    // when: se buscan los ProyectoSocioPeriodoPago por proyecto socio id
    List<ProyectoSocioPeriodoPago> proyectoSocioPeriodoPagoEncontrados = repository
        .findAllByProyectoSocioId(proyectoSocioId);

    // then: Se recupera el ProyectoSocioPeriodoPago con el id proyecto socio
    // buscado
    Assertions.assertThat(proyectoSocioPeriodoPagoEncontrados.get(0).getId()).as("getId").isNotNull();
    Assertions.assertThat(proyectoSocioPeriodoPagoEncontrados.get(0).getImporte()).as("getImporte")
        .isEqualTo(proyectoSocioPeriodoPago1.getImporte());

  }

}
