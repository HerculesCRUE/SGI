package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud.OrigenSolicitud;
import org.crue.hercules.sgi.csp.model.SolicitudObservaciones;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudTitulo;
import org.crue.hercules.sgi.csp.repository.SolicitudExternaRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoSocioServiceImpl;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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
 * SolicitudProyectoSocioServiceTest
 */
class SolicitudProyectoSocioServiceTest extends BaseServiceTest {

  @Mock
  private SolicitudProyectoSocioRepository repository;

  @Mock
  private SolicitudRepository solicitudRepository;

  @Mock
  private SolicitudProyectoSocioPeriodoPagoRepository solicitudProyectoSocioPeriodoPagoRepository;

  @Mock
  private SolicitudProyectoSocioEquipoRepository solicitudProyectoEquipoSocioRepository;

  @Mock
  private SolicitudProyectoSocioPeriodoJustificacionRepository solicitudProyectoSocioPeriodoJustificacionRepository;

  @Mock
  private SolicitudService solicitudService;

  @Mock
  private SolicitudExternaRepository solicitudExternaRepository;

  private SolicitudProyectoSocioService service;
  private SolicitudAuthorityHelper solicitudAuthorityHelper;

  @BeforeEach
  void setUp() throws Exception {
    solicitudAuthorityHelper = new SolicitudAuthorityHelper(solicitudRepository, solicitudExternaRepository);
    service = new SolicitudProyectoSocioServiceImpl(repository, solicitudRepository,
        solicitudProyectoEquipoSocioRepository, solicitudProyectoSocioPeriodoPagoRepository,
        solicitudProyectoSocioPeriodoJustificacionRepository, solicitudService, solicitudAuthorityHelper);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create__ReturnsSolicitudProyectoSocio() {
    // given: Un nuevo SolicitudProyectoSocio
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .will((InvocationOnMock invocation) -> {
          SolicitudProyectoSocio solicitudProyectoSocioCreado = invocation.getArgument(0);
          if (solicitudProyectoSocioCreado.getId() == null) {
            solicitudProyectoSocioCreado.setId(1L);
          }

          return solicitudProyectoSocioCreado;
        });

    // when: Creamos el SolicitudProyectoSocio
    SolicitudProyectoSocio solicitudProyectoSocioCreado = service.create(solicitudProyectoSocio);

    // then: El SolicitudProyectoSocio se crea correctamente
    Assertions.assertThat(solicitudProyectoSocioCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoSocioCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudProyectoSocioCreado.getSolicitudProyectoId()).as("getSolicitudProyectoId()")
        .isEqualTo(solicitudProyectoSocio.getSolicitudProyectoId());
    Assertions.assertThat(solicitudProyectoSocioCreado.getRolSocio().getId()).as("getRolSocio().getId()")
        .isEqualTo(solicitudProyectoSocio.getRolSocio().getId());
    Assertions.assertThat(solicitudProyectoSocioCreado.getMesInicio()).as("getMesInicio()")
        .isEqualTo(solicitudProyectoSocio.getMesInicio());
    Assertions.assertThat(solicitudProyectoSocioCreado.getMesFin()).as("getMesFin()")
        .isEqualTo(solicitudProyectoSocio.getMesFin());
    Assertions.assertThat(solicitudProyectoSocioCreado.getNumInvestigadores()).as("getNumInvestigadores()")
        .isEqualTo(solicitudProyectoSocio.getNumInvestigadores());
    Assertions.assertThat(solicitudProyectoSocioCreado.getImporteSolicitado()).as("getImporteSolicitado()")
        .isEqualTo(solicitudProyectoSocio.getImporteSolicitado());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoSocio que ya tiene id
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    // when: Creamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque el SolicitudProyectoSocio ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Solicitud Proyecto Socio debe ser nulo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithoutSolicitudProyectoId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoSocio que no tiene solicitud de proyecto
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    solicitudProyectoSocio.setSolicitudProyectoId(null);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    // when: Creamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene solicitud de proyecto
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Datos de Proyecto no puede ser nulo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithoutRolSocio_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoSocio que no tiene rol socio
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    solicitudProyectoSocio.setRolSocio(null);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    // when: Creamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene rol socio
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Rol de socio de proyecto de Solicitud Proyecto Socio no puede ser nulo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithNoExistingSolicitud_ThrowsSolicitudNotFoundException() {
    // given: Un nuevo SolicitudProyectoSocio que tiene una solicitud que no existe
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));
    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Creamos el SolicitudProyectoSocio
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoSocio))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void create_WithOutEmpresaRef_ThrowsSolicitudNotFoundException() {
    // given: Un nuevo SolicitudProyectoSocio que no tiene empresa ref
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);
    solicitudProyectoSocio.setEmpresaRef(null);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    // when: Creamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene empresa ref
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Referencia Empresa de Solicitud Proyecto Socio no puede ser nulo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_ReturnsSolicitudProyectoSocio() {
    // given: Un nuevo SolicitudProyectoSocio con el titulo actualizado
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(3L, 1L, 1L);

    SolicitudProyectoSocio solicitudProyectoSocioActualizado = generarSolicitudProyectoSocio(3L, 1L, 1L);

    solicitudProyectoSocioActualizado.setMesFin(12);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));
    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitudProyectoSocio));

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyectoSocio>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocio
    SolicitudProyectoSocio solicitudProyectoSocioActualizada = service.update(solicitudProyectoSocioActualizado);

    // then: El SolicitudProyectoSocio se actualiza correctamente.
    Assertions.assertThat(solicitudProyectoSocioActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoSocioActualizada.getId()).as("getId()")
        .isEqualTo(solicitudProyectoSocio.getId());
    Assertions.assertThat(solicitudProyectoSocioActualizada.getMesFin()).as("getMesFin()")
        .isEqualTo(solicitudProyectoSocio.getMesFin());

  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithSolicitudNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un SolicitudProyectoSocio actualizado con un programa que no existe
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));
    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque la solicitud asociada no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithIdNotExist_ThrowsSolicitudProyectoSocioNotFoundException() {
    // given: Un SolicitudProyectoSocio actualizado con un id que no existe
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));
    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque el SolicitudProyectoSocio no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithoutSolicitudProyectoId_ThrowsIllegalArgumentException() {
    // given: Actualizar SolicitudProyectoSocio que no tiene solicitud proyecto
    // datos
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    solicitudProyectoSocio.setSolicitudProyectoId(null);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene solicitud de proyecto
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Identificador de Datos de Proyecto no puede ser nulo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithoutRolSocio_ThrowsIllegalArgumentException() {
    // given: Actualizar SolicitudProyectoSocio que no tiene rol socio
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    solicitudProyectoSocio.setRolSocio(null);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene rol socio
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Rol de socio de proyecto de Solicitud Proyecto Socio no puede ser nulo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithNoExistingSolicitud_ThrowsSolicitudNotFoundException() {
    // given: Actualizar SolicitudProyectoSocio que tiene una solicitud que no
    // existe
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));
    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void update_WithOutEmpresaRef_ThrowsSolicitudNotFoundException() {
    // given: Actualizada SolicitudProyectoSocio que no tiene empresa ref
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);
    solicitudProyectoSocio.setEmpresaRef(null);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene empresa ref
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Referencia Empresa de Solicitud Proyecto Socio no puede ser nulo");
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing SolicitudProyectoSocio
    Long id = 1L;
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  void findById_ReturnsSolicitudProyectoSocio() {
    // given: Un SolicitudProyectoSocio con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoSocio(idBuscado, 1L, 1L)));

    // when: Buscamos el SolicitudProyectoSocio por su id
    SolicitudProyectoSocio solicitudProyectoSocio = service.findById(idBuscado);

    // then: el SolicitudProyectoSocio
    Assertions.assertThat(solicitudProyectoSocio).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoSocio.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsSolicitudProyectoSocioNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoSocio con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoSocio por su id
    // then: lanza un SolicitudProyectoSocioNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-SOL-E" })
  void findAllBySolicitud_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoSocio
    Long solicitudId = 1L;
    List<SolicitudProyectoSocio> solicitudProyectoSocio = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoSocio.add(generarSolicitudProyectoSocio(i, i, i));
    }

    BDDMockito.given(solicitudRepository.findById(ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(generarMockSolicitud(1L, 1L, null)));
    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoSocio>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<SolicitudProyectoSocio>>() {
          @Override
          public Page<SolicitudProyectoSocio> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudProyectoSocio.size() ? solicitudProyectoSocio.size() : toIndex;
            List<SolicitudProyectoSocio> content = solicitudProyectoSocio.subList(fromIndex, toIndex);
            Page<SolicitudProyectoSocio> page = new PageImpl<>(content, pageable, solicitudProyectoSocio.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoSocio> page = service.findAllBySolicitud(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoSocio solicitudProyectoSocioRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoSocioRecuperado.getId()).isEqualTo(i);
    }
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocio
   * 
   * @param solicitudProyectoSocioId
   * @param solicitudProyectoId
   * @return el objeto SolicitudProyectoSocio
   */
  private SolicitudProyectoSocio generarSolicitudProyectoSocio(Long solicitudProyectoSocioId, Long solicitudProyectoId,
      Long rolSocioId) {

    SolicitudProyectoSocio solicitudProyectoSocio = SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId)
        .solicitudProyectoId(solicitudProyectoId).rolSocio(RolSocio.builder().id(rolSocioId).build()).mesInicio(1)
        .mesFin(3).numInvestigadores(2).importeSolicitado(new BigDecimal("335")).empresaRef("002").build();

    return solicitudProyectoSocio;
  }

  /**
   * Función que devuelve un objeto Solicitud
   * 
   * @param id                  id del Solicitud
   * @param convocatoriaId      id de la Convocatoria
   * @param convocatoriaExterna convocatoria externa
   * @return el objeto Solicitud
   */
  private Solicitud generarMockSolicitud(Long id, Long convocatoriaId, String convocatoriaExterna) {
    EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
    estadoSolicitud.setId(1L);
    estadoSolicitud.setEstado(EstadoSolicitud.Estado.BORRADOR);

    Programa programa = new Programa();
    programa.setId(1L);

    Set<SolicitudTitulo> solicitudTitulo = new HashSet<>();
    solicitudTitulo.add(new SolicitudTitulo(Language.ES, "titulo"));

    Set<SolicitudObservaciones> solicitudObservaciones = new HashSet<>();
    solicitudObservaciones.add(new SolicitudObservaciones(Language.ES, "observaciones-" + String.format("%03d", id)));

    Solicitud solicitud = new Solicitud();
    solicitud.setId(id);
    solicitud.setTitulo(solicitudTitulo);
    solicitud.setCodigoExterno(null);
    solicitud.setConvocatoriaId(convocatoriaId);
    solicitud.setCreadorRef("usr-001");
    solicitud.setSolicitanteRef("usr-002");
    solicitud.setObservaciones(solicitudObservaciones);
    solicitud.setConvocatoriaExterna(convocatoriaExterna);
    solicitud.setUnidadGestionRef("1");
    solicitud.setActivo(true);
    solicitud.setFormularioSolicitud(FormularioSolicitud.PROYECTO);
    solicitud.setOrigenSolicitud(
        convocatoriaId != null ? OrigenSolicitud.CONVOCATORIA_SGI : OrigenSolicitud.CONVOCATORIA_NO_SGI);

    if (id != null) {
      solicitud.setEstado(estadoSolicitud);
      solicitud.setCodigoRegistroInterno("SGI_SLC1202011061027");
      solicitud.setCreadorRef("usr-001");
    }

    return solicitud;
  }

}
