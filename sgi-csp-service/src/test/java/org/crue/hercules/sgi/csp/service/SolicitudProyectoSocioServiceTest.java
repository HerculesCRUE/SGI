package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoSocioServiceImpl;
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
 * SolicitudProyectoSocioServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudProyectoSocioServiceTest {

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

  private SolicitudProyectoSocioService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoSocioServiceImpl(repository, solicitudRepository,
        solicitudProyectoEquipoSocioRepository, solicitudProyectoSocioPeriodoPagoRepository,
        solicitudProyectoSocioPeriodoJustificacionRepository, solicitudService);
  }

  @Test
  public void create__ReturnsSolicitudProyectoSocio() {
    // given: Un nuevo SolicitudProyectoSocio
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

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
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoSocio que ya tiene id
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    // when: Creamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque el SolicitudProyectoSocio ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear la SolicitudProyectoSocio");
  }

  @Test
  public void create_WithoutSolicitudProyectoId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoSocio que no tiene solicitud de proyecto
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    solicitudProyectoSocio.setSolicitudProyectoId(null);

    // when: Creamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene solicitud de proyecto
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Proyecto datos no puede ser null para realizar la acción sobre SolicitudProyectoSocio");
  }

  @Test
  public void create_WithoutRolSocio_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoSocio que no tiene rol socio
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    solicitudProyectoSocio.setRolSocio(null);

    // when: Creamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene rol socio
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Rol socio no puede ser null para realizar la acción sobre SolicitudProyectoSocio");
  }

  @Test
  public void create_WithNoExistingSolicitud_ThrowsSolicitudNotFoundException() {
    // given: Un nuevo SolicitudProyectoSocio que tiene una solicitud que no existe
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Creamos el SolicitudProyectoSocio
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoSocio))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  public void create_WithOutEmpresaRef_ThrowsSolicitudNotFoundException() {
    // given: Un nuevo SolicitudProyectoSocio que no tiene empresa ref
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(null, 1L, 1L);
    solicitudProyectoSocio.setEmpresaRef(null);

    // when: Creamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene empresa ref
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Empresa ref no puede ser null para realizar la acción sobre SolicitudProyectoSocio");
  }

  @Test
  public void update_ReturnsSolicitudProyectoSocio() {
    // given: Un nuevo SolicitudProyectoSocio con el titulo actualizado
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(3L, 1L, 1L);

    SolicitudProyectoSocio solicitudProyectoSocioActualizado = generarSolicitudProyectoSocio(3L, 1L, 1L);

    solicitudProyectoSocioActualizado.setMesFin(12);

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
  public void update_WithSolicitudNotExist_ThrowsSolicitudNotFoundException() {
    // given: Un SolicitudProyectoSocio actualizado con un programa que no existe
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque la solicitud asociada no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsSolicitudProyectoSocioNotFoundException() {
    // given: Un SolicitudProyectoSocio actualizado con un id que no existe
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque el SolicitudProyectoSocio no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_WithoutSolicitudProyectoId_ThrowsIllegalArgumentException() {
    // given: Actualizar SolicitudProyectoSocio que no tiene solicitud proyecto
    // datos
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    solicitudProyectoSocio.setSolicitudProyectoId(null);

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene solicitud de proyecto
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Proyecto datos no puede ser null para realizar la acción sobre SolicitudProyectoSocio");
  }

  @Test
  public void update_WithoutRolSocio_ThrowsIllegalArgumentException() {
    // given: Actualizar SolicitudProyectoSocio que no tiene rol socio
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    solicitudProyectoSocio.setRolSocio(null);
    ;

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene rol socio
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Rol socio no puede ser null para realizar la acción sobre SolicitudProyectoSocio");
  }

  @Test
  public void update_WithNoExistingSolicitud_ThrowsSolicitudNotFoundException() {
    // given: Actualizar SolicitudProyectoSocio que tiene una solicitud que no
    // existe
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);

    BDDMockito.given(solicitudRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(SolicitudNotFoundException.class);
  }

  @Test
  public void update_WithOutEmpresaRef_ThrowsSolicitudNotFoundException() {
    // given: Actualizada SolicitudProyectoSocio que no tiene empresa ref
    SolicitudProyectoSocio solicitudProyectoSocio = generarSolicitudProyectoSocio(1L, 1L, 1L);
    solicitudProyectoSocio.setEmpresaRef(null);

    // when: Actualizamos el SolicitudProyectoSocio
    // then: Lanza una excepcion porque no tiene empresa ref
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoSocio))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Empresa ref no puede ser null para realizar la acción sobre SolicitudProyectoSocio");
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing SolicitudProyectoSocio
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
  public void delete_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    Long id = 1L;

    BDDMockito.given(repository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: delete
        () -> service.delete(id))
        // then: NotFoundException is thrown
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  public void findById_ReturnsSolicitudProyectoSocio() {
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
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoSocioNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoSocio con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoSocio por su id
    // then: lanza un SolicitudProyectoSocioNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  public void findAllBySolicitud_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoSocio
    Long solicitudId = 1L;
    List<SolicitudProyectoSocio> solicitudProyectoSocio = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoSocio.add(generarSolicitudProyectoSocio(i, i, i));
    }

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
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
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

}
