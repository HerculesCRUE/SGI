package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoTareaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.repository.TipoTareaRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoTareaServiceImpl;
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
 * TipoTareaServiceTest
 */
public class TipoTareaServiceTest extends BaseServiceTest {

  @Mock
  private TipoTareaRepository tipoTareaRepository;

  private TipoTareaService tipoTareaService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoTareaService = new TipoTareaServiceImpl(tipoTareaRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoTarea() {
    BDDMockito.given(tipoTareaRepository.findById(1L)).willReturn(Optional.of(generarMockTipoTarea(1L, "TipoTarea1")));

    TipoTarea tipoTarea = tipoTareaService.findById(1L);

    Assertions.assertThat(tipoTarea.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoTarea.getNombre()).isEqualTo("TipoTarea1");

  }

  @Test
  public void create_ReturnsTipoTarea() {
    // given: Un nuevo TipoTarea
    TipoTarea tipoTareaNew = generarMockTipoTarea(1L, "TipoTareaNew");

    TipoTarea tipoTarea = generarMockTipoTarea(1L, "TipoTareaNew");

    BDDMockito.given(tipoTareaRepository.save(tipoTareaNew)).willReturn(tipoTarea);

    // when: Creamos el tipo Tarea
    TipoTarea tipoTareaCreado = tipoTareaService.create(tipoTareaNew);

    // then: El tipo Tarea se crea correctamente
    Assertions.assertThat(tipoTareaCreado).isNotNull();
    Assertions.assertThat(tipoTareaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoTareaCreado.getNombre()).isEqualTo("TipoTareaNew");
  }

  @Test
  public void update_ReturnsTipoTarea() {
    // given: Un nuevo tipo Tarea con el servicio actualizado
    TipoTarea tipoTareaServicioActualizado = generarMockTipoTarea(1L, "TipoTarea1 actualizada");

    TipoTarea tipoTarea = generarMockTipoTarea(1L, "TipoTarea1");

    BDDMockito.given(tipoTareaRepository.findById(1L)).willReturn(Optional.of(tipoTarea));
    BDDMockito.given(tipoTareaRepository.save(tipoTarea)).willReturn(tipoTareaServicioActualizado);

    // when: Actualizamos el tipo Tarea
    TipoTarea tipoTareaActualizado = tipoTareaService.update(tipoTarea);

    // then: El tipo Tarea se actualiza correctamente.
    Assertions.assertThat(tipoTareaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoTareaActualizado.getNombre()).isEqualTo("TipoTarea1 actualizada");

  }

  @Test
  public void update_ThrowsTipoTareaNotFoundException() {
    // given: Un nuevo tipo Tarea a actualizar
    TipoTarea tipoTarea = generarMockTipoTarea(1L, "TipoTarea");

    // then: Lanza una excepcion porque el tipo Tarea no existe
    Assertions.assertThatThrownBy(() -> tipoTareaService.update(tipoTarea))
        .isInstanceOf(TipoTareaNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un TipoTarea que venga sin id
    TipoTarea tipoTarea = generarMockTipoTarea(null, "TipoTarea");

    Assertions.assertThatThrownBy(
        // when: update TipoTarea
        () -> tipoTareaService.update(tipoTarea))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tipoTareaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTipoTareaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tipoTareaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tipoTareaService.delete(1L))
        // then: Lanza TipoTareaNotFoundException
        .isInstanceOf(TipoTareaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTipoTarea() {
    // given: Id existente
    BDDMockito.given(tipoTareaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tipoTareaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tipoTareaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoTareaList() {
    // given: One hundred TipoTarea
    List<TipoTarea> tipoTareas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoTareas.add(generarMockTipoTarea(Long.valueOf(i), "TipoTarea" + String.format("%03d", i)));
    }

    BDDMockito.given(
        tipoTareaRepository.findAll(ArgumentMatchers.<Specification<TipoTarea>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoTareas));

    // when: find unlimited
    Page<TipoTarea> page = tipoTareaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoTareas
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoTareas
    List<TipoTarea> tipoTareas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoTareas.add(generarMockTipoTarea(Long.valueOf(i), "TipoTarea" + String.format("%03d", i)));
    }

    BDDMockito.given(
        tipoTareaRepository.findAll(ArgumentMatchers.<Specification<TipoTarea>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoTarea>>() {
          @Override
          public Page<TipoTarea> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoTarea> content = tipoTareas.subList(fromIndex, toIndex);
            Page<TipoTarea> page = new PageImpl<>(content, pageable, tipoTareas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoTarea> page = tipoTareaService.findAll(null, paging);

    // then: A Page with ten TipoTareas are returned containing
    // descripcion='TipoTarea031' to 'TipoTarea040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoTarea tipoTarea = page.getContent().get(i);
      Assertions.assertThat(tipoTarea.getNombre()).isEqualTo("TipoTarea" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoTarea
   * 
   * @param id     id del tipoTarea
   * @param nombre nombre del tipo de Tarea
   * @return el objeto tipo Tarea
   */

  public TipoTarea generarMockTipoTarea(Long id, String nombre) {

    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(id);
    tipoTarea.setNombre(nombre);
    tipoTarea.setActivo(Boolean.TRUE);

    return tipoTarea;
  }
}