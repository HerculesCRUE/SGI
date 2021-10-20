package org.crue.hercules.sgi.csp.repository;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto.TipoPresupuesto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class SolicitudProyectoSocioEquipoRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private SolicitudProyectoSocioEquipoRepository repository;

  @Test
  public void findAllBySolicitudProyectoSocioId_ReturnsConvocatoriaPeriodoJustificacion() throws Exception {

    // given: 2 SolicitudProyectoSocioEquipo para el SolicitudProyectoSocio buscado

    // @formatter:off
    Solicitud solicitud1 = entityManager.persistAndFlush(Solicitud.builder()
        .creadorRef("user-001")
        .titulo("titulo")
        .solicitanteRef("user-002")
        .unidadGestionRef("1")
        .formularioSolicitud(FormularioSolicitud.GRUPO)
        .activo(Boolean.TRUE)
        .build());
    // @formatter:on
    SolicitudProyecto solicitudProyecto = entityManager.persistAndFlush(
        new SolicitudProyecto(solicitud1.getId(), null, null, null, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, null,
            null, null, null, null, null, TipoPresupuesto.GLOBAL, null, null, null, null, null, null, null, null));

    // @formatter:off
    RolSocio rolSocio = RolSocio.builder()
        .abreviatura("001")
        .nombre("Lider")
        .descripcion("Lider")
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build();
    entityManager.persistAndFlush(rolSocio);

    RolProyecto rolProyecto = RolProyecto.builder()
        .abreviatura("001")
        .nombre("Rol1")
        .descripcion("Rol1")
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

    SolicitudProyectoSocioEquipo solicitudProyectoEquipoSocio1 = entityManager.persistAndFlush(
        new SolicitudProyectoSocioEquipo(null, solicitudProyectoSocio1.getId(), "user-001", rolProyecto, 1, 3));

    SolicitudProyectoSocioEquipo solicitudProyectoEquipoSocio2 = entityManager.persistAndFlush(
        new SolicitudProyectoSocioEquipo(null, solicitudProyectoSocio1.getId(), "user-001", rolProyecto, 5, 6));

    entityManager.persistAndFlush(
        new SolicitudProyectoSocioEquipo(null, solicitudProyectoSocio2.getId(), "user-001", rolProyecto, 1, 3));

    Long proyectoSocioIdBuscado = solicitudProyectoSocio1.getId();

    // when: se buscan los SolicitudProyectoSocioEquipo por SolicitudProyectoSocioId
    List<SolicitudProyectoSocioEquipo> dataFound = repository.findAllBySolicitudProyectoSocioId(proyectoSocioIdBuscado);

    // then: Se recuperan los SolicitudProyectoSocioEquipo con el
    // SolicitudProyectoSocioId
    // buscado
    Assertions.assertThat(dataFound.size()).isEqualTo(2);
    Assertions.assertThat(dataFound.get(0).getSolicitudProyectoSocioId())
        .isEqualTo(solicitudProyectoEquipoSocio1.getSolicitudProyectoSocioId());
    Assertions.assertThat(dataFound.get(1).getSolicitudProyectoSocioId())
        .isEqualTo(solicitudProyectoEquipoSocio2.getSolicitudProyectoSocioId());
  }

}
