package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.model.TipoTareaNombre;
import org.crue.hercules.sgi.eti.repository.TipoTareaRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoTareaServiceImpl;
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

    Assertions.assertThat(I18nHelper.getValueForLanguage(tipoTarea.getNombre(), Language.ES)).isEqualTo("TipoTarea1");

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
      Assertions.assertThat(I18nHelper.getValueForLanguage(tipoTarea.getNombre(), Language.ES))
          .isEqualTo("TipoTarea" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoTarea
   * 
   * @param id     id del tipoTarea
   * @param nombre nombre del tipo de Tarea
   * @return el objeto tipo Tarea
   */
  private TipoTarea generarMockTipoTarea(Long id, String nombre) {
    Set<TipoTareaNombre> nom = new HashSet<>();
    nom.add(new TipoTareaNombre(Language.ES, nombre));
    TipoTarea tipoTarea = new TipoTarea();
    tipoTarea.setId(id);
    tipoTarea.setNombre(nom);
    tipoTarea.setActivo(Boolean.TRUE);

    return tipoTarea;
  }
}