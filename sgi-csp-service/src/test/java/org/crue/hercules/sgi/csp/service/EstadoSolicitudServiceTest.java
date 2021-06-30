package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.repository.EstadoSolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.EstadoSolicitudServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * EstadoSolicitudServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class EstadoSolicitudServiceTest {

  @Mock
  private EstadoSolicitudRepository repository;

  private EstadoSolicitudService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new EstadoSolicitudServiceImpl(repository);
  }

  @Test
  public void create_WithConvocatoria_ReturnsEstadoSolicitud() {
    // given: Un nuevo EstadoSolicitud
    EstadoSolicitud estadoSolicitud = generarMockEstadoSolicitud(null);

    BDDMockito.given(repository.save(ArgumentMatchers.<EstadoSolicitud>any())).will((InvocationOnMock invocation) -> {
      EstadoSolicitud estadoSolicitudCreado = invocation.getArgument(0);
      if (estadoSolicitudCreado.getId() == null) {
        estadoSolicitudCreado.setId(1L);
      }

      return estadoSolicitudCreado;
    });

    // when: Creamos el EstadoSolicitud
    EstadoSolicitud estadoSolicitudCreado = service.create(estadoSolicitud);

    // then: El EstadoSolicitud se crea correctamente
    Assertions.assertThat(estadoSolicitudCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(estadoSolicitudCreado.getId()).as("getId()").isEqualTo(1L);
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo EstadoSolicitud que ya tiene id
    EstadoSolicitud solicitudModalidad = generarMockEstadoSolicitud(1L);

    // when: Creamos el EstadoSolicitud
    // then: Lanza una excepcion porque el EstadoSolicitud ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitudModalidad)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("EstadoSolicitud id tiene que ser null para crear un EstadoSolicitud");
  }

  @Test
  public void create_WithoutSolicitudId_ThrowsIllegalArgumentException() {
    // given: Un nuevo EstadoSolicitud que no tiene solicitud
    EstadoSolicitud estadoSolicitud = generarMockEstadoSolicitud(null);
    estadoSolicitud.setSolicitudId(null);

    // when: Creamos el EstadoSolicitud
    // then: Lanza una excepcion porque no tiene solicitud
    Assertions.assertThatThrownBy(() -> service.create(estadoSolicitud)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("idSolicitud no puede ser null para crear un EstadoSolicitud");
  }

  @Test
  public void findAllBySolicitud_ReturnsPage() {
    // given: Una lista con 37 EstadoSolicitud
    Long solicitudId = 1L;
    List<EstadoSolicitud> estadosSolicitud = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      estadosSolicitud.add(generarMockEstadoSolicitud(i));
    }

    BDDMockito.given(repository.findAllBySolicitudId(ArgumentMatchers.anyLong(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EstadoSolicitud>>() {
          @Override
          public Page<EstadoSolicitud> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > estadosSolicitud.size() ? estadosSolicitud.size() : toIndex;
            List<EstadoSolicitud> content = estadosSolicitud.subList(fromIndex, toIndex);
            Page<EstadoSolicitud> page = new PageImpl<>(content, pageable, estadosSolicitud.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<EstadoSolicitud> page = service.findAllBySolicitud(solicitudId, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      EstadoSolicitud estadoSolicitud = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(estadoSolicitud.getId()).isEqualTo(i);
    }
  }

  /**
   * Función que devuelve un objeto EstadoSolicitud
   * 
   * @param id id del EstadoSolicitud
   * @return el objeto EstadoSolicitud
   */
  private EstadoSolicitud generarMockEstadoSolicitud(Long id) {
    EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
    estadoSolicitud.setId(id);
    estadoSolicitud.setSolicitudId(1L);
    estadoSolicitud.setComentario("Comentario");
    estadoSolicitud.setEstado(EstadoSolicitud.Estado.BORRADOR);
    estadoSolicitud.setFechaEstado(Instant.now());

    return estadoSolicitud;
  }
}
