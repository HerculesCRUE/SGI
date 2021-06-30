package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.DictamenNotFoundException;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.DictamenRepository;
import org.crue.hercules.sgi.eti.service.impl.DictamenServiceImpl;
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
 * DictamenServiceTest
 */
public class DictamenServiceTest extends BaseServiceTest {

  @Mock
  private DictamenRepository dictamenRepository;

  private DictamenService dictamenService;

  @BeforeEach
  public void setUp() throws Exception {
    dictamenService = new DictamenServiceImpl(dictamenRepository);
  }

  @Test
  public void find_WithId_ReturnsDictamen() {
    BDDMockito.given(dictamenRepository.findById(1L)).willReturn(Optional.of(generarMockDictamen(1L, "Dictamen1")));

    Dictamen dictamen = dictamenService.findById(1L);

    Assertions.assertThat(dictamen.getId()).isEqualTo(1L);
    Assertions.assertThat(dictamen.getNombre()).isEqualTo("Dictamen1");
    Assertions.assertThat(dictamen.getTipoEvaluacion().getNombre()).isEqualTo("TipoEvaluacion1");

  }

  @Test
  public void find_NotFound_ThrowsDictamenNotFoundException() throws Exception {
    BDDMockito.given(dictamenRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> dictamenService.findById(1L)).isInstanceOf(DictamenNotFoundException.class);
  }

  @Test
  public void create_ReturnsDictamen() {
    // given: Un nuevo dictamen
    Dictamen dictamenNew = generarMockDictamen(1L, "DictamenNew");

    Dictamen dictamen = generarMockDictamen(1L, "DictamenNew");

    BDDMockito.given(dictamenRepository.save(dictamenNew)).willReturn(dictamen);

    // when: Creamos el dictamen
    Dictamen dictamenCreado = dictamenService.create(dictamenNew);

    // then: El dictamen se crea correctamente
    Assertions.assertThat(dictamenCreado).isNotNull();
    Assertions.assertThat(dictamenCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(dictamenCreado.getNombre()).isEqualTo("DictamenNew");
  }

  @Test
  public void create_DictamenWithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo dictamen que ya tiene id
    Dictamen dictamenNew = generarMockDictamen(null, "DictamenNew");
    // when: Creamos el dictamen
    // then: Lanza una excepcion porque el dictamen ya tiene id
    Assertions.assertThatThrownBy(() -> dictamenService.create(dictamenNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsDictamen() {
    // given: Un nuevo dictamenNew con el nombre actualizado
    Dictamen dictamenNombreActualizado = generarMockDictamen(1L, "Dictamen1 actualizado");

    Dictamen dictamen = generarMockDictamen(1L, "Dictamen1");

    BDDMockito.given(dictamenRepository.findById(1L)).willReturn(Optional.of(dictamen));
    BDDMockito.given(dictamenRepository.save(dictamen)).willReturn(dictamenNombreActualizado);

    // when: Actualizamos el dictamen
    Dictamen dictamenActualizado = dictamenService.update(dictamen);

    // then: El dictamen se actualiza correctamente.
    Assertions.assertThat(dictamenActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(dictamenActualizado.getNombre()).isEqualTo("Dictamen1 actualizado");

  }

  @Test
  public void update_ThrowsDictamenNotFoundException() {
    // given: Un nuevo dictamen a actualizar
    Dictamen dictamen = generarMockDictamen(1L, "Dictamen");

    // then: Lanza una excepcion porque el dictamen no existe
    Assertions.assertThatThrownBy(() -> dictamenService.update(dictamen)).isInstanceOf(DictamenNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {
    // given: Un dictamen que venga sin id
    Dictamen dictamen = generarMockDictamen(null, "Dictamen");

    Assertions.assertThatThrownBy(
        // when: update dictamen
        () -> dictamenService.update(dictamen))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> dictamenService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsDictamenNotFoundException() {
    // given: Id no existe
    BDDMockito.given(dictamenRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> dictamenService.delete(1L))
        // then: Lanza DictamenNotFoundException
        .isInstanceOf(DictamenNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesDictamen() {
    // given: Id existente
    BDDMockito.given(dictamenRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(dictamenRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> dictamenService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void deleteAll_DeleteAllDictamen() {
    // given: One hundred dictamenes
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    BDDMockito.doNothing().when(dictamenRepository).deleteAll();

    Assertions.assertThatCode(
        // when: Delete all
        () -> dictamenService.deleteAll())
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullDictamenList() {
    // given: One hundred dictamenes
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    BDDMockito.given(
        dictamenRepository.findAll(ArgumentMatchers.<Specification<Dictamen>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(dictamenes));

    // when: find unlimited
    Page<Dictamen> page = dictamenService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Dictamenes
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred dictamenes
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    BDDMockito.given(
        dictamenRepository.findAll(ArgumentMatchers.<Specification<Dictamen>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Dictamen>>() {
          @Override
          public Page<Dictamen> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Dictamen> content = dictamenes.subList(fromIndex, toIndex);
            Page<Dictamen> page = new PageImpl<>(content, pageable, dictamenes.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Dictamen> page = dictamenService.findAll(null, paging);

    // then: A Page with ten dictamenes are returned containing
    // nombre='Dictamen031' to 'Dictamen040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Dictamen dictamen = page.getContent().get(i);
      Assertions.assertThat(dictamen.getNombre()).isEqualTo("Dictamen" + String.format("%03d", j));
    }
  }

  @Test
  public void findAllByMemoriaRevisionMinima_ReturnsDictamenList() {
    // given: One hundred dictamenes
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 2; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    List<Long> ids = new ArrayList<Long>(Arrays.asList(1L, 2L));

    BDDMockito.given(dictamenRepository.findByIdIn(ids)).willReturn(dictamenes);

    // when: find unlimited
    List<Dictamen> result = dictamenService.findAllByMemoriaRevisionMinima();

    // then: Get 2 Dictamenes
    Assertions.assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void findAllByMemoriaNoRevisionMinima_ReturnsDictamenList() {
    // given: One hundred dictamenes
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    BDDMockito.given(dictamenRepository.findByTipoEvaluacionId(2L)).willReturn(dictamenes);

    // when: find unlimited
    List<Dictamen> result = dictamenService.findAllByMemoriaNoRevisionMinima();

    // then: Get 10 Dictamenes
    Assertions.assertThat(result.size()).isEqualTo(10);
  }

  @Test
  public void findAllByRetrospectiva_ReturnsDictamenList() {
    // given: One hundred dictamenes
    List<Dictamen> dictamenes = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      dictamenes.add(generarMockDictamen(Long.valueOf(i), "Dictamen" + String.format("%03d", i)));
    }

    BDDMockito.given(dictamenRepository.findByTipoEvaluacionId(1L)).willReturn(dictamenes);

    // when: find unlimited
    List<Dictamen> result = dictamenService.findAllByRetrospectiva();

    // then: Get 10 Dictamenes
    Assertions.assertThat(result.size()).isEqualTo(10);
  }

  /**
   * Función que devuelve un objeto Dictamen
   * 
   * @param id     id del Dictamen
   * @param nombre nombre del Dictamen
   * @return el objeto Dictamen
   */
  public Dictamen generarMockDictamen(Long id, String nombre) {

    TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
    tipoEvaluacion.setId(1L);
    tipoEvaluacion.setNombre("TipoEvaluacion1");
    tipoEvaluacion.setActivo(Boolean.TRUE);

    Dictamen dictamen = new Dictamen();
    dictamen.setId(id);
    dictamen.setNombre(nombre);
    dictamen.setTipoEvaluacion(tipoEvaluacion);
    dictamen.setActivo(Boolean.TRUE);

    return dictamen;
  }

}