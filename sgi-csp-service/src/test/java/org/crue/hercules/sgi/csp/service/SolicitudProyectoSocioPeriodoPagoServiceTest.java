package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioPeriodoPagoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioPeriodoPagoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoSocioPeriodoPagoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * SolicitudProyectoSocioPeriodoPagoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudProyectoSocioPeriodoPagoServiceTest {

  @Mock
  private SolicitudProyectoSocioPeriodoPagoRepository repository;

  @Mock
  private SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  @Mock
  private SolicitudProyectoRepository solicitudProyectoRepository;

  private SolicitudProyectoSocioPeriodoPagoService service;

  @Mock
  private SolicitudService solicitudService;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoSocioPeriodoPagoServiceImpl(repository, solicitudProyectoSocioRepository,
        solicitudService, solicitudProyectoRepository);
  }

  @Test
  public void update_ReturnsSolicitudProyectoSocioPeriodoPago() {
    // given: una lista con uno de los SolicitudProyectoSocioPeriodoPago
    // actualizado,
    // otro nuevo y sin el otros existente
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoPagoExistentes.add(generarSolicitudProyectoSocioPeriodoPago(2L, 1L));
    solicitudProyectoSocioPeriodoPagoExistentes.add(generarSolicitudProyectoSocioPeriodoPago(4L, 1L));
    solicitudProyectoSocioPeriodoPagoExistentes.add(generarSolicitudProyectoSocioPeriodoPago(5L, 1L));

    SolicitudProyectoSocioPeriodoPago newSolicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(
        null, 1L);
    newSolicitudProyectoSocioPeriodoPago.setMes(5);
    SolicitudProyectoSocioPeriodoPago updatedSolicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(
        4L, 1L);
    updatedSolicitudProyectoSocioPeriodoPago.setMes(6);

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagoActualizar = new ArrayList<>();
    solicitudProyectoSocioPeriodoPagoActualizar.add(newSolicitudProyectoSocioPeriodoPago);
    solicitudProyectoSocioPeriodoPagoActualizar.add(updatedSolicitudProyectoSocioPeriodoPago);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoPagoExistentes);
    BDDMockito.doNothing().when(repository).deleteAll(ArgumentMatchers.<SolicitudProyectoSocioPeriodoPago>anyList());
    BDDMockito.given(repository.saveAll(ArgumentMatchers.<SolicitudProyectoSocioPeriodoPago>anyList()))
        .will((InvocationOnMock invocation) -> {
          List<SolicitudProyectoSocioPeriodoPago> periodosPagos = invocation.getArgument(0);
          return periodosPagos.stream().map(periodoPago -> {
            if (periodoPago.getId() == null) {
              periodoPago.setId(6L);
            }
            periodoPago.setSolicitudProyectoSocioId(solicitudProyectoSocioId);
            return periodoPago;
          }).collect(Collectors.toList());
        });
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: se actualiza solicitudProyectoSocioPeriodoPagoActualizar
    List<SolicitudProyectoSocioPeriodoPago> periodosPagoActualizados = service.update(solicitudProyectoSocioId,
        solicitudProyectoSocioPeriodoPagoActualizar);

    // then: El SolicitudProyectoSocioPeriodoPago se actualiza correctamente.

    // then: Se crea el nuevo ConvocatoriaPeriodoJustificacion, se actualiza el
    // existe y se elimina el otro
    Assertions.assertThat(periodosPagoActualizados.get(0).getId()).as("get(0).getId()").isEqualTo(6L);
    Assertions.assertThat(periodosPagoActualizados.get(0).getSolicitudProyectoSocioId())
        .as("get(0).getSolicitudProyectoSocioId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(0).getImporte()).as("get(0).getImporte()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoPago.getImporte());
    Assertions.assertThat(periodosPagoActualizados.get(0).getNumPeriodo()).as("get(0).getNumPeriodo()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoPago.getNumPeriodo());
    Assertions.assertThat(periodosPagoActualizados.get(0).getMes()).as("get(0).getMes()")
        .isEqualTo(newSolicitudProyectoSocioPeriodoPago.getMes());

    Assertions.assertThat(periodosPagoActualizados.get(1).getId()).as("get(1).getId()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoPago.getId());
    Assertions.assertThat(periodosPagoActualizados.get(1).getSolicitudProyectoSocioId())
        .as("get(1).getSolicitudProyectoSocioId()").isEqualTo(solicitudProyectoSocioId);
    Assertions.assertThat(periodosPagoActualizados.get(1).getImporte()).as("get(1).getImporte()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoPago.getImporte());
    Assertions.assertThat(periodosPagoActualizados.get(1).getNumPeriodo()).as("get(1).getNumPeriodo()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoPago.getNumPeriodo());
    Assertions.assertThat(periodosPagoActualizados.get(1).getMes()).as("get(1).getMes()")
        .isEqualTo(updatedSolicitudProyectoSocioPeriodoPago.getMes());

    Mockito.verify(repository, Mockito.times(1))
        .deleteAll(ArgumentMatchers.<SolicitudProyectoSocioPeriodoPago>anyList());
    Mockito.verify(repository, Mockito.times(1)).saveAll(ArgumentMatchers.<SolicitudProyectoSocioPeriodoPago>anyList());

  }

  @Test
  public void update_WithSolicitudProyectoSocioNotExist_ThrowsSolicitudProyectoSocioNotFoundException() {
    // given: a SolicitudProyectoSocioPeriodoPago with non existing
    // SolicitudProyectoSocio
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(1L,
        1L);

    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: update ConvocatoriaEntidadGestora
        () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoSocioPeriodoPago)))
        // then: throw exception as SolicitudProyectoSocio is not found
        .isInstanceOf(SolicitudProyectoSocioNotFoundException.class);
  }

  @Test
  public void update_WithIdNotExist_ThrowsSolicitudProyectoSocioPeriodoPagoNotFoundException() {
    // given: Un SolicitudProyectoSocioPeriodoPago actualizado con un id que no
    // existe
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    Long solicitudProyectoSocioPeriodoPagoId = 1L;
    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(
        solicitudProyectoSocioPeriodoPagoId, solicitudProyectoSocioId);
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoPagoExistentes
        .add(generarSolicitudProyectoSocioPeriodoPago(3L, solicitudProyectoSocioId));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoPagoExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioPeriodoPago
    // then: Lanza una excepcion porque el SolicitudProyectoSocioPeriodoPago no
    // existe
    Assertions
        .assertThatThrownBy(
            () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoSocioPeriodoPago)))
        .isInstanceOf(SolicitudProyectoSocioPeriodoPagoNotFoundException.class);
  }

  @Test
  public void update_WithSolicitudProyectoSocioChange_ThrowsIllegalArgumentException() {
    // given:Se actualiza SolicitudProyectoSocio
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    Long solicitudProyectoSocioPeriodoPagoId = 1L;
    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(
        solicitudProyectoSocioPeriodoPagoId, solicitudProyectoSocioId);
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);

    solicitudProyectoSocioPeriodoPago.setSolicitudProyectoSocioId(2L);

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoPagoExistentes.add(generarSolicitudProyectoSocioPeriodoPago(1L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoPagoExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocio del
    // SolicitudProyectoSocioPeriodoPago
    // then: Lanza una excepcion porque no se puede modificar el campo
    // SolicitudProyectoSocio
    Assertions
        .assertThatThrownBy(
            () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoSocioPeriodoPago)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No se puede modificar la solicitud proyecto socio del SolicitudProyectoSocioPeriodoPago");
  }

  @Test
  public void update_WithMesSuperiorDuracion_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoSocioPeriodoPago cuyo mes es superior a
    // la
    // duración de solicitud de proyecto
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    Long solicitudProyectoSocioPeriodoPagoId = 1L;
    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(
        solicitudProyectoSocioPeriodoPagoId, solicitudProyectoSocioId);
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);

    solicitudProyecto.setDuracion(2);

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoPagoExistentes.add(generarSolicitudProyectoSocioPeriodoPago(1L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoPagoExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioPeriodoPago
    // then: Lanza una excepcion porque el mes es superior a la duración de
    // solicitud de proyecto
    Assertions
        .assertThatThrownBy(
            () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoSocioPeriodoPago)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El mes no puede ser superior a la duración en meses indicada en la Convocatoria");
  }

  @Test
  public void update_WithMesSolapamiento_ThrowsIllegalArgumentException() {
    // given: Se actualiza SolicitudProyectoSocioPeriodoPago cuyo mes es superior a
    // la
    // duración de solicitud de proyecto
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago1 = generarSolicitudProyectoSocioPeriodoPago(1L,
        solicitudProyectoSocioId);
    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago2 = generarSolicitudProyectoSocioPeriodoPago(2L,
        solicitudProyectoSocioId);
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoPagoExistentes.add(generarSolicitudProyectoSocioPeriodoPago(1L, 1L));
    solicitudProyectoSocioPeriodoPagoExistentes.add(generarSolicitudProyectoSocioPeriodoPago(2L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoPagoExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioPeriodoPago
    // then: Lanza una excepcion porque el mes es superior a la duración de
    // solicitud de proyecto
    Assertions
        .assertThatThrownBy(() -> service.update(solicitudProyectoSocioId,
            Arrays.asList(solicitudProyectoSocioPeriodoPago1, solicitudProyectoSocioPeriodoPago2)))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("El periodo se solapa con otro existente");
  }

  @Test
  public void update_WithoutMes_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoSocioPeriodoPago que no tiene mes
    Long solicitudProyectoId = 1L;
    Long solicitudProyectoSocioId = 1L;
    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = generarSolicitudProyectoSocioPeriodoPago(1L,
        solicitudProyectoSocioId);
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId);
    SolicitudProyectoSocio solicitudProyectoSocio = generarMockSolicitudProyectoSocio(solicitudProyectoSocioId,
        solicitudProyectoId);

    solicitudProyectoSocioPeriodoPago.setMes(null);

    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPagoExistentes = new ArrayList<>();
    solicitudProyectoSocioPeriodoPagoExistentes.add(generarSolicitudProyectoSocioPeriodoPago(1L, 1L));

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(solicitudProyectoSocioRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoSocio));
    BDDMockito.given(repository.findAllBySolicitudProyectoSocioId(ArgumentMatchers.anyLong()))
        .willReturn(solicitudProyectoSocioPeriodoPagoExistentes);
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoSocioPeriodoPago
    // then: Lanza una excepcion porque no tiene mes
    Assertions
        .assertThatThrownBy(
            () -> service.update(solicitudProyectoSocioId, Arrays.asList(solicitudProyectoSocioPeriodoPago)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Mes no puede ser null para realizar la acción sobre SolicitudProyectoSocioPeriodoPago");
  }

  @Test
  public void findById_ReturnsSolicitudProyectoSocioPeriodoPago() {
    // given: Un SolicitudProyectoSocioPeriodoPago con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoSocioPeriodoPago(idBuscado, 1L)));

    // when: Buscamos el SolicitudProyectoSocioPeriodoPago por su id
    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = service.findById(idBuscado);

    // then: el SolicitudProyectoSocioPeriodoPago
    Assertions.assertThat(solicitudProyectoSocioPeriodoPago).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoSocioPeriodoPago.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoSocioPeriodoPagoNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoSocioPeriodoPago con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoSocioPeriodoPago por su id
    // then: lanza un SolicitudProyectoSocioPeriodoPagoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoSocioPeriodoPagoNotFoundException.class);
  }

  @Test
  public void findAllBySolicitudProyectoSocio_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoSocioPeriodoPago
    Long solicitudId = 1L;
    List<SolicitudProyectoSocioPeriodoPago> solicitudProyectoSocioPeriodoPago = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoSocioPeriodoPago.add(generarSolicitudProyectoSocioPeriodoPago(i, i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoSocioPeriodoPago>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<SolicitudProyectoSocioPeriodoPago>>() {
          @Override
          public Page<SolicitudProyectoSocioPeriodoPago> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > solicitudProyectoSocioPeriodoPago.size() ? solicitudProyectoSocioPeriodoPago.size()
                : toIndex;
            List<SolicitudProyectoSocioPeriodoPago> content = solicitudProyectoSocioPeriodoPago.subList(fromIndex,
                toIndex);
            Page<SolicitudProyectoSocioPeriodoPago> page = new PageImpl<>(content, pageable,
                solicitudProyectoSocioPeriodoPago.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoSocioPeriodoPago> page = service.findAllBySolicitudProyectoSocio(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPagoRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoSocioPeriodoPagoRecuperado.getId()).isEqualTo(i);
    }
  }

  private SolicitudProyecto generarMockSolicitudProyecto(Long solicitudProyectoId) {
    return SolicitudProyecto.builder().id(solicitudProyectoId).build();
  }

  private SolicitudProyectoSocio generarMockSolicitudProyectoSocio(Long solicitudProyectoSocioId,
      Long solicitudProyectoId) {
    return SolicitudProyectoSocio.builder().id(solicitudProyectoSocioId).solicitudProyectoId(solicitudProyectoId)
        .build();
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoSocioPeriodoPago
   * 
   * @param solicitudProyectoSocioPeriodoPagoId
   * @param solicitudProyectoSocioId
   * @return el objeto SolicitudProyectoSocioPeriodoPago
   */
  private SolicitudProyectoSocioPeriodoPago generarSolicitudProyectoSocioPeriodoPago(
      Long solicitudProyectoSocioPeriodoPagoId, Long solicitudProyectoSocioId) {

    SolicitudProyectoSocioPeriodoPago solicitudProyectoSocioPeriodoPago = SolicitudProyectoSocioPeriodoPago.builder()
        .id(solicitudProyectoSocioPeriodoPagoId).solicitudProyectoSocioId(solicitudProyectoSocioId).numPeriodo(3)
        .importe(new BigDecimal(358)).mes(3).build();

    return solicitudProyectoSocioPeriodoPago;
  }

}
