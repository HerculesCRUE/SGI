package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoComentarioNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.repository.TipoComentarioRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoComentarioServiceImpl;
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
 * TipoComentarioServiceTest
 */
public class TipoComentarioServiceTest extends BaseServiceTest {

  @Mock
  private TipoComentarioRepository tipoComentarioRepository;

  private TipoComentarioService tipoComentarioService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoComentarioService = new TipoComentarioServiceImpl(tipoComentarioRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoComentario() {
    BDDMockito.given(tipoComentarioRepository.findById(1L))
        .willReturn(Optional.of(generarMockTipoComentario(1L, "TipoComentario1")));

    TipoComentario tipoComentario = tipoComentarioService.findById(1L);

    Assertions.assertThat(tipoComentario.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoComentario.getNombre()).isEqualTo("TipoComentario1");

  }

  @Test
  public void find_NotFound_ThrowsTipoComentarioNotFoundException() throws Exception {
    BDDMockito.given(tipoComentarioRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoComentarioService.findById(1L))
        .isInstanceOf(TipoComentarioNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoComentario() {
    // given: Un nuevo TipoComentario
    TipoComentario tipoComentarioNew = generarMockTipoComentario(1L, "TipoComentarioNew");

    TipoComentario tipoComentario = generarMockTipoComentario(1L, "TipoComentarioNew");

    BDDMockito.given(tipoComentarioRepository.save(tipoComentarioNew)).willReturn(tipoComentario);

    // when: Creamos el TipoComentario
    TipoComentario tipoComentarioCreado = tipoComentarioService.create(tipoComentarioNew);

    // then: El TipoComentario se crea correctamente
    Assertions.assertThat(tipoComentarioCreado).isNotNull();
    Assertions.assertThat(tipoComentarioCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoComentarioCreado.getNombre()).isEqualTo("TipoComentarioNew");
  }

  @Test
  public void create_TipoComentarioWithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo tipo de comentario que ya tiene id
    TipoComentario tipoComentarioNew = generarMockTipoComentario(null, "TipoComentarioNew");
    // when: Creamos el tipo de comentario
    // then: Lanza una excepcion porque el TipoComentario ya tiene id
    Assertions.assertThatThrownBy(() -> tipoComentarioService.create(tipoComentarioNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTipoComentario() {
    // given: Un nuevo TipoComentario con el servicio actualizado
    TipoComentario tipoComentarioServicioActualizado = generarMockTipoComentario(1L, "TipoComentario1 actualizada");

    TipoComentario tipoComentario = generarMockTipoComentario(1L, "TipoComentario1");

    BDDMockito.given(tipoComentarioRepository.findById(1L)).willReturn(Optional.of(tipoComentario));
    BDDMockito.given(tipoComentarioRepository.save(tipoComentario)).willReturn(tipoComentarioServicioActualizado);

    // when: Actualizamos el TipoComentario
    TipoComentario tipoComentarioActualizado = tipoComentarioService.update(tipoComentario);

    // then: El TipoComentario se actualiza correctamente.
    Assertions.assertThat(tipoComentarioActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoComentarioActualizado.getNombre()).isEqualTo("TipoComentario1 actualizada");

  }

  @Test
  public void update_ThrowsTipoComentarioNotFoundException() {
    // given: Un nuevo TipoComentario a actualizar
    TipoComentario tipoComentario = generarMockTipoComentario(1L, "TipoComentario");

    // then: Lanza una excepcion porque el TipoComentario no existe
    Assertions.assertThatThrownBy(() -> tipoComentarioService.update(tipoComentario))
        .isInstanceOf(TipoComentarioNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un TipoComentario que venga sin id
    TipoComentario tipoComentario = generarMockTipoComentario(null, "TipoComentario");

    Assertions.assertThatThrownBy(
        // when: update TipoComentario
        () -> tipoComentarioService.update(tipoComentario))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tipoComentarioService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTipoComentarioNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tipoComentarioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tipoComentarioService.delete(1L))
        // then: Lanza TipoComentarioNotFoundException
        .isInstanceOf(TipoComentarioNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTipoComentario() {
    // given: Id existente
    BDDMockito.given(tipoComentarioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tipoComentarioRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tipoComentarioService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void deleteAll_DeleteAllTipoComentario() {
    // given: One hundred TipoComentario
    List<TipoComentario> tipoComentarios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoComentarios.add(generarMockTipoComentario(Long.valueOf(i), "TipoComentario" + String.format("%03d", i)));
    }
    BDDMockito.doNothing().when(tipoComentarioRepository).deleteAll();

    Assertions.assertThatCode(
        // when: Delete all
        () -> tipoComentarioService.deleteAll())
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoComentarioList() {
    // given: One hundred TipoComentario
    List<TipoComentario> tipoComentarios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoComentarios.add(generarMockTipoComentario(Long.valueOf(i), "TipoComentario" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoComentarioRepository.findAll(ArgumentMatchers.<Specification<TipoComentario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoComentarios));

    // when: find unlimited
    Page<TipoComentario> page = tipoComentarioService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoComentarios
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoComentarios
    List<TipoComentario> tipoComentarios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoComentarios.add(generarMockTipoComentario(Long.valueOf(i), "TipoComentario" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoComentarioRepository.findAll(ArgumentMatchers.<Specification<TipoComentario>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoComentario>>() {
          @Override
          public Page<TipoComentario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoComentario> content = tipoComentarios.subList(fromIndex, toIndex);
            Page<TipoComentario> page = new PageImpl<>(content, pageable, tipoComentarios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoComentario> page = tipoComentarioService.findAll(null, paging);

    // then: A Page with ten TipoComentarios are returned containing
    // descripcion='TipoComentario031' to 'TipoComentario040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoComentario tipoComentario = page.getContent().get(i);
      Assertions.assertThat(tipoComentario.getNombre()).isEqualTo("TipoComentario" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoComentario
   * 
   * @param id     id del TipoComentario
   * @param nombre nombre del TipoComentario
   * @return el objeto TipoComentario
   */

  public TipoComentario generarMockTipoComentario(Long id, String nombre) {

    TipoComentario tipoComentario = new TipoComentario();
    tipoComentario.setId(id);
    tipoComentario.setNombre(nombre);
    tipoComentario.setActivo(Boolean.TRUE);

    return tipoComentario;
  }
}