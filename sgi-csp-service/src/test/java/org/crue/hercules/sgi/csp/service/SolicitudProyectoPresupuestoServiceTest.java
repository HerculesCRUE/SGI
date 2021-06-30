package org.crue.hercules.sgi.csp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoPresupuestoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPresupuestoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.SolicitudProyectoPresupuestoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * SolicitudProyectoPresupuestoServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class SolicitudProyectoPresupuestoServiceTest {

  @Mock
  private SolicitudProyectoPresupuestoRepository repository;

  @Mock
  private SolicitudService solicitudService;

  @Mock
  private SolicitudProyectoRepository solicitudProyectoRepository;

  private SolicitudProyectoPresupuestoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new SolicitudProyectoPresupuestoServiceImpl(repository, solicitudService, solicitudProyectoRepository);
  }

  @Test
  public void create_ReturnsSolicitudProyectoPresupuesto() {
    // given: Un nuevo SolicitudProyectoPresupuesto
    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = generarSolicitudProyectoPresupuesto(null, 1L, 1L);

    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyectoPresupuesto>any()))
        .will((InvocationOnMock invocation) -> {
          SolicitudProyectoPresupuesto solicitudProyectoPresupuestoCreado = invocation.getArgument(0);
          if (solicitudProyectoPresupuestoCreado.getId() == null) {
            solicitudProyectoPresupuestoCreado.setId(1L);
          }

          return solicitudProyectoPresupuestoCreado;
        });

    // when: Creamos el SolicitudProyectoPresupuesto
    SolicitudProyectoPresupuesto solicitudProyectoPresupuestoCreado = service.create(solicitudProyectoPresupuesto);

    // then: El SolicitudProyectoPresupuesto se crea correctamente
    Assertions.assertThat(solicitudProyectoPresupuestoCreado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoPresupuestoCreado.getId()).as("getId()").isEqualTo(1L);
    Assertions.assertThat(solicitudProyectoPresupuestoCreado.getSolicitudProyectoId()).as("getSolicitudProyectoId()")
        .isEqualTo(solicitudProyectoPresupuesto.getSolicitudProyectoId());
    Assertions.assertThat(solicitudProyectoPresupuestoCreado.getConceptoGasto().getId())
        .as("getConceptoGasto().getId()").isEqualTo(solicitudProyectoPresupuesto.getConceptoGasto().getId());
    Assertions.assertThat(solicitudProyectoPresupuestoCreado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(solicitudProyectoPresupuesto.getEntidadRef());
    Assertions.assertThat(solicitudProyectoPresupuestoCreado.getAnualidad()).as("getAnualidad()")
        .isEqualTo(solicitudProyectoPresupuesto.getAnualidad());
    Assertions.assertThat(solicitudProyectoPresupuestoCreado.getImporteSolicitado()).as("getImporteSolicitado()")
        .isEqualTo(solicitudProyectoPresupuesto.getImporteSolicitado());
    Assertions.assertThat(solicitudProyectoPresupuestoCreado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(solicitudProyectoPresupuesto.getObservaciones());
    Assertions.assertThat(solicitudProyectoPresupuestoCreado.getFinanciacionAjena()).as("getFinanciacionAjena()")
        .isEqualTo(solicitudProyectoPresupuesto.getFinanciacionAjena());
  }

  @Test
  public void create_WithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo SolicitudProyectoPresupuesto que ya tiene id
    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = generarSolicitudProyectoPresupuesto(1L, 1L, 1L);

    // when: Creamos el SolicitudProyectoPresupuesto
    // then: Lanza una excepcion porque el SolicitudProyectoPresupuesto ya tiene id
    Assertions.assertThatThrownBy(() -> service.create(solicitudProyectoPresupuesto))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Id tiene que ser null para crear la SolicitudProyectoPresupuesto");
  }

  @Test
  public void update_ReturnsSolicitudProyectoPresupuesto() {
    // given: Un nuevo SolicitudProyectoPresupuesto con el titulo actualizado
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = generarSolicitudProyectoPresupuesto(3L, 1L, 1L);

    SolicitudProyectoPresupuesto solicitudProyectoPresupuestoActualizar = generarSolicitudProyectoPresupuesto(3L, 1L,
        1L);
    solicitudProyectoPresupuestoActualizar.setObservaciones("actualizado");

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyectoPresupuesto));
    BDDMockito.given(repository.save(ArgumentMatchers.<SolicitudProyectoPresupuesto>any()))
        .will((InvocationOnMock invocation) -> invocation.getArgument(0));
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPresupuesto
    SolicitudProyectoPresupuesto solicitudProyectoPresupuestoActualizado = service
        .update(solicitudProyectoPresupuestoActualizar);

    // then: El SolicitudProyectoPresupuesto se actualiza correctamente.
    Assertions.assertThat(solicitudProyectoPresupuestoActualizado).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoPresupuestoActualizado.getId()).as("getId()")
        .isEqualTo(solicitudProyectoPresupuesto.getId());
    Assertions.assertThat(solicitudProyectoPresupuestoActualizado.getSolicitudProyectoId())
        .as("getSolicitudProyectoId()").isEqualTo(solicitudProyectoPresupuesto.getSolicitudProyectoId());
    Assertions.assertThat(solicitudProyectoPresupuestoActualizado.getConceptoGasto().getId())
        .as("getConceptoGasto().getId()").isEqualTo(solicitudProyectoPresupuesto.getConceptoGasto().getId());
    Assertions.assertThat(solicitudProyectoPresupuestoActualizado.getEntidadRef()).as("getEntidadRef()")
        .isEqualTo(solicitudProyectoPresupuesto.getEntidadRef());
    Assertions.assertThat(solicitudProyectoPresupuestoActualizado.getAnualidad()).as("getAnualidad()")
        .isEqualTo(solicitudProyectoPresupuesto.getAnualidad());
    Assertions.assertThat(solicitudProyectoPresupuestoActualizado.getImporteSolicitado()).as("getImporteSolicitado()")
        .isEqualTo(solicitudProyectoPresupuesto.getImporteSolicitado());
    Assertions.assertThat(solicitudProyectoPresupuestoActualizado.getObservaciones()).as("getObservaciones()")
        .isEqualTo(solicitudProyectoPresupuestoActualizar.getObservaciones());
    Assertions.assertThat(solicitudProyectoPresupuestoActualizado.getFinanciacionAjena()).as("getFinanciacionAjena()")
        .isEqualTo(solicitudProyectoPresupuesto.getFinanciacionAjena());

  }

  @Test
  public void update_WithIdNotExist_ThrowsSolicitudProyectoPresupuestoNotFoundException() {
    // given: Un SolicitudProyectoPresupuesto actualizado con un id que no existe
    Long solicitudId = 1L;
    Long solicitudProyectoId = 1L;
    SolicitudProyecto solicitudProyecto = generarMockSolicitudProyecto(solicitudProyectoId, solicitudId);
    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = generarSolicitudProyectoPresupuesto(1L, 1L, 1L);

    BDDMockito.given(solicitudProyectoRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(solicitudProyecto));
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());
    BDDMockito.given(solicitudService.modificable(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);

    // when: Actualizamos el SolicitudProyectoPresupuesto
    // then: Lanza una excepcion porque el SolicitudProyectoPresupuesto no existe
    Assertions.assertThatThrownBy(() -> service.update(solicitudProyectoPresupuesto))
        .isInstanceOf(SolicitudProyectoPresupuestoNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_NoReturnsAnyException() {
    // given: existing SolicitudProyectoPresupuesto
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
        .isInstanceOf(SolicitudProyectoPresupuestoNotFoundException.class);
  }

  @Test
  public void findById_ReturnsSolicitudProyectoPresupuesto() {
    // given: Un SolicitudProyectoPresupuesto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarSolicitudProyectoPresupuesto(idBuscado, 1L, 1L)));

    // when: Buscamos el SolicitudProyectoPresupuesto por su id
    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = service.findById(idBuscado);

    // then: el SolicitudProyectoPresupuesto
    Assertions.assertThat(solicitudProyectoPresupuesto).as("isNotNull()").isNotNull();
    Assertions.assertThat(solicitudProyectoPresupuesto.getId()).as("getId()").isEqualTo(idBuscado);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsSolicitudProyectoPresupuestoNotFoundException() throws Exception {
    // given: Ningun SolicitudProyectoPresupuesto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el SolicitudProyectoPresupuesto por su id
    // then: lanza un SolicitudProyectoPresupuestoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(SolicitudProyectoPresupuestoNotFoundException.class);
  }

  @Test
  public void findAllBySolicitud_ReturnsPage() {
    // given: Una lista con 37 SolicitudProyectoPresupuesto
    Long solicitudId = 1L;
    List<SolicitudProyectoPresupuesto> solicitudProyectoPresupuesto = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      solicitudProyectoPresupuesto.add(generarSolicitudProyectoPresupuesto(i, i, i));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<SolicitudProyectoPresupuesto>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer((InvocationOnMock invocation) -> {
          Pageable pageable = invocation.getArgument(1, Pageable.class);
          int size = pageable.getPageSize();
          int index = pageable.getPageNumber();
          int fromIndex = size * index;
          int toIndex = fromIndex + size;
          toIndex = toIndex > solicitudProyectoPresupuesto.size() ? solicitudProyectoPresupuesto.size() : toIndex;
          List<SolicitudProyectoPresupuesto> content = solicitudProyectoPresupuesto.subList(fromIndex, toIndex);
          Page<SolicitudProyectoPresupuesto> page = new PageImpl<>(content, pageable,
              solicitudProyectoPresupuesto.size());
          return page;
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<SolicitudProyectoPresupuesto> page = service.findAllBySolicitud(solicitudId, null, paging);

    // then: Devuelve la pagina 3 con los Programa del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      SolicitudProyectoPresupuesto solicitudProyectoPresupuestoRecuperado = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(solicitudProyectoPresupuestoRecuperado.getId()).isEqualTo(i);
    }
  }

  private SolicitudProyecto generarMockSolicitudProyecto(Long solicitudProyectoId, Long solicitudId) {
    return SolicitudProyecto.builder().id(solicitudProyectoId).build();
  }

  /**
   * Función que devuelve un objeto SolicitudProyectoPresupuesto
   * 
   * @param id                  Id {@link SolicitudProyectoPresupuesto}.
   * @param solicitudProyectoId Id {@link SolicitudProyecto}.
   * @param conceptoGastoId     Id {@link ConceptoGasto}.
   * @return el objeto {@link SolicitudProyectoPresupuesto}.
   */
  private SolicitudProyectoPresupuesto generarSolicitudProyectoPresupuesto(Long id, Long solicitudProyectoId,
      Long conceptoGastoId) {

    String suffix = String.format("%03d", id);

    SolicitudProyectoPresupuesto solicitudProyectoPresupuesto = SolicitudProyectoPresupuesto
        .builder()// @formatter:off
        .id(id)
        .solicitudProyectoId(solicitudProyectoId)
        .conceptoGasto(ConceptoGasto.builder().id(conceptoGastoId).build())
        .entidadRef(null)
        .anualidad(1000)
        .importeSolicitado(new BigDecimal("335"))
        .observaciones("observaciones-" + suffix)
        .financiacionAjena(false)
        .build();// @formatter:on

    return solicitudProyectoPresupuesto;
  }

}
