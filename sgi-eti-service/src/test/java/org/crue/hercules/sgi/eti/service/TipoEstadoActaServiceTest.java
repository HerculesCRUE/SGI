package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoEstadoActaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.repository.TipoEstadoActaRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoEstadoActaServiceImpl;
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
 * TipoEstadoActaServiceTest
 */
public class TipoEstadoActaServiceTest extends BaseServiceTest {

  @Mock
  private TipoEstadoActaRepository tipoEstadoActaRepository;

  private TipoEstadoActaService tipoEstadoActaService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoEstadoActaService = new TipoEstadoActaServiceImpl(tipoEstadoActaRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoEstadoActa() {
    BDDMockito.given(tipoEstadoActaRepository.findById(1L))
        .willReturn(Optional.of(generarMockTipoEstadoActa(1L, "TipoEstadoActa1")));

    TipoEstadoActa tipoEstadoActa = tipoEstadoActaService.findById(1L);

    Assertions.assertThat(tipoEstadoActa.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoEstadoActa.getNombre()).isEqualTo("TipoEstadoActa1");

  }

  @Test
  public void find_NotFound_ThrowsTipoEstadoActaNotFoundException() throws Exception {
    BDDMockito.given(tipoEstadoActaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoEstadoActaService.findById(1L))
        .isInstanceOf(TipoEstadoActaNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoEstadoActa() {
    // given: Un nuevo TipoEstadoActa
    TipoEstadoActa tipoEstadoActaNew = generarMockTipoEstadoActa(1L, "TipoEstadoActaNew");

    TipoEstadoActa tipoEstadoActa = generarMockTipoEstadoActa(1L, "TipoEstadoActaNew");

    BDDMockito.given(tipoEstadoActaRepository.save(tipoEstadoActaNew)).willReturn(tipoEstadoActa);

    // when: Creamos el tipoEstadoActa
    TipoEstadoActa tipoEstadoActaCreado = tipoEstadoActaService.create(tipoEstadoActaNew);

    // then: El tipoEstadoActa se crea correctamente
    Assertions.assertThat(tipoEstadoActaCreado).isNotNull();
    Assertions.assertThat(tipoEstadoActaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEstadoActaCreado.getNombre()).isEqualTo("TipoEstadoActaNew");
  }

  @Test
  public void create_TipoEstadoActaWithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo tipoEstadoActa que ya tiene id
    TipoEstadoActa tipoEstadoActaNew = generarMockTipoEstadoActa(null, "TipoEstadoActaNew");
    // when: Creamos el tipoEstadoActa
    // then: Lanza una excepcion porque el tipoEstadoActa ya tiene id
    Assertions.assertThatThrownBy(() -> tipoEstadoActaService.create(tipoEstadoActaNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTipoEstadoActa() {
    // given: Un nuevo tipoEstadoActa con el servicio actualizado
    TipoEstadoActa tipoEstadoActaServicioActualizado = generarMockTipoEstadoActa(1L, "TipoEstadoActa1 actualizada");

    TipoEstadoActa tipoEstadoActa = generarMockTipoEstadoActa(1L, "TipoEstadoActa1");

    BDDMockito.given(tipoEstadoActaRepository.findById(1L)).willReturn(Optional.of(tipoEstadoActa));
    BDDMockito.given(tipoEstadoActaRepository.save(tipoEstadoActa)).willReturn(tipoEstadoActaServicioActualizado);

    // when: Actualizamos el tipoEstadoActa
    TipoEstadoActa tipoEstadoActaActualizado = tipoEstadoActaService.update(tipoEstadoActa);

    // then: El tipoEstadoActa se actualiza correctamente.
    Assertions.assertThat(tipoEstadoActaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEstadoActaActualizado.getNombre()).isEqualTo("TipoEstadoActa1 actualizada");

  }

  @Test
  public void update_ThrowsTipoEstadoActaNotFoundException() {
    // given: Un nuevo tipoEstadoActa a actualizar
    TipoEstadoActa tipoEstadoActa = generarMockTipoEstadoActa(1L, "TipoEstadoActa");

    // then: Lanza una excepcion porque el tipoEstadoActa no existe
    Assertions.assertThatThrownBy(() -> tipoEstadoActaService.update(tipoEstadoActa))
        .isInstanceOf(TipoEstadoActaNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un TipoEstadoActa que venga sin id
    TipoEstadoActa tipoEstadoActa = generarMockTipoEstadoActa(null, "TipoEstadoActa");

    Assertions.assertThatThrownBy(
        // when: update TipoEstadoActa
        () -> tipoEstadoActaService.update(tipoEstadoActa))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tipoEstadoActaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTipoEstadoActaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tipoEstadoActaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tipoEstadoActaService.delete(1L))
        // then: Lanza TipoEstadoActaNotFoundException
        .isInstanceOf(TipoEstadoActaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTipoEstadoActa() {
    // given: Id existente
    BDDMockito.given(tipoEstadoActaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tipoEstadoActaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tipoEstadoActaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoEstadoActaList() {
    // given: One hundred TipoEstadoActa
    List<TipoEstadoActa> tipoEstadoActas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEstadoActas.add(generarMockTipoEstadoActa(Long.valueOf(i), "TipoEstadoActa" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEstadoActaRepository.findAll(ArgumentMatchers.<Specification<TipoEstadoActa>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoEstadoActas));

    // when: find unlimited
    Page<TipoEstadoActa> page = tipoEstadoActaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoEstadoActas
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoEstadoActas
    List<TipoEstadoActa> tipoEstadoActas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEstadoActas.add(generarMockTipoEstadoActa(Long.valueOf(i), "TipoEstadoActa" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEstadoActaRepository.findAll(ArgumentMatchers.<Specification<TipoEstadoActa>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoEstadoActa>>() {
          @Override
          public Page<TipoEstadoActa> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoEstadoActa> content = tipoEstadoActas.subList(fromIndex, toIndex);
            Page<TipoEstadoActa> page = new PageImpl<>(content, pageable, tipoEstadoActas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoEstadoActa> page = tipoEstadoActaService.findAll(null, paging);

    // then: A Page with ten TipoEstadoActas are returned containing
    // descripcion='TipoEstadoActa031' to 'TipoEstadoActa040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoEstadoActa tipoEstadoActa = page.getContent().get(i);
      Assertions.assertThat(tipoEstadoActa.getNombre()).isEqualTo("TipoEstadoActa" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoEstadoActa
   * 
   * @param id     id del TipoEstadoActa
   * @param nombre nombre del tipoEstadoActa
   * @return el objeto tipoEstadoActa
   */

  public TipoEstadoActa generarMockTipoEstadoActa(Long id, String nombre) {

    TipoEstadoActa tipoEstadoActa = new TipoEstadoActa();
    tipoEstadoActa.setId(id);
    tipoEstadoActa.setNombre(nombre);
    tipoEstadoActa.setActivo(Boolean.TRUE);

    return tipoEstadoActa;
  }
}