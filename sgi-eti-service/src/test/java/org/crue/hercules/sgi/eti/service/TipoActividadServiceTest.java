package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoActividadNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.repository.TipoActividadRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoActividadServiceImpl;
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
 * TipoActividadServiceTest
 */
public class TipoActividadServiceTest extends BaseServiceTest {

  @Mock
  private TipoActividadRepository tipoActividadRepository;

  private TipoActividadService tipoActividadService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoActividadService = new TipoActividadServiceImpl(tipoActividadRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoActividad() {
    BDDMockito.given(tipoActividadRepository.findById(1L))
        .willReturn(Optional.of(generarMockTipoActividad(1L, "TipoActividad1")));

    TipoActividad tipoActividad = tipoActividadService.findById(1L);

    Assertions.assertThat(tipoActividad.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoActividad.getNombre()).isEqualTo("TipoActividad1");

  }

  @Test
  public void find_NotFound_ThrowsTipoActividadNotFoundException() throws Exception {
    BDDMockito.given(tipoActividadRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoActividadService.findById(1L))
        .isInstanceOf(TipoActividadNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoActividad() {
    // given: Un nuevo TipoActividad
    TipoActividad tipoActividadNew = generarMockTipoActividad(1L, "TipoActividadNew");

    TipoActividad tipoActividad = generarMockTipoActividad(1L, "TipoActividadNew");

    BDDMockito.given(tipoActividadRepository.save(tipoActividadNew)).willReturn(tipoActividad);

    // when: Creamos el TipoActividad
    TipoActividad tipoActividadCreado = tipoActividadService.create(tipoActividadNew);

    // then: El TipoActividad se crea correctamente
    Assertions.assertThat(tipoActividadCreado).isNotNull();
    Assertions.assertThat(tipoActividadCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoActividadCreado.getNombre()).isEqualTo("TipoActividadNew");
  }

  @Test
  public void create_TipoActividadWithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo tipo de actividad que ya tiene id
    TipoActividad tipoActividadNew = generarMockTipoActividad(null, "TipoActividadNew");
    // when: Creamos el tipo de actividad
    // then: Lanza una excepcion porque el tipo actividad ya tiene id
    Assertions.assertThatThrownBy(() -> tipoActividadService.create(tipoActividadNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTipoActividad() {
    // given: Un nuevo tipo actividad con el servicio actualizado
    TipoActividad tipoActividadServicioActualizado = generarMockTipoActividad(1L, "TipoActividad1 actualizada");

    TipoActividad tipoActividad = generarMockTipoActividad(1L, "TipoActividad1");

    BDDMockito.given(tipoActividadRepository.findById(1L)).willReturn(Optional.of(tipoActividad));
    BDDMockito.given(tipoActividadRepository.save(tipoActividad)).willReturn(tipoActividadServicioActualizado);

    // when: Actualizamos el tipo actividad
    TipoActividad tipoActividadActualizado = tipoActividadService.update(tipoActividad);

    // then: El tipo actividad se actualiza correctamente.
    Assertions.assertThat(tipoActividadActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoActividadActualizado.getNombre()).isEqualTo("TipoActividad1 actualizada");

  }

  @Test
  public void update_ThrowsTipoActividadNotFoundException() {
    // given: Un nuevo tipo actividad a actualizar
    TipoActividad tipoActividad = generarMockTipoActividad(1L, "TipoActividad");

    // then: Lanza una excepcion porque el tipo actividad no existe
    Assertions.assertThatThrownBy(() -> tipoActividadService.update(tipoActividad))
        .isInstanceOf(TipoActividadNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un TipoActividad que venga sin id
    TipoActividad tipoActividad = generarMockTipoActividad(null, "TipoActividad");

    Assertions.assertThatThrownBy(
        // when: update TipoActividad
        () -> tipoActividadService.update(tipoActividad))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tipoActividadService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTipoActividadNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tipoActividadRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tipoActividadService.delete(1L))
        // then: Lanza TipoActividadNotFoundException
        .isInstanceOf(TipoActividadNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTipoActividad() {
    // given: Id existente
    BDDMockito.given(tipoActividadRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tipoActividadRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tipoActividadService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void deleteAll_DeleteAllTipoActividad() {
    // given: One hundred TipoActividad
    List<TipoActividad> tipoActividads = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoActividads.add(generarMockTipoActividad(Long.valueOf(i), "TipoActividad" + String.format("%03d", i)));
    }

    BDDMockito.doNothing().when(tipoActividadRepository).deleteAll();

    Assertions.assertThatCode(
        // when: Delete all
        () -> tipoActividadService.deleteAll())
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoActividadList() {
    // given: One hundred TipoActividad
    List<TipoActividad> tipoActividads = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoActividads.add(generarMockTipoActividad(Long.valueOf(i), "TipoActividad" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoActividadRepository.findAll(ArgumentMatchers.<Specification<TipoActividad>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoActividads));

    // when: find unlimited
    Page<TipoActividad> page = tipoActividadService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoActividads
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoActividads
    List<TipoActividad> tipoActividads = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoActividads.add(generarMockTipoActividad(Long.valueOf(i), "TipoActividad" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoActividadRepository.findAll(ArgumentMatchers.<Specification<TipoActividad>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoActividad>>() {
          @Override
          public Page<TipoActividad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoActividad> content = tipoActividads.subList(fromIndex, toIndex);
            Page<TipoActividad> page = new PageImpl<>(content, pageable, tipoActividads.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoActividad> page = tipoActividadService.findAll(null, paging);

    // then: A Page with ten TipoActividads are returned containing
    // descripcion='TipoActividad031' to 'TipoActividad040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoActividad tipoActividad = page.getContent().get(i);
      Assertions.assertThat(tipoActividad.getNombre()).isEqualTo("TipoActividad" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoActividad
   * 
   * @param id     id del tipoActividad
   * @param nombre nombre del tipo de actividad
   * @return el objeto tipo actividad
   */

  public TipoActividad generarMockTipoActividad(Long id, String nombre) {

    TipoActividad tipoActividad = new TipoActividad();
    tipoActividad.setId(id);
    tipoActividad.setNombre(nombre);
    tipoActividad.setActivo(Boolean.TRUE);

    return tipoActividad;
  }
}