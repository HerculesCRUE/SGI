package org.crue.hercules.sgi.csp.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.*;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto.TipoPresupuesto;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SolicitudPeriodoJustificacionRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private SolicitudProyectoSocioPeriodoJustificacionRepository repository;

  @Test
  void findAllBySolicitudProyectoSocioId_ReturnsSolicitudProyectoSocioPeriodoJustificacion() throws Exception {

    // given: 2 SolicitudProyectoSocioPeriodoJustificacion para el
    // solicitudProyectoSocio
    // buscado
    // @formatter:off
    Set<SolicitudTitulo> solicitudTitulo = new HashSet<>();
    solicitudTitulo.add(new SolicitudTitulo(Language.ES, "titulo"));

    Solicitud solicitud1 = entityManager.persistAndFlush(Solicitud.builder()
        .creadorRef("user-001")
        .titulo(solicitudTitulo)
        .solicitanteRef("user-002")
        .unidadGestionRef("1")
        .formularioSolicitud(FormularioSolicitud.GRUPO)
        .activo(Boolean.TRUE)
        .build());
    // @formatter:on
    SolicitudProyecto solicitudProyecto = entityManager.persistAndFlush(
        SolicitudProyecto.builder().id(solicitud1.getId()).colaborativo(Boolean.TRUE)
            .coordinado(Boolean.TRUE).colaborativo(Boolean.TRUE).tipoPresupuesto(TipoPresupuesto.GLOBAL).build());

    Set<RolSocioAbreviatura> rolSocioAbreviatura = new HashSet<>();
    rolSocioAbreviatura.add(new RolSocioAbreviatura(Language.ES, "001"));

    Set<RolSocioNombre> rolSocioNombre = new HashSet<>();
    rolSocioNombre.add(new RolSocioNombre(Language.ES, "Lider"));

    Set<RolSocioDescripcion> rolSocioDescripcion = new HashSet<>();
    rolSocioDescripcion.add(new RolSocioDescripcion(Language.ES, "Lider"));

    // @formatter:off
    RolSocio rolSocio = RolSocio.builder()
        .abreviatura(rolSocioAbreviatura)
        .nombre(rolSocioNombre)
        .descripcion(rolSocioDescripcion)
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(rolSocio);

    Set<RolProyectoNombre> nombre = new HashSet<>();
    nombre.add(new RolProyectoNombre(Language.ES, "Rol1"));

    Set<RolProyectoDescripcion> descripcion = new HashSet<>();
    descripcion.add(new RolProyectoDescripcion(Language.ES, "Rol1"));

    Set<RolProyectoAbreviatura> abreviatura = new HashSet<>();
    abreviatura.add(new RolProyectoAbreviatura(Language.ES, "001"));

    RolProyecto rolProyecto = RolProyecto.builder()
        .abreviatura(abreviatura)
        .nombre(nombre)
        .descripcion(descripcion)
        .rolPrincipal(Boolean.FALSE)
        .orden(null)
        .equipo(RolProyecto.Equipo.INVESTIGACION)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(rolProyecto);
    // @formatter:on

    SolicitudProyectoSocio solicitudProyectoSocio1 = entityManager.persistAndFlush(new SolicitudProyectoSocio(null,
        solicitudProyecto.getId(), rolSocio, "001", 1, 3, 3, new BigDecimal(468), null));

    SolicitudProyectoSocio solicitudProyectoSocio2 = entityManager.persistAndFlush(new SolicitudProyectoSocio(null,
        solicitudProyecto.getId(), rolSocio, "002", 1, 3, 3, new BigDecimal(468), null));

    SolicitudProyectoSocioPeriodoJustificacion solicitudProyectoSocioPeriodoJustificacion1 = entityManager
        .persistAndFlush(new SolicitudProyectoSocioPeriodoJustificacion(null, solicitudProyectoSocio1.getId(), 1, 2, 3,
            Instant.parse("2020-12-20T00:00:00Z"), Instant.parse("2021-03-20T00:00:00Z"), null));
    entityManager.persistAndFlush(new SolicitudProyectoSocioPeriodoJustificacion(null, solicitudProyectoSocio1.getId(),
        1, 4, 6, Instant.parse("2020-12-20T00:00:00Z"), Instant.parse("2021-03-20T00:00:00Z"), null));
    entityManager.persistAndFlush(new SolicitudProyectoSocioPeriodoJustificacion(null, solicitudProyectoSocio2.getId(),
        1, 4, 6, Instant.parse("2020-12-20T00:00:00Z"), Instant.parse("2021-03-20T00:00:00Z"), null));

    Long solicitudProyectoSocioBuscado = solicitudProyectoSocio1.getId();

    // when: se buscan los SolicitudProyectoSocioPeriodoJustificacion
    // por SolicitudProyectoSocioId
    List<SolicitudProyectoSocioPeriodoJustificacion> dataFound = repository
        .findAllBySolicitudProyectoSocioId(solicitudProyectoSocioBuscado);

    // then: Se recuperan los SolicitudProyectoSocioPeriodoJustificacion con el
    // SolicitudProyectoSocioId
    // buscado
    Assertions.assertThat(dataFound).hasSize(2);
    Assertions.assertThat(dataFound.get(0).getId()).isEqualTo(solicitudProyectoSocioPeriodoJustificacion1.getId());
    Assertions.assertThat(dataFound.get(0).getSolicitudProyectoSocioId())
        .isEqualTo(solicitudProyectoSocioPeriodoJustificacion1.getSolicitudProyectoSocioId());
    Assertions.assertThat(dataFound.get(0).getObservaciones())
        .isEqualTo(solicitudProyectoSocioPeriodoJustificacion1.getObservaciones());
  }

}
