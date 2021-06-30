package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.TipoMemoriaRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoMemoriaServiceImpl;
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
 * TipoMemoriaServiceTest
 */
public class TipoMemoriaServiceTest extends BaseServiceTest {

  @Mock
  private TipoMemoriaRepository tipoMemoriaRepository;

  private TipoMemoriaService tipoMemoriaService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoMemoriaService = new TipoMemoriaServiceImpl(tipoMemoriaRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoMemoria() {
    BDDMockito.given(tipoMemoriaRepository.findById(1L))
        .willReturn(Optional.of(generarMockTipoMemoria(1L, "TipoMemoria1")));

    TipoMemoria tipoMemoria = tipoMemoriaService.findById(1L);

    Assertions.assertThat(tipoMemoria.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoMemoria.getNombre()).isEqualTo("TipoMemoria1");

  }

  @Test
  public void find_NotFound_ThrowsTipoMemoriaNotFoundException() throws Exception {
    BDDMockito.given(tipoMemoriaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoMemoriaService.findById(1L))
        .isInstanceOf(TipoMemoriaNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoMemoria() {
    // given: Un nuevo TipoMemoria
    TipoMemoria tipoMemoriaNew = generarMockTipoMemoria(1L, "TipoMemoriaNew");

    TipoMemoria tipoMemoria = generarMockTipoMemoria(1L, "TipoMemoriaNew");

    BDDMockito.given(tipoMemoriaRepository.save(tipoMemoriaNew)).willReturn(tipoMemoria);

    // when: Creamos el tipo memoria
    TipoMemoria tipoMemoriaCreado = tipoMemoriaService.create(tipoMemoriaNew);

    // then: El tipo memoria se crea correctamente
    Assertions.assertThat(tipoMemoriaCreado).isNotNull();
    Assertions.assertThat(tipoMemoriaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoMemoriaCreado.getNombre()).isEqualTo("TipoMemoriaNew");
  }

  @Test
  public void create_TipoMemoriaWithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo tipo de Memoria que ya tiene id
    TipoMemoria tipoMemoriaNew = generarMockTipoMemoria(null, "TipoMemoriaNew");
    // when: Creamos el tipo de Memoria
    // then: Lanza una excepcion porque el tipo Memoria ya tiene id
    Assertions.assertThatThrownBy(() -> tipoMemoriaService.create(tipoMemoriaNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTipoMemoria() {
    // given: Un nuevo tipo Memoria con el servicio actualizado
    TipoMemoria tipoMemoriaServicioActualizado = generarMockTipoMemoria(1L, "TipoMemoria1 actualizada");

    TipoMemoria tipoMemoria = generarMockTipoMemoria(1L, "TipoMemoria1");

    BDDMockito.given(tipoMemoriaRepository.findById(1L)).willReturn(Optional.of(tipoMemoria));
    BDDMockito.given(tipoMemoriaRepository.save(tipoMemoria)).willReturn(tipoMemoriaServicioActualizado);

    // when: Actualizamos el tipo Memoria
    TipoMemoria tipoMemoriaActualizado = tipoMemoriaService.update(tipoMemoria);

    // then: El tipo Memoria se actualiza correctamente.
    Assertions.assertThat(tipoMemoriaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoMemoriaActualizado.getNombre()).isEqualTo("TipoMemoria1 actualizada");

  }

  @Test
  public void update_ThrowsTipoMemoriaNotFoundException() {
    // given: Un nuevo tipo Memoria a actualizar
    TipoMemoria tipoMemoria = generarMockTipoMemoria(1L, "TipoMemoria");

    // then: Lanza una excepcion porque el tipo Memoria no existe
    Assertions.assertThatThrownBy(() -> tipoMemoriaService.update(tipoMemoria))
        .isInstanceOf(TipoMemoriaNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un TipoMemoria que venga sin id
    TipoMemoria tipoMemoria = generarMockTipoMemoria(null, "TipoMemoria");

    Assertions.assertThatThrownBy(
        // when: update TipoMemoria
        () -> tipoMemoriaService.update(tipoMemoria))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tipoMemoriaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTipoMemoriaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tipoMemoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tipoMemoriaService.delete(1L))
        // then: Lanza TipoMemoriaNotFoundException
        .isInstanceOf(TipoMemoriaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTipoMemoria() {
    // given: Id existente
    BDDMockito.given(tipoMemoriaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tipoMemoriaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tipoMemoriaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void deleteAll_DeleteAllTipoMemoria() {
    // given: One hundred TipoMemoria
    List<TipoMemoria> tipoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemorias.add(generarMockTipoMemoria(Long.valueOf(i), "TipoMemoria" + String.format("%03d", i)));
    }

    BDDMockito.doNothing().when(tipoMemoriaRepository).deleteAll();

    Assertions.assertThatCode(
        // when: Delete all
        () -> tipoMemoriaService.deleteAll())
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoMemoriaList() {
    // given: One hundred TipoMemoria
    List<TipoMemoria> tipoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemorias.add(generarMockTipoMemoria(Long.valueOf(i), "TipoMemoria" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoMemoriaRepository.findAll(ArgumentMatchers.<Specification<TipoMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoMemorias));

    // when: find unlimited
    Page<TipoMemoria> page = tipoMemoriaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoMemorias
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoMemorias
    List<TipoMemoria> tipoMemorias = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoMemorias.add(generarMockTipoMemoria(Long.valueOf(i), "TipoMemoria" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoMemoriaRepository.findAll(ArgumentMatchers.<Specification<TipoMemoria>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoMemoria>>() {
          @Override
          public Page<TipoMemoria> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoMemoria> content = tipoMemorias.subList(fromIndex, toIndex);
            Page<TipoMemoria> page = new PageImpl<>(content, pageable, tipoMemorias.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoMemoria> page = tipoMemoriaService.findAll(null, paging);

    // then: A Page with ten TipoMemorias are returned containing
    // descripcion='TipoMemoria031' to 'TipoMemoria040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoMemoria tipoMemoria = page.getContent().get(i);
      Assertions.assertThat(tipoMemoria.getNombre()).isEqualTo("TipoMemoria" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoMemoria
   * 
   * @param id     id del tipoMemoria
   * @param nombre nombre del tipo de Memoria
   * @return el objeto tipo Memoria
   */

  public TipoMemoria generarMockTipoMemoria(Long id, String nombre) {

    TipoMemoria tipoMemoria = new TipoMemoria();
    tipoMemoria.setId(id);
    tipoMemoria.setNombre(nombre);
    tipoMemoria.setActivo(Boolean.TRUE);

    return tipoMemoria;
  }
}