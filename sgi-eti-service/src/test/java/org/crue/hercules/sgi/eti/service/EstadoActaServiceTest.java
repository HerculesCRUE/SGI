package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.EstadoActaNotFoundException;
import org.crue.hercules.sgi.eti.model.Acta;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.repository.EstadoActaRepository;
import org.crue.hercules.sgi.eti.service.impl.EstadoActaServiceImpl;
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

/**
 * EstadoActaServiceTest
 */
public class EstadoActaServiceTest extends BaseServiceTest {

  @Mock
  private EstadoActaRepository estadoActaRepository;

  private EstadoActaService estadoActaService;

  @BeforeEach
  public void setUp() throws Exception {
    estadoActaService = new EstadoActaServiceImpl(estadoActaRepository);
  }

  @Test
  public void find_WithId_ReturnsEstadoActa() {
    BDDMockito.given(estadoActaRepository.findById(1L)).willReturn(Optional.of(generarMockEstadoActa(1L)));

    EstadoActa estadoActa = estadoActaService.findById(1L);

    Assertions.assertThat(estadoActa.getId()).isEqualTo(1L);
    Assertions.assertThat(estadoActa.getActa().getId()).isEqualTo(100L);
  }

  @Test
  public void find_NotFound_ThrowsEstadoActaNotFoundException() throws Exception {
    BDDMockito.given(estadoActaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> estadoActaService.findById(1L)).isInstanceOf(EstadoActaNotFoundException.class);
  }

  @Test
  public void create_ReturnsEstadoActa() {
    // given: Un nuevo EstadoActa
    EstadoActa estadoActaNew = generarMockEstadoActa(null);

    EstadoActa estadoActa = generarMockEstadoActa(1L);

    BDDMockito.given(estadoActaRepository.save(estadoActaNew)).willReturn(estadoActa);

    // when: Creamos el estado acta
    EstadoActa estaoActaCreado = estadoActaService.create(estadoActaNew);

    // then: el estado acta se crea correctamente
    Assertions.assertThat(estaoActaCreado).isNotNull();
    Assertions.assertThat(estaoActaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(estaoActaCreado.getActa().getId()).isEqualTo(estadoActaNew.getActa().getId());
  }

  @Test
  public void create_EstadoActaWithId_ThrowsIllegalArgumentException() {
    // given: Un nuevo estado acta que tiene id
    EstadoActa estadoActaNew = generarMockEstadoActa(1L);
    // when: Creamos el estado acta
    // then: Lanza una excepcion porque el estado acta ya tiene id
    Assertions.assertThatThrownBy(() -> estadoActaService.create(estadoActaNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsEstadoActa() {
    // given: Un nuevo estado acta con la descripcion actualizada
    TipoEstadoActa nuevoTipoEstadoActa = new TipoEstadoActa(2L, "tipo2", true);
    EstadoActa estadoActaTipoActualizado = generarMockEstadoActa(1L);
    estadoActaTipoActualizado.setTipoEstadoActa(nuevoTipoEstadoActa);

    EstadoActa estadoActa = generarMockEstadoActa(1L);

    BDDMockito.given(estadoActaRepository.findById(1L)).willReturn(Optional.of(estadoActa));
    BDDMockito.given(estadoActaRepository.save(estadoActaTipoActualizado)).willReturn(estadoActaTipoActualizado);

    // when: Actualizamos el estado acta
    EstadoActa estadoActaActualizado = estadoActaService.update(estadoActaTipoActualizado);

    // then: El estado acta se actualiza correctamente.
    Assertions.assertThat(estadoActaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(estadoActaActualizado.getTipoEstadoActa().getId()).isEqualTo(nuevoTipoEstadoActa.getId());
  }

  @Test
  public void update_ThrowsEstadoActaNotFoundException() {
    // given: Un estado acta a actualizar
    EstadoActa estadoActaTipoActualizado = generarMockEstadoActa(1L);

    // then: Lanza una excepcion porque el estado acta no existe
    Assertions.assertThatThrownBy(() -> estadoActaService.update(estadoActaTipoActualizado))
        .isInstanceOf(EstadoActaNotFoundException.class);
  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: Un estado acta que venga sin id
    EstadoActa estadoActaTipoActualizado = generarMockEstadoActa(null);

    Assertions.assertThatThrownBy(
        // when: update estado acta
        () -> estadoActaService.update(estadoActaTipoActualizado))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> estadoActaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsEstadoActaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(estadoActaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> estadoActaService.delete(1L))
        // then: Lanza EstadoActaNotFoundException
        .isInstanceOf(EstadoActaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesEstadoActa() {
    // given: Id existente
    BDDMockito.given(estadoActaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(estadoActaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> estadoActaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullEstadoActaList() {
    // given: One hundred estados actas
    List<EstadoActa> estadosActas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      estadosActas.add(generarMockEstadoActa(Long.valueOf(i)));
    }

    BDDMockito.given(estadoActaRepository.findAll(ArgumentMatchers.<Specification<EstadoActa>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(estadosActas));

    // when: find unlimited
    Page<EstadoActa> page = estadoActaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred estados actas
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred estados actas
    List<EstadoActa> estadosActas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      estadosActas.add(generarMockEstadoActa(Long.valueOf(i)));
    }

    BDDMockito.given(estadoActaRepository.findAll(ArgumentMatchers.<Specification<EstadoActa>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<EstadoActa>>() {
          @Override
          public Page<EstadoActa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<EstadoActa> content = estadosActas.subList(fromIndex, toIndex);
            Page<EstadoActa> page = new PageImpl<>(content, pageable, estadosActas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<EstadoActa> page = estadoActaService.findAll(null, paging);

    // then: A Page with ten estados actas are returned containing id='31' to '40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      EstadoActa estadoActa = page.getContent().get(i);
      Assertions.assertThat(estadoActa.getId()).isEqualTo(j);
    }
  }

  /**
   * Función que devuelve un objeto EstadoActa
   * 
   * @param id id del estado acta
   * @return el objeto EstadoActa
   */
  public EstadoActa generarMockEstadoActa(Long id) {
    Acta acta = new Acta();
    acta.setId(100L);

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(200L);

    EstadoActa estadoActa = new EstadoActa();
    estadoActa.setId(id);
    estadoActa.setActa(acta);
    estadoActa.setTipoEstadoActa(tipoEstadoActa);
    estadoActa.setFechaEstado(Instant.parse("2020-07-14T00:00:00Z"));

    return estadoActa;
  }

}
