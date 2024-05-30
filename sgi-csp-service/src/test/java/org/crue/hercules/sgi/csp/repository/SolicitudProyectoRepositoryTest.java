package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto.TipoPresupuesto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * SolicitudProyectoRepositoryTest
 */
@DataJpaTest
class SolicitudProyectoRepositoryTest extends BaseRepositoryTest {
  @Autowired
  private SolicitudProyectoRepository repository;

  @Test
  void findBySolicitudId_ReturnsSolicitudProyecto() throws Exception {

    // given: 2 SolicitudProyecto de los que 1 coincide con el idSolicitud
    // buscado

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
    SolicitudProyecto solicitudProyecto1 = entityManager.persistAndFlush(
        new SolicitudProyecto(solicitud1.getId(), null, null, null, Boolean.TRUE, null, Boolean.TRUE, null,
            null, null, null, null, null, TipoPresupuesto.GLOBAL, null, null, null, null, null, null, null, null));

    // @formatter:off
    Solicitud solicitud2 = entityManager.persistAndFlush(Solicitud.builder()
        .creadorRef("user-001")
        .titulo("titulo")
        .solicitanteRef("user-002")
        .unidadGestionRef("1")
        .formularioSolicitud(FormularioSolicitud.GRUPO)
        .activo(Boolean.TRUE)
        .build());
    // @formatter:on
    entityManager.persistAndFlush(
        new SolicitudProyecto(solicitud2.getId(), null, null, null, Boolean.TRUE, null, Boolean.TRUE, null,
            null, null, null, null, null, TipoPresupuesto.GLOBAL, null, null, null, null, null, null, null, null));

    Long convocatoriaIdBuscada = solicitud1.getId();

    // when: se busca el SolicitudProyecto por idSolicitud
    SolicitudProyecto solicitudProyectoEncontrado = repository.findById(convocatoriaIdBuscada).get();

    // then: Se recupera el SolicitudProyecto con el idSolicitud buscado
    Assertions.assertThat(solicitudProyectoEncontrado.getId()).as("getId").isNotNull();

  }

  @Test
  void findBySolicitudNoExiste_ReturnsNull() throws Exception {

    // given: 2 SolicitudProyecto de los que ninguno coincide con el
    // idSolicitud
    // buscado
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
    entityManager.persistAndFlush(
        new SolicitudProyecto(solicitud1.getId(), null, null, null, Boolean.TRUE, null, Boolean.TRUE, null,
            null, null, null, null, null, TipoPresupuesto.GLOBAL, null, null, null, null, null, null, null, null));
    // @formatter:off
    Solicitud solicitud2 = entityManager.persistAndFlush(Solicitud.builder()
        .creadorRef("user-001")
        .titulo("titulo")
        .solicitanteRef("user-002")
        .unidadGestionRef("1")
        .formularioSolicitud(FormularioSolicitud.GRUPO)
        .activo(Boolean.TRUE)
        .build());
    // @formatter:on
    entityManager.persistAndFlush(
        new SolicitudProyecto(solicitud2.getId(), null, null, null, Boolean.TRUE, null, Boolean.TRUE, null,
            null, null, null, null, null, TipoPresupuesto.GLOBAL, null, null, null, null, null, null, null, null));

    Long solicitudIdBuscada = 99999L;

    // when: se busca el SolicitudProyecto por solicitudId
    Optional<SolicitudProyecto> solicitudProyectoEncontrado = repository.findById(solicitudIdBuscada);

    // then: Se recupera el SolicitudProyecto con el solicitudId buscado
    Assertions.assertThat(solicitudProyectoEncontrado).isEqualTo(Optional.empty());
  }

}
