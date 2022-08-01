package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.dto.SolicitudHitoInput;
import org.crue.hercules.sgi.csp.exceptions.SolicitudHitoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.SolicitudHitoAvisoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudHitoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiComService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiTpService;
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
import org.springframework.data.jpa.domain.Specification;

/**
 * SolicitudHitoServiceTest
 */
@ExtendWith(MockitoExtension.class)
class SolicitudHitoServiceTest {

  @Mock
  private SolicitudHitoRepository repository;

  @Mock
  SolicitudHitoAvisoRepository solicitudHitoAvisoRepository;

  @Mock
  private SolicitudRepository solicitudRepository;

  @Mock
  private TipoHitoRepository tipoHitoRepository;

  @Mock
  private SolicitudService solicitudService;

  @Mock
  SgiApiComService emailService;

  @Mock
  SgiApiTpService sgiApiTaskService;

  @Mock
  SgiApiSgpService personaService;

  private SolicitudHitoService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new SolicitudHitoService(repository, solicitudRepository, tipoHitoRepository, solicitudService,
        solicitudHitoAvisoRepository, emailService, sgiApiTaskService, personaService);
  }

  @Test
  void create_WithConvocatoria_ReturnsSolicitudHito() {
    // given: Un nuevo SolicitudHito
    SolicitudHitoInput solicitudHito = generarSolicitudHitoInput(1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(true);
    BDDMockito.given(tipoHitoRepository.findById(1L)).willReturn(Optional.of(generarTipoHito(1L)));

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudHito>any())).will((InvocationOnMock invocation) -> {
      SolicitudHito solicitudHitoCreado = invocation.getArgument(0);
      if (solicitudHitoCreado.getId() == null) {
        solicitudHitoCreado.setId(1L);
      }

      return solicitudHitoCreado;
    });

    // when: Creamos el SolicitudHito
    SolicitudHito solicitudHitoCreado = service.create(solicitudHito);

    // then: El SolicitudHito se crea correctamente
    Assertions.assertThat(solicitudHitoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudHitoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudHitoCreado.getComentario()).as("getComentario()")
        .isEqualTo(solicitudHito.getComentario());
    Assertions.assertThat(solicitudHitoCreado.getTipoHito().getId()).as("getTipoHito().getId()")
        .isEqualTo(solicitudHito.getTipoHitoId());
    Assertions.assertThat(solicitudHitoCreado.getSolicitudHitoAviso()).as("getsolicitudHitoAviso()")
        .isNull();
  }

  @Test
  void create_WithoutSolicitudId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudHito que no tiene solicitud
    SolicitudHitoInput solicitudHito = generarSolicitudHitoInput(1L);

    solicitudHito.setSolicitudId(null);

    // when: Creamos el SolicitudHito
    // then: Lanza una excepcion porque no tiene solicitud
    Assertions.assertThatThrownBy(() -> service.create(solicitudHito)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La solicitud no puede ser null para crear la SolicitudHito");
  }

  @Test
  void create_WithoutTipoHitoId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudHito que no tiene programa
    SolicitudHitoInput solicitudHito = generarSolicitudHitoInput(1L);

    solicitudHito.setTipoHitoId(null);

    // when: Creamos el SolicitudHito
    // then: Lanza una excepcion porque no tiene programa
    Assertions.assertThatThrownBy(() -> service.create(solicitudHito)).isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El tipo hito no puede ser null para crear la SolicitudHito");
  }

  @Test
  void create_WithNoExistingSolicitud_ThrowsSolicitudNotFoundException() {
    // given: Un nuevo SolicitudHito que tiene una solicitud que no existe
    SolicitudHitoInput solicitudHito = generarSolicitudHitoInput(1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(false);

    // when: Creamos el SolicitudHito
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudHito)).isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  void create_WithNoExistingTipoHito_ThrowsTipoHitoNotFoundException() {
    // given: Un nuevo SolicitudHito que tiene un tipo hito que no existe
    SolicitudHitoInput solicitudHito = generarSolicitudHitoInput(1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(true);
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Creamos el SolicitudHito
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudHito)).isInstanceOf(TipoHitoNotFoundException.class);
  }

  @Test
  void create_WithFechaYTipoHitoDuplicado_ThrowsIllegalArgumentException() {
    // given: a SolicitudHito fecha duplicada
    SolicitudHito solicitudHitoExistente = generarSolicitudHito(1L);
    solicitudHitoExistente.setId(2L);
    SolicitudHitoInput solicitudHito = generarSolicitudHitoInput(1L);

    BDDMockito
        .given(repository.findBySolicitudIdAndFechaAndTipoHitoId(ArgumentMatchers.<Long>any(),
            ArgumentMatchers.<Instant>any(), ArgumentMatchers.<Long>any()))
        .willReturn(Optional.of(solicitudHitoExistente));

    Assertions.assertThatThrownBy(
        // when: create SolicitudHito
        () -> service.create(solicitudHito))
        // then: throw exception as fecha is null
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un Hito con el mismo tipo en esa fecha");
  }

  @Test
  void update_ReturnsSolicitudHito() {
    // given: Un nuevo SolicitudHito con los comentarios actualizados
    SolicitudHito solicitudHito = generarSolicitudHito(1L);

    SolicitudHitoInput solicitudComentarioActualizado = generarSolicitudHitoInput(1L);

    solicitudComentarioActualizado.setComentario("comentario-actualizado");

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(true);
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudHito.getTipoHito()));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(solicitudHito));

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudHito>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudHito
    SolicitudHito solicitudHitoActualizada = service.update(1L, solicitudComentarioActualizado);

    // then: El SolicitudHito se actualiza correctamente.
    Assertions.assertThat(solicitudHitoActualizada).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudHitoActualizada.getId()).as("getId()").isEqualTo(solicitudHito.getId());
    Assertions.assertThat(solicitudHitoActualizada.getComentario()).as("getComentario()")
        .isEqualTo(solicitudHito.getComentario());

  }

  @Test
  void update_WithSolicitudNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un SolicitudHito actualizado con un programa que no existe
    SolicitudHitoInput solicitudHito = generarSolicitudHitoInput(1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(false);

    // when: Actualizamos el SolicitudHito
    // then: Lanza una excepcion porque la solicitud asociada no existe
    Assertions.assertThatThrownBy(() -> service.update(1L, solicitudHito))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  void update_WithTipoHitoNotExist_ThrowsTipoHitoNotFoundException() {
    // given: Un SolicitudHito actualizado con un tipo hito que no existe
    SolicitudHitoInput solicitudHito = generarSolicitudHitoInput(1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(true);
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    // when: Actualizamos el SolicitudHito
    // then: Lanza una excepcion porque la solicitud asociada no existe
    Assertions.assertThatThrownBy(() -> service.update(1L, solicitudHito))
        .isInstanceOf(TipoHitoNotFoundException.class);
  }

  @Test
  void update_WithIdNotExist_ThrowsSolicitudHitoNotFoundException() {
    // given: Un SolicitudHito actualizado con un id que no existe
    SolicitudHitoInput solicitudHito = generarSolicitudHitoInput(1L);
    TipoHito tipoHito = generarTipoHito(1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(true);
    BDDMockito.given(tipoHitoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(tipoHito));

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudHito
    // then: Lanza una excepcion porque el SolicitudHito no existe
    Assertions.assertThatThrownBy(() -> service.update(1L, solicitudHito))
        .isInstanceOf(SolicitudHitoNotFoundException.class);
  }

  @Test
  void update_WithFechaYTipoHitoDuplicado_ThrowsIllegalArgumentException() {
    // given: Un SolicitudHito a actualizar con fecha duplicada
    SolicitudHito solicitudHitoExistente = generarSolicitudHito(1L);
    solicitudHitoExistente.setId(2L);
    SolicitudHitoInput solicitudHito = generarSolicitudHitoInput(1L);

    BDDMockito.given(repository.findBySolicitudIdAndFechaAndTipoHitoId(ArgumentMatchers.<Long>any(),
        ArgumentMatchers.any(), ArgumentMatchers.<Long>any())).willReturn(Optional.of(solicitudHitoExistente));

    // when: Actualizamos el SolicitudHito
    // then: Lanza una excepcion porque la fecha ya existe para ese tipo
    Assertions.assertThatThrownBy(() -> service.update(1L, solicitudHito))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Ya existe un Hito con el mismo tipo en esa fecha");
  }

  @Test
  void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing SolicitudHito
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(repository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: delete by existing id
        () -> service.delete(id))
        // then: no exception is thrown
        .doesNotThrowAnyException();
  }

  @Test
  void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(SolicitudHitoNotFoundException.class);
  }

  @Test
  void findById_ReturnsSolicitudHito() {
    // given: Un SolicitudHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarSolicitudHito(idBuscado)));

    // when: Buscamos el SolicitudHito por su id
    SolicitudHito solicitudHito = service.findById(idBuscado);

    // then: el SolicitudHito
    Assertions.assertThat(solicitudHito).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudHito.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  void findById_WithIdNotExist_ThrowsSolicitudHitoNotFoundException() throws Exception {
    // given: Ningun SolicitudHito con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudHito por su id
    // then: lanza un SolicitudHitoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(SolicitudHitoNotFoundException.class);
  }

  @Test
  void findAll_ReturnsPage() {
    // given: Una lista con 37 SolicitudHito
    Long solicitudId = 1L;
    List<SolicitudHito> solicitudHito = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudHito.add(generarSolicitudHito(i));
    }

    BDDMockito
        .given(
            repository.findAll(ArgumentMatchers.<Specification<SolicitudHito>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<SolicitudHito>>() {
          @Override
          public Page<SolicitudHito> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudHito.size() ? solicitudHito.size() : toIndex;
            List<SolicitudHito> content = solicitudHito.subList(fromIndex, toIndex);
            Page<SolicitudHito> page = new PageImpl<>(content, pageable, solicitudHito.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudHito> page = service.findAllBySolicitud(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().size()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudHito solicitudHitoRecuperado = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudHitoRecuperado.getId()).isEqualTo(i);
    }
  }

  private Solicitud generarMockSolicitud(Long solicitudId) {
    Solicitud solicitud = Solicitud.builder().id(solicitudId).build();
    solicitud.setEstado(new EstadoSolicitud());
    solicitud.getEstado().setEstado(EstadoSolicitud.Estado.BORRADOR);
    return solicitud;
  }

  private TipoHito generarTipoHito(Long id) {
    // @formatter:off
    return TipoHito.builder()
        .id(id)
        .nombre("TipoHito")
        .activo(true)
        .build();
    // @formatter:on
  }

  /**
   * Función que devuelve un objeto SolicitudHito
   * 
   * @param solicitudHitoId
   * @param solicitudId
   * @param tipoDocumentoId
   * @return el objeto SolicitudHito
   */
  private SolicitudHito generarSolicitudHito(Long id) {
    SolicitudHito solicitudHito = new SolicitudHito();
    solicitudHito.setId(id);
    solicitudHito.setSolicitudId(id == null ? 1 : id);
    solicitudHito.setFecha(Instant.parse("2020-10-19T00:00:00Z"));
    solicitudHito.setComentario("comentario");
    solicitudHito.setTipoHito(generarTipoHito(id == null ? 1 : id));

    return solicitudHito;
  }

  private SolicitudHitoInput generarSolicitudHitoInput(Long id) {

    SolicitudHitoInput convocatoriaHito = new SolicitudHitoInput();
    convocatoriaHito.setSolicitudId(id);
    convocatoriaHito.setTipoHitoId(id);
    convocatoriaHito.setFecha(Instant.parse("2020-10-19T00:00:00Z"));
    convocatoriaHito.setComentario("comentario");

    return convocatoriaHito;
  }
}
