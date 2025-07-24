package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitudComentario;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudTitulo;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

@DataJpaTest
class EstadoSolicitudRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private EstadoSolicitudRepository repository;

  @Test
  void findAllBySolicitud_ReturnsPageEstadoSolicitud() {
    // given: data EstadoSolicitud with nombre to find
    Solicitud sol1 = entityManager.persistAndFlush(generarMockSolicitud());
    Solicitud sol2 = entityManager.persistAndFlush(generarMockSolicitud());
    entityManager.persistAndFlush(generarMockEstadoSolicitud(sol1.getId()));
    entityManager.persistAndFlush(generarMockEstadoSolicitud(sol2.getId()));
    entityManager.persistAndFlush(generarMockEstadoSolicitud(sol1.getId()));

    // when: find given nombre
    Page<EstadoSolicitud> page = repository.findAllBySolicitudId(sol1.getId(), null);

    // then: EstadoSolicitud with given name is found
    Assertions.assertThat(page.hasContent()).isTrue();
  }

  private Solicitud generarMockSolicitud() {
    Set<SolicitudTitulo> solicitudTitulo = new HashSet<>();
    solicitudTitulo.add(new SolicitudTitulo(Language.ES, "titulo"));

    Solicitud solicitud = new Solicitud();
    solicitud.setCreadorRef("usr-001");
    solicitud.setTitulo(solicitudTitulo);
    solicitud.setSolicitanteRef("usr-002");
    solicitud.setUnidadGestionRef("2");
    solicitud.setFormularioSolicitud(FormularioSolicitud.GRUPO);
    solicitud.setActivo(true);

    return solicitud;
  }

  /**
   * Función que devuelve un objeto EstadoSolicitud
   * 
   * @param id identificador de la solicitud
   * @return EstadoSolicitud
   */
  private EstadoSolicitud generarMockEstadoSolicitud(Long id) {
    Set<EstadoSolicitudComentario> comentarioEstadoSolicitud = new HashSet<>();
    comentarioEstadoSolicitud.add(new EstadoSolicitudComentario(Language.ES, "comentario"));

    return EstadoSolicitud.builder()
        .estado(EstadoSolicitud.Estado.BORRADOR)
        .solicitudId(id)
        .fechaEstado(Instant.now())
        .comentario(comentarioEstadoSolicitud)
        .build();
  }

}
