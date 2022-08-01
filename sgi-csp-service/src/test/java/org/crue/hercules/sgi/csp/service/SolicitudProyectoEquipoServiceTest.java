package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoEquipoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * SolicitudProyectoEquipoServiceTest
 */
class SolicitudProyectoEquipoServiceTest extends BaseServiceTest {

  @Mock
  private SolicitudProyectoEquipoRepository repository;

  @Mock
  private RolProyectoRepository rolProyectoRepository;

  @Mock
  private SolicitudService solicitudService;

  @Mock
  private SolicitudRepository solicitudRepository;

  @Mock
  private Validator validator;

  private SolicitudProyectoEquipoService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new SolicitudProyectoEquipoServiceImpl(validator, repository, rolProyectoRepository, solicitudRepository);
  }

  @Test
  void findById_ReturnsSolicitudProyectoEquipo() {
    // given: Un SolicitudProyectoEquipo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoEquipo(idBuscado, 1L, 1L)));

    // when: Buscamos el SolicitudProyectoEquipo por su id
    SolicitudProyectoEquipo solicitudProyectoEquipo = service.findById(idBuscado);

    // then: el SolicitudProyectoEquipo
    Assertions.assertThat(solicitudProyectoEquipo).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoEquipo.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsSolicitudProyectoEquipoNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoEquipo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoEquipo por su id
    // then: lanza un SolicitudProyectoEquipoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoEquipoNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void findAll_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoEquipo
    Long solicitudId = 1L;
    Solicitud solicitud = new Solicitud();
    solicitud.setId(solicitudId);
    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(solicitud));
    List<SolicitudProyectoEquipo> solicitudProyectoEquipo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoEquipo.add(generarSolicitudProyectoEquipo(i, i, i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoEquipo>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<SolicitudProyectoEquipo>>() {
          @Override
          public Page<SolicitudProyectoEquipo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudProyectoEquipo.size() ? solicitudProyectoEquipo.size() : toIndex;
            List<SolicitudProyectoEquipo> content = solicitudProyectoEquipo.subList(fromIndex, toIndex);
            Page<SolicitudProyectoEquipo> page = new PageImpl<>(content, pageable, solicitudProyectoEquipo.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoEquipo> page = service.findAllBySolicitud(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoEquipo solicitudProyectoEquipoRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoEquipoRecuperado.getId()).isEqualTo(i);
    }
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoEquipo
   * 
   * @param solicitudProyectoEquipoId
   * @param solicitudProyectoId
   * @param tipoDocumentoId
   * @return el objeto SolicitudProyectoEquipo
   */
  private SolicitudProyectoEquipo generarSolicitudProyectoEquipo(Long solicitudProyectoEquipoId,
      Long solicitudProyectoId, Long rolProyectoId) {

    SolicitudProyectoEquipo solicitudProyectoEquipo = SolicitudProyectoEquipo.builder().id(solicitudProyectoEquipoId)
        .solicitudProyectoId(1L).personaRef("personaRef-" + solicitudProyectoEquipoId)
        .rolProyecto(RolProyecto.builder().id(rolProyectoId).build()).mesInicio(1).mesFin(5).build();

    return solicitudProyectoEquipo;
  }

}
