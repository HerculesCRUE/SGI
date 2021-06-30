package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoEstadoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.repository.TipoEstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoEstadoMemoriaServiceImpl;
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
 * TipoEstadoMemoriaServiceTest
 */
public class TipoEstadoMemoriaServiceTest extends BaseServiceTest {

  @Mock
  private TipoEstadoMemoriaRepository tipoEstadoMemoriaRepository;

  private TipoEstadoMemoriaService tipoEstadoMemoriaService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoEstadoMemoriaService = new TipoEstadoMemoriaServiceImpl(tipoEstadoMemoriaRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoEstadoMemoria() {
    BDDMockito.given(tipoEstadoMemoriaRepository.findById(1L))
        .willReturn(Optional.of(generarMockTipoEstadoMemoria(1L, "TipoEstadoMemoria1")));

    TipoEstadoMemoria tipoEstadoMemoria = tipoEstadoMemoriaService.findById(1L);

    Assertions.assertThat(tipoEstadoMemoria.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoEstadoMemoria.getNombre()).isEqualTo("TipoEstadoMemoria1");

  }

  @Test
  public void find_NotFound_ThrowsTipoEstadoMemoriaNotFoundException() throws Exception {
    BDDMockito.given(tipoEstadoMemoriaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoEstadoMemoriaService.findById(1L))
        .isInstanceOf(TipoEstadoMemoriaNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoEstadoMemoria() {
    // given: Un nuevo TipoEstadoMemoria
    TipoEstadoMemoria tipoEstadoMemoriaNew = generarMockTipoEstadoMemoria(1L, "TipoEstadoMemoriaNew");

    TipoEstadoMemoria tipoEstadoMemoria = generarMockTipoEstadoMemoria(1L, "TipoEstadoMemoriaNew");

    BDDMockito.given(tipoEstadoMemoriaRepository.save(tipoEstadoMemoriaNew)).willReturn(tipoEstadoMemoria);

    // when: Creamos el tipo estado memoria
    TipoEstadoMemoria tipoEstadoMemoriaCreado = tipoEstadoMemoriaService.create(tipoEstadoMemoriaNew);

    // then: El tipo estado memoria se crea correctamente
    Assertions.assertThat(tipoEstadoMemoriaCreado).isNotNull();
    Assertions.assertThat(tipoEstadoMemoriaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEstadoMemoriaCreado.getNombre()).isEqualTo("TipoEstadoMemoriaNew");
  }

  @Test
  public void create_TipoEstadoMemoriaWithNoId_ThrowsIllegalArgumentException() {
    // given: Un nuevo tipo estado de Memoria que no tiene id
    TipoEstadoMemoria tipoEstadoMemoriaNew = generarMockTipoEstadoMemoria(null, "TipoEstadoMemoriaNew");
    // when: Creamos el tipo estado de Memoria
    // then: Lanza una excepcion porque el tipo estado Memoria no tiene id
    Assertions.assertThatThrownBy(() -> tipoEstadoMemoriaService.create(tipoEstadoMemoriaNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTipoEstadoMemoria() {
    // given: Un nuevo TipoEstadoMemoria con el servicio actualizado
    TipoEstadoMemoria tipoEstadoMemoriaServicioActualizado = generarMockTipoEstadoMemoria(1L,
        "TipoEstadoMemoria1 actualizada");

    TipoEstadoMemoria tipoEstadoMemoria = generarMockTipoEstadoMemoria(1L, "TipoEstadoMemoria1");

    BDDMockito.given(tipoEstadoMemoriaRepository.findById(1L)).willReturn(Optional.of(tipoEstadoMemoria));
    BDDMockito.given(tipoEstadoMemoriaRepository.save(tipoEstadoMemoria))
        .willReturn(tipoEstadoMemoriaServicioActualizado);

    // when: Actualizamos el TipoEstadoMemoria
    TipoEstadoMemoria tipoEstadoMemoriaActualizado = tipoEstadoMemoriaService.update(tipoEstadoMemoria);

    // then: El tipo estado Memoria se actualiza correctamente.
    Assertions.assertThat(tipoEstadoMemoriaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoEstadoMemoriaActualizado.getNombre()).isEqualTo("TipoEstadoMemoria1 actualizada");

  }

  @Test
  public void update_ThrowsTipoEstadoMemoriaNotFoundException() {
    // given: Un nuevo tipo estado Memoria a actualizar
    TipoEstadoMemoria tipoEstadoMemoria = generarMockTipoEstadoMemoria(1L, "TipoEstadoMemoria");

    // then: Lanza una excepcion porque el tipo estado Memoria no existe
    Assertions.assertThatThrownBy(() -> tipoEstadoMemoriaService.update(tipoEstadoMemoria))
        .isInstanceOf(TipoEstadoMemoriaNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un TipoEstadoMemoria que venga sin id
    TipoEstadoMemoria tipoEstadoMemoria = generarMockTipoEstadoMemoria(null, "TipoEstadoMemoria");

    Assertions.assertThatThrownBy(
        // when: update TipoEstadoMemoria
        () -> tipoEstadoMemoriaService.update(tipoEstadoMemoria))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tipoEstadoMemoriaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTipoEstadoMemoriaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tipoEstadoMemoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tipoEstadoMemoriaService.delete(1L))
        // then: Lanza TipoEstadoMemoriaNotFoundException
        .isInstanceOf(TipoEstadoMemoriaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTipoEstadoMemoria() {
    // given: Id existente
    BDDMockito.given(tipoEstadoMemoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tipoEstadoMemoriaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tipoEstadoMemoriaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoEstadoMemoriaList() {
    // given: One hundred TipoEstadoMemoria
    List<TipoEstadoMemoria> tipoEstadoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEstadoMemorias
          .add(generarMockTipoEstadoMemoria(Long.valueOf(i), "TipoEstadoMemoria" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEstadoMemoriaRepository.findAll(ArgumentMatchers.<Specification<TipoEstadoMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoEstadoMemorias));

    // when: find unlimited
    Page<TipoEstadoMemoria> page = tipoEstadoMemoriaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoEstadoMemorias
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoEstadoMemorias
    List<TipoEstadoMemoria> tipoEstadoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoEstadoMemorias
          .add(generarMockTipoEstadoMemoria(Long.valueOf(i), "TipoEstadoMemoria" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoEstadoMemoriaRepository.findAll(ArgumentMatchers.<Specification<TipoEstadoMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoEstadoMemoria>>() {
          @Override
          public Page<TipoEstadoMemoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoEstadoMemoria> content = tipoEstadoMemorias.subList(fromIndex, toIndex);
            Page<TipoEstadoMemoria> page = new PageImpl<>(content, pageable, tipoEstadoMemorias.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoEstadoMemoria> page = tipoEstadoMemoriaService.findAll(null, paging);

    // then: A Page with ten TipoEstadoMemorias are returned containing
    // descripcion='TipoEstadoMemoria031' to 'TipoEstadoMemoria040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoEstadoMemoria tipoEstadoMemoria = page.getContent().get(i);
      Assertions.assertThat(tipoEstadoMemoria.getNombre()).isEqualTo("TipoEstadoMemoria" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoEstadoMemoria
   * 
   * @param id     id del TipoEstadoMemoria
   * @param nombre nombre del TipoEstadoMemoria
   * @return el objeto TipoEstadoMemoria
   */

  public TipoEstadoMemoria generarMockTipoEstadoMemoria(Long id, String nombre) {

    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(id);
    tipoEstadoMemoria.setNombre(nombre);
    tipoEstadoMemoria.setActivo(Boolean.TRUE);

    return tipoEstadoMemoria;
  }
}