package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.FormacionEspecificaNotFoundException;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.repository.FormacionEspecificaRepository;
import org.crue.hercules.sgi.eti.service.impl.FormacionEspecificaServiceImpl;
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
 * FormacionEspecificaServiceTest
 */
public class FormacionEspecificaServiceTest extends BaseServiceTest {

  @Mock
  private FormacionEspecificaRepository formacionEspecificaRepository;

  private FormacionEspecificaService formacionEspecificaService;

  @BeforeEach
  public void setUp() throws Exception {
    formacionEspecificaService = new FormacionEspecificaServiceImpl(formacionEspecificaRepository);
  }

  @Test
  public void find_WithId_ReturnsFormacionEspecifica() {
    BDDMockito.given(formacionEspecificaRepository.findById(1L))
        .willReturn(Optional.of(generarMockFormacionEspecifica(1L, "FormacionEspecifica1")));

    FormacionEspecifica formacionEspecifica = formacionEspecificaService.findById(1L);

    Assertions.assertThat(formacionEspecifica.getId()).isEqualTo(1L);

    Assertions.assertThat(formacionEspecifica.getNombre()).isEqualTo("FormacionEspecifica1");

  }

  @Test
  public void find_NotFound_ThrowsFormacionEspecificaNotFoundException() throws Exception {
    BDDMockito.given(formacionEspecificaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> formacionEspecificaService.findById(1L))
        .isInstanceOf(FormacionEspecificaNotFoundException.class);
  }

  @Test
  public void create_ReturnsFormacionEspecifica() {
    // given: Un nuevo FormacionEspecifica
    FormacionEspecifica formacionEspecificaNew = generarMockFormacionEspecifica(1L, "FormacionEspecificaNew");

    FormacionEspecifica formacionEspecifica = generarMockFormacionEspecifica(1L, "FormacionEspecificaNew");

    BDDMockito.given(formacionEspecificaRepository.save(formacionEspecificaNew)).willReturn(formacionEspecifica);

    // when: Creamos la formación específica
    FormacionEspecifica formacionEspecificaCreado = formacionEspecificaService.create(formacionEspecificaNew);

    // then: La formación específica se crea correctamente
    Assertions.assertThat(formacionEspecificaCreado).isNotNull();
    Assertions.assertThat(formacionEspecificaCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(formacionEspecificaCreado.getNombre()).isEqualTo("FormacionEspecificaNew");
  }

  @Test
  public void create_FormacionEspecificaWithoutId_ThrowsIllegalArgumentException() {
    // given: Una nueva formación específica que no tiene id
    FormacionEspecifica formacionEspecificaNew = generarMockFormacionEspecifica(null, "FormacionEspecificaNew");

    // when: Creamos la formación específica
    // then: Lanza una excepcion porque la formación específica no tiene id
    Assertions.assertThatThrownBy(() -> formacionEspecificaService.create(formacionEspecificaNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsFormacionEspecifica() {
    // given: Una nueva formación específica con el servicio actualizado
    FormacionEspecifica formacionEspecificaServicioActualizado = generarMockFormacionEspecifica(1L,
        "FormacionEspecifica1 actualizada");

    FormacionEspecifica formacionEspecifica = generarMockFormacionEspecifica(1L, "FormacionEspecifica1");

    BDDMockito.given(formacionEspecificaRepository.findById(1L)).willReturn(Optional.of(formacionEspecifica));
    BDDMockito.given(formacionEspecificaRepository.save(formacionEspecifica))
        .willReturn(formacionEspecificaServicioActualizado);

    // when: Actualizamos la formación específica
    FormacionEspecifica formacionEspecificaActualizado = formacionEspecificaService.update(formacionEspecifica);

    // then: La formación específica se actualiza correctamente.
    Assertions.assertThat(formacionEspecificaActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(formacionEspecificaActualizado.getNombre()).isEqualTo("FormacionEspecifica1 actualizada");

  }

  @Test
  public void update_ThrowsFormacionEspecificaNotFoundException() {
    // given: Una nueva formación específica a actualizar
    FormacionEspecifica formacionEspecifica = generarMockFormacionEspecifica(1L, "FormacionEspecifica");

    // then: Lanza una excepcion porque la formación específica no existe
    Assertions.assertThatThrownBy(() -> formacionEspecificaService.update(formacionEspecifica))
        .isInstanceOf(FormacionEspecificaNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Una FormacionEspecifica que venga sin id
    FormacionEspecifica formacionEspecifica = generarMockFormacionEspecifica(null, "FormacionEspecifica");

    Assertions.assertThatThrownBy(
        // when: update FormacionEspecifica
        () -> formacionEspecificaService.update(formacionEspecifica))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> formacionEspecificaService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsFormacionEspecificaNotFoundException() {
    // given: Id no existe
    BDDMockito.given(formacionEspecificaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> formacionEspecificaService.delete(1L))
        // then: Lanza FormacionEspecificaNotFoundException
        .isInstanceOf(FormacionEspecificaNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesFormacionEspecifica() {
    // given: Id existente
    BDDMockito.given(formacionEspecificaRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(formacionEspecificaRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> formacionEspecificaService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void deleteAll_DeleteAllFormacionEspecifica() {
    // given: One hundred FormacionEspecifica
    List<FormacionEspecifica> formacionEspecificas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formacionEspecificas
          .add(generarMockFormacionEspecifica(Long.valueOf(i), "FormacionEspecifica" + String.format("%03d", i)));
    }
    BDDMockito.doNothing().when(formacionEspecificaRepository).deleteAll();

    Assertions.assertThatCode(
        // when: Delete all
        () -> formacionEspecificaService.deleteAll())
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullFormacionEspecificaList() {
    // given: One hundred FormacionEspecifica
    List<FormacionEspecifica> formacionEspecificas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formacionEspecificas
          .add(generarMockFormacionEspecifica(Long.valueOf(i), "FormacionEspecifica" + String.format("%03d", i)));
    }

    BDDMockito.given(formacionEspecificaRepository.findAll(ArgumentMatchers.<Specification<FormacionEspecifica>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(formacionEspecificas));

    // when: find unlimited
    Page<FormacionEspecifica> page = formacionEspecificaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred FormacionEspecificas
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred FormacionEspecificas
    List<FormacionEspecifica> formacionEspecificas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formacionEspecificas
          .add(generarMockFormacionEspecifica(Long.valueOf(i), "FormacionEspecifica" + String.format("%03d", i)));
    }

    BDDMockito.given(formacionEspecificaRepository.findAll(ArgumentMatchers.<Specification<FormacionEspecifica>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<FormacionEspecifica>>() {
          @Override
          public Page<FormacionEspecifica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<FormacionEspecifica> content = formacionEspecificas.subList(fromIndex, toIndex);
            Page<FormacionEspecifica> page = new PageImpl<>(content, pageable, formacionEspecificas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<FormacionEspecifica> page = formacionEspecificaService.findAll(null, paging);

    // then: A Page with ten FormacionEspecificas are returned containing
    // descripcion='FormacionEspecifica031' to 'FormacionEspecifica040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      FormacionEspecifica formacionEspecifica = page.getContent().get(i);
      Assertions.assertThat(formacionEspecifica.getNombre())
          .isEqualTo("FormacionEspecifica" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto FormacionEspecifica
   * 
   * @param id     id del formacionEspecifica
   * @param nombre nombre de la formación específica
   * @return el objeto tipo Memoria
   */

  public FormacionEspecifica generarMockFormacionEspecifica(Long id, String nombre) {

    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(id);
    formacionEspecifica.setNombre(nombre);
    formacionEspecifica.setActivo(Boolean.TRUE);

    return formacionEspecifica;
  }
}