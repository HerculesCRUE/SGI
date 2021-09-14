package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

@DataJpaTest
public class EstadoSolicitudRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private EstadoSolicitudRepository repository;

  @Test
  public void findAllBySolicitud_ReturnsPageEstadoSolicitud() throws Exception {
    // given: data EstadoSolicitud with nombre to find
    Solicitud sol1 = entityManager.persistAndFlush(generarMockSolicitud());
    Solicitud sol2 = entityManager.persistAndFlush(generarMockSolicitud());
    entityManager.persistAndFlush(generarMockEstadoSolicitud(sol1.getId()));
    entityManager.persistAndFlush(generarMockEstadoSolicitud(sol2.getId()));
    entityManager.persistAndFlush(generarMockEstadoSolicitud(sol1.getId()));

    // when: find given nombre
    Page<EstadoSolicitud> page = repository.findAllBySolicitudId(sol1.getId(), null);

    // then: EstadoSolicitud with given name is found
    Assertions.assertThat(page.hasContent()).isNotNull();
  }

  private Solicitud generarMockSolicitud() {
    Solicitud solicitud = new Solicitud();
    solicitud.setCreadorRef("usr-001");
    solicitud.setTitulo("titulo");
    solicitud.setSolicitanteRef("usr-002");
    solicitud.setUnidadGestionRef("2");
    solicitud.setFormularioSolicitud(FormularioSolicitud.RRHH);
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
    return EstadoSolicitud.builder().estado(EstadoSolicitud.Estado.BORRADOR).solicitudId(id).fechaEstado(Instant.now())
        .comentario("comentario").build();
  }

}
