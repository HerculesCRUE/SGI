package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.FormacionEspecificaNotFoundException;
import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.model.FormacionEspecificaNombre;
import org.crue.hercules.sgi.eti.repository.FormacionEspecificaRepository;
import org.crue.hercules.sgi.eti.service.impl.FormacionEspecificaServiceImpl;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.framework.i18n.Language;
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

    Assertions.assertThat(I18nHelper.getValueForLanguage(formacionEspecifica.getNombre(), Language.ES))
        .isEqualTo("FormacionEspecifica1");

  }

  @Test
  public void find_NotFound_ThrowsFormacionEspecificaNotFoundException() throws Exception {
    BDDMockito.given(formacionEspecificaRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> formacionEspecificaService.findById(1L))
        .isInstanceOf(FormacionEspecificaNotFoundException.class);
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
      Assertions.assertThat(I18nHelper.getValueForLanguage(formacionEspecifica.getNombre(), Language.ES))
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
  private FormacionEspecifica generarMockFormacionEspecifica(Long id, String nombre) {

    Set<FormacionEspecificaNombre> nom = new HashSet<>();
    nom.add(new FormacionEspecificaNombre(Language.ES, nombre));
    FormacionEspecifica formacionEspecifica = new FormacionEspecifica();
    formacionEspecifica.setId(id);
    formacionEspecifica.setNombre(nom);
    formacionEspecifica.setActivo(Boolean.TRUE);

    return formacionEspecifica;
  }
}