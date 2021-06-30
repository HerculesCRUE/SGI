package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.FormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.FormularioRepository;
import org.crue.hercules.sgi.eti.service.impl.FormularioServiceImpl;
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
 * FormularioServiceTest
 */
public class FormularioServiceTest extends BaseServiceTest {

  @Mock
  private FormularioRepository formularioRepository;

  private FormularioService formularioService;

  @BeforeEach
  public void setUp() throws Exception {
    formularioService = new FormularioServiceImpl(formularioRepository);
  }

  @Test
  public void find_WithId_ReturnsFormulario() {
    BDDMockito.given(formularioRepository.findById(1L))
        .willReturn(Optional.of(generarMockFormulario(1L, "Formulario1", "Descripcion1")));

    Formulario formulario = formularioService.findById(1L);

    Assertions.assertThat(formulario.getId()).isEqualTo(1L);
    Assertions.assertThat(formulario.getNombre()).isEqualTo("Formulario1");
    Assertions.assertThat(formulario.getDescripcion()).isEqualTo("Descripcion1");

  }

  @Test
  public void find_NotFound_ThrowsFormularioNotFoundException() throws Exception {
    BDDMockito.given(formularioRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> formularioService.findById(1L)).isInstanceOf(FormularioNotFoundException.class);
  }

  @Test
  public void create_ReturnsFormulario() {
    // given: Un nuevo Formulario
    Formulario formularioNew = generarMockFormulario(1L, "FormularioNew", "Descripcion1");

    Formulario formulario = generarMockFormulario(1L, "FormularioNew", "Descripcion1");

    BDDMockito.given(formularioRepository.save(formularioNew)).willReturn(formulario);

    // when: Creamos el Formulario
    Formulario formularioCreado = formularioService.create(formularioNew);

    // then: El Formulario se crea correctamente
    Assertions.assertThat(formularioCreado).isNotNull();
    Assertions.assertThat(formularioCreado.getId()).isEqualTo(1L);
    Assertions.assertThat(formularioCreado.getNombre()).isEqualTo("FormularioNew");
    Assertions.assertThat(formularioCreado.getDescripcion()).isEqualTo("Descripcion1");
  }

  @Test
  public void create_FormularioWithoutId_ThrowsIllegalArgumentException() {
    // given: Un nuevo Formulario que no tiene id
    Formulario formularioNew = generarMockFormulario(null, "FormularioNew", "Descripcion1");
    // when: Creamos el Formulario
    // then: Lanza una excepcion porque el Formulario no tiene id
    Assertions.assertThatThrownBy(() -> formularioService.create(formularioNew))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void update_ReturnsFormulario() {
    // given: Un nuevo Formulario con el nombre y la descripcion actualizados
    Formulario formularioServicioActualizado = generarMockFormulario(1L, "Formulario1 actualizada",
        "Descripcion1 actualizada");

    Formulario formulario = generarMockFormulario(1L, "Formulario1", "Descripcion1");

    BDDMockito.given(formularioRepository.findById(1L)).willReturn(Optional.of(formulario));
    BDDMockito.given(formularioRepository.save(formulario)).willReturn(formularioServicioActualizado);

    // when: Actualizamos el Formulario
    Formulario formularioActualizado = formularioService.update(formulario);

    // then: El Formulario se actualiza correctamente.
    Assertions.assertThat(formularioActualizado.getId()).isEqualTo(1L);
    Assertions.assertThat(formularioActualizado.getNombre()).isEqualTo("Formulario1 actualizada");
    Assertions.assertThat(formularioActualizado.getDescripcion()).isEqualTo("Descripcion1 actualizada");

  }

  @Test
  public void update_ThrowsFormularioNotFoundException() {
    // given: Un nuevo Formulario a actualizar
    Formulario formulario = generarMockFormulario(1L, "Formulario", "Descripcion");

    // then: Lanza una excepcion porque el Formulario no existe
    Assertions.assertThatThrownBy(() -> formularioService.update(formulario))
        .isInstanceOf(FormularioNotFoundException.class);

  }

  @Test
  public void update_WithoutId_ThrowsIllegalArgumentException() {

    // given: Un Formulario que venga sin id
    Formulario formulario = generarMockFormulario(null, "Formulario", "Descripcion");

    Assertions.assertThatThrownBy(
        // when: update Formulario
        () -> formularioService.update(formulario))
        // then: Lanza una excepción, el id es necesario
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_WithoutId_ThrowsIllegalArgumentException() {
    // given: Sin id
    Assertions.assertThatThrownBy(
        // when: Delete sin id
        () -> formularioService.delete(null))
        // then: Lanza una excepción
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void delete_NonExistingId_ThrowsFormularioNotFoundException() {
    // given: Id no existe
    BDDMockito.given(formularioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.FALSE);

    Assertions.assertThatThrownBy(
        // when: Delete un id no existente
        () -> formularioService.delete(1L))
        // then: Lanza FormularioNotFoundException
        .isInstanceOf(FormularioNotFoundException.class);
  }

  @Test
  public void delete_WithExistingId_DeletesFormulario() {
    // given: Id existente
    BDDMockito.given(formularioRepository.existsById(ArgumentMatchers.anyLong())).willReturn(Boolean.TRUE);
    BDDMockito.doNothing().when(formularioRepository).deleteById(ArgumentMatchers.anyLong());

    Assertions.assertThatCode(
        // when: Delete con id existente
        () -> formularioService.delete(1L))
        // then: No se lanza ninguna excepción
        .doesNotThrowAnyException();
  }

  @Test
  public void findAll_Unlimited_ReturnsFullFormularioList() {
    // given: One hundred Formulario
    List<Formulario> formularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formularios.add(generarMockFormulario(Long.valueOf(i), "Formulario" + String.format("%03d", i), "Descripcion"));
    }

    BDDMockito.given(formularioRepository.findAll(ArgumentMatchers.<Specification<Formulario>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(formularios));

    // when: find unlimited
    Page<Formulario> page = formularioService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred Formularios
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred Formularios
    List<Formulario> formularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formularios.add(generarMockFormulario(Long.valueOf(i), "Formulario" + String.format("%03d", i), "Descripcion"));
    }

    BDDMockito.given(formularioRepository.findAll(ArgumentMatchers.<Specification<Formulario>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<Formulario>>() {
          @Override
          public Page<Formulario> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<Formulario> content = formularios.subList(fromIndex, toIndex);
            Page<Formulario> page = new PageImpl<>(content, pageable, formularios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Formulario> page = formularioService.findAll(null, paging);

    // then: A Page with ten Formularios are returned containing
    // nombre='Formulario031' to 'Formulario040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      Formulario formulario = page.getContent().get(i);
      Assertions.assertThat(formulario.getNombre()).isEqualTo("Formulario" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto Formulario
   * 
   * @param id          id del Formulario
   * @param nombre      nombre del Formulario
   * @param descripcion descripción del Formulario
   * @return el objeto Formulario
   */

  public Formulario generarMockFormulario(Long id, String nombre, String descripcion) {

    Formulario formulario = new Formulario();
    formulario.setId(id);
    formulario.setNombre(nombre);
    formulario.setDescripcion(descripcion);

    return formulario;
  }
}