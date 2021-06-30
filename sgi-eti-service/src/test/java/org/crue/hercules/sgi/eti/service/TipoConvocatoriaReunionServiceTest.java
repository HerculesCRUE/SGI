package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.repository.TipoConvocatoriaReunionRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoConvocatoriaReunionServiceImpl;
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
 * TipoConvocatoriaReunionServiceTest
 */
public class TipoConvocatoriaReunionServiceTest extends BaseServiceTest {

  @Mock
  private TipoConvocatoriaReunionRepository tipoConvocatoriaReunionRepository;

  private TipoConvocatoriaReunionService tipoConvocatoriaReunionService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoConvocatoriaReunionService = new TipoConvocatoriaReunionServiceImpl(tipoConvocatoriaReunionRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoConvocatoriaReunion() {
    BDDMockito.given(tipoConvocatoriaReunionRepository.findById(1L))
        .willReturn(Optional.of(generarMockTipoConvocatoriaReunion(1L, "TipoConvocatoriaReunion1")));

    TipoConvocatoriaReunion tipoConvocatoriaReunion = tipoConvocatoriaReunionService.findById(1L);

    Assertions.assertThat(tipoConvocatoriaReunion.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoConvocatoriaReunion.getNombre()).isEqualTo("TipoConvocatoriaReunion1");

  }

  @Test
  public void find_NotFound_ThrowsTipoConvocatoriaReunionNotFoundException() throws Exception {
    BDDMockito.given(tipoConvocatoriaReunionRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoConvocatoriaReunionService.findById(1L))
        .isInstanceOf(TipoConvocatoriaReunionNotFoundException.class);
  }

  @Test
  public void create_ReturnsTipoConvocatoriaReunion() {
    // given: Un nuevo TipoConvocatoriaReunion
    TipoConvocatoriaReunion tipoConvocatoriaReunionNew = generarMockTipoConvocatoriaReunion(1L,
        "TipoConvocatoriaReunionNew");

    TipoConvocatoriaReunion tipoConvocatoriaReunion = generarMockTipoConvocatoriaReunion(1L,
        "TipoConvocatoriaReunionNew");

    BDDMockito.given(tipoConvocatoriaReunionRepository.save(tipoConvocatoriaReunionNew))
        .willReturn(tipoConvocatoriaReunion);

    // when: Creamos el TipoConvocatoriaReunion
    TipoConvocatoriaReunion tipoConvocatoriaReunionCreado = tipoConvocatoriaReunionService
        .create(tipoConvocatoriaReunionNew);

    // then: El TipoConvocatoriaReunion se crea correctamente
    Assertions.assertThat(tipoConvocatoriaReunionCreado).isNotNull();
    Assertions.assertThat(tipoConvocatoriaReunionCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoConvocatoriaReunionCreado.getNombre()).isEqualTo("TipoConvocatoriaReunionNew");
  }

  @Test
  public void create_TipoConvocatoriaReunionWithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo tipo de convocatoria de reunión que no tiene id
    TipoConvocatoriaReunion tipoConvocatoriaReunionNew = generarMockTipoConvocatoriaReunion(null,
        "TipoConvocatoriaReunionNew");
    // when: Creamos el tipo de convocatoria de reunión
    // then: Lanza una excepcion porque el TipoConvocatoriaReunion no tiene id
    Assertions.assertThatThrownBy(() -> tipoConvocatoriaReunionService.create(tipoConvocatoriaReunionNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsTipoConvocatoriaReunion() {
    // given: Un nuevo TipoConvocatoriaReunion con el servicio actualizado
    TipoConvocatoriaReunion tipoConvocatoriaReunionServicioActualizado = generarMockTipoConvocatoriaReunion(1L,
        "TipoConvocatoriaReunion1 actualizada");

    TipoConvocatoriaReunion tipoConvocatoriaReunion = generarMockTipoConvocatoriaReunion(1L,
        "TipoConvocatoriaReunion1");

    BDDMockito.given(tipoConvocatoriaReunionRepository.findById(1L)).willReturn(Optional.of(tipoConvocatoriaReunion));
    BDDMockito.given(tipoConvocatoriaReunionRepository.save(tipoConvocatoriaReunion))
        .willReturn(tipoConvocatoriaReunionServicioActualizado);

    // when: Actualizamos el TipoConvocatoriaReunion
    TipoConvocatoriaReunion tipoConvocatoriaReunionActualizado = tipoConvocatoriaReunionService
        .update(tipoConvocatoriaReunion);

    // then: El TipoConvocatoriaReunion se actualiza correctamente.
    Assertions.assertThat(tipoConvocatoriaReunionActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(tipoConvocatoriaReunionActualizado.getNombre())
        .isEqualTo("TipoConvocatoriaReunion1 actualizada");

  }

  @Test
  public void update_ThrowsTipoConvocatoriaReunionNotFoundException() {
    // given: Un nuevo TipoConvocatoriaReunion a actualizar
    TipoConvocatoriaReunion tipoConvocatoriaReunion = generarMockTipoConvocatoriaReunion(1L, "TipoConvocatoriaReunion");

    // then: Lanza una excepcion porque el TipoConvocatoriaReunion no existe
    Assertions.assertThatThrownBy(() -> tipoConvocatoriaReunionService.update(tipoConvocatoriaReunion))
        .isInstanceOf(TipoConvocatoriaReunionNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un TipoConvocatoriaReunion que venga sin id
    TipoConvocatoriaReunion tipoConvocatoriaReunion = generarMockTipoConvocatoriaReunion(null,
        "TipoConvocatoriaReunion");

    Assertions.assertThatThrownBy(
        // when: update TipoConvocatoriaReunion
        () -> tipoConvocatoriaReunionService.update(tipoConvocatoriaReunion))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> tipoConvocatoriaReunionService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsTipoConvocatoriaReunionNotFoundException() {
    // given: Id no existe
    BDDMockito.given(tipoConvocatoriaReunionRepository.existsById(ArgumentMatchers.anyLong()))
        .willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> tipoConvocatoriaReunionService.delete(1L))
        // then: Lanza TipoConvocatoriaReunionNotFoundException
        .isInstanceOf(TipoConvocatoriaReunionNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesTipoConvocatoriaReunion() {
    // given: Id existente
    BDDMockito.given(tipoConvocatoriaReunionRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(tipoConvocatoriaReunionRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> tipoConvocatoriaReunionService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void deleteAll_DeleteAllTipoConvocatoriaReunion() {
    // given: One hundred TipoConvocatoriaReunion
    List<TipoConvocatoriaReunion> tipoConvocatoriaReunions = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoConvocatoriaReunions.add(
          generarMockTipoConvocatoriaReunion(Long.valueOf(i), "TipoConvocatoriaReunion" + String.format("%03d", i)));
    }

    BDDMockito.doNothing().when(tipoConvocatoriaReunionRepository).deleteAll();

    Assertions.assertThatCode(
        // when: Delete all
        () -> tipoConvocatoriaReunionService.deleteAll())
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoConvocatoriaReunionList() {
    // given: One hundred TipoConvocatoriaReunion
    List<TipoConvocatoriaReunion> tipoConvocatoriaReunions = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoConvocatoriaReunions.add(
          generarMockTipoConvocatoriaReunion(Long.valueOf(i), "TipoConvocatoriaReunion" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoConvocatoriaReunionRepository
        .findAll(ArgumentMatchers.<Specification<TipoConvocatoriaReunion>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoConvocatoriaReunions));

    // when: find unlimited
    Page<TipoConvocatoriaReunion> page = tipoConvocatoriaReunionService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoConvocatoriaReunions
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoConvocatoriaReunions
    List<TipoConvocatoriaReunion> tipoConvocatoriaReunions = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoConvocatoriaReunions.add(
          generarMockTipoConvocatoriaReunion(Long.valueOf(i), "TipoConvocatoriaReunion" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoConvocatoriaReunionRepository
        .findAll(ArgumentMatchers.<Specification<TipoConvocatoriaReunion>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoConvocatoriaReunion>>() {
          @Override
          public Page<TipoConvocatoriaReunion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoConvocatoriaReunion> content = tipoConvocatoriaReunions.subList(fromIndex, toIndex);
            Page<TipoConvocatoriaReunion> page = new PageImpl<>(content, pageable, tipoConvocatoriaReunions.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoConvocatoriaReunion> page = tipoConvocatoriaReunionService.findAll(null, paging);

    // then: A Page with ten TipoConvocatoriaReunions are returned containing
    // nombre='TipoConvocatoriaReunion031' to 'TipoConvocatoriaReunion040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoConvocatoriaReunion tipoConvocatoriaReunion = page.getContent().get(i);
      Assertions.assertThat(tipoConvocatoriaReunion.getNombre())
          .isEqualTo("TipoConvocatoriaReunion" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoConvocatoriaReunion
   * 
   * @param id     id del TipoConvocatoriaReunion
   * @param nombre nombre del TipoConvocatoriaReunion
   * @return el objeto TipoConvocatoriaReunion
   */

  public TipoConvocatoriaReunion generarMockTipoConvocatoriaReunion(Long id, String nombre) {

    TipoConvocatoriaReunion tipoConvocatoriaReunion = new TipoConvocatoriaReunion();
    tipoConvocatoriaReunion.setId(id);
    tipoConvocatoriaReunion.setNombre(nombre);
    tipoConvocatoriaReunion.setActivo(Boolean.TRUE);

    return tipoConvocatoriaReunion;
  }
}