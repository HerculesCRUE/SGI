package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.FormularioNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.FormularioReportRepository;
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

  @Mock
  private MemoriaService memoriaService;

  @Mock
  private RetrospectivaService retrospectivaService;

  @Mock
  private FormularioReportRepository formularioReportRepository;

  private FormularioService formularioService;

  @BeforeEach
  public void setUp() throws Exception {
    formularioService = new FormularioServiceImpl(formularioRepository, memoriaService, retrospectivaService,
        formularioReportRepository);
  }

  @Test
  public void find_WithId_ReturnsFormulario() {
    BDDMockito.given(formularioRepository.findById(1L))
        .willReturn(Optional.of(generarMockFormulario(1L)));

    Formulario formulario = formularioService.findById(1L);

    Assertions.assertThat(formulario.getId()).isEqualTo(1L);

  }

  @Test
  public void find_NotFound_ThrowsFormularioNotFoundException() throws Exception {
    BDDMockito.given(formularioRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> formularioService.findById(1L)).isInstanceOf(FormularioNotFoundException.class);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullFormularioList() {
    // given: One hundred Formulario
    List<Formulario> formularios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      formularios.add(generarMockFormulario(Long.valueOf(i)));
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
      formularios.add(generarMockFormulario(Long.valueOf(i)));
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
      Assertions.assertThat(formulario.getId()).isEqualTo(j);
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

  private Formulario generarMockFormulario(Long id) {

    Formulario formulario = new Formulario();
    formulario.setId(id);
    formulario.setTipo(Formulario.Tipo.MEMORIA);

    return formulario;
  }
}