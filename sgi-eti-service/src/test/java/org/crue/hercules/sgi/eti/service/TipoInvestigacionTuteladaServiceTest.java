package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.eti.repository.TipoInvestigacionTuteladaRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoInvestigacionTuteladaServiceImpl;
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
 * TipoInvestigacionTuteladaServiceTest
 */
public class TipoInvestigacionTuteladaServiceTest extends BaseServiceTest {

  @Mock
  private TipoInvestigacionTuteladaRepository tipoInvestigacionTuteladaRepository;

  private TipoInvestigacionTuteladaService tipoInvestigacionTuteladaService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoInvestigacionTuteladaService = new TipoInvestigacionTuteladaServiceImpl(tipoInvestigacionTuteladaRepository);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoInvestigacionTuteladaList() {
    // given: One hundred TipoInvestigacionTutelada
    List<TipoInvestigacionTutelada> tipoInvestigacionTuteladas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoInvestigacionTuteladas.add(generarMockTipoInvestigacionTutelada(Long.valueOf(i),
          "TipoInvestigacionTutelada" + String.format("%03d", i)));
    }

    BDDMockito
        .given(tipoInvestigacionTuteladaRepository.findAll(
            ArgumentMatchers.<Specification<TipoInvestigacionTutelada>>any(), ArgumentMatchers.<Pageable>any()))
        .willReturn(new PageImpl<>(tipoInvestigacionTuteladas));

    // when: find unlimited
    Page<TipoInvestigacionTutelada> page = tipoInvestigacionTuteladaService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoInvestigacionTuteladas
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoInvestigacionTuteladas
    List<TipoInvestigacionTutelada> tipoInvestigacionTuteladas = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoInvestigacionTuteladas.add(generarMockTipoInvestigacionTutelada(Long.valueOf(i),
          "TipoInvestigacionTutelada" + String.format("%03d", i)));
    }

    BDDMockito
        .given(tipoInvestigacionTuteladaRepository.findAll(
            ArgumentMatchers.<Specification<TipoInvestigacionTutelada>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoInvestigacionTutelada>>() {
          @Override
          public Page<TipoInvestigacionTutelada> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoInvestigacionTutelada> content = tipoInvestigacionTuteladas.subList(fromIndex, toIndex);
            Page<TipoInvestigacionTutelada> page = new PageImpl<>(content, pageable, tipoInvestigacionTuteladas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoInvestigacionTutelada> page = tipoInvestigacionTuteladaService.findAll(null, paging);

    // then: A Page with ten TipoInvestigacionTuteladas are returned containing
    // descripcion='TipoInvestigacionTutelada031' to 'TipoInvestigacionTutelada040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoInvestigacionTutelada tipoInvestigacionTutelada = page.getContent().get(i);
      Assertions.assertThat(tipoInvestigacionTutelada.getNombre())
          .isEqualTo("TipoInvestigacionTutelada" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoInvestigacionTutelada
   * 
   * @param id     id del tipoInvestigacionTutelada
   * @param nombre nombre del tipo de Investigacion Tutelada
   * @return el objeto tipo Investigacion Tutelada
   */

  public TipoInvestigacionTutelada generarMockTipoInvestigacionTutelada(Long id, String nombre) {

    TipoInvestigacionTutelada tipoInvestigacionTutelada = new TipoInvestigacionTutelada();
    tipoInvestigacionTutelada.setId(id);
    tipoInvestigacionTutelada.setNombre(nombre);
    tipoInvestigacionTutelada.setActivo(Boolean.TRUE);

    return tipoInvestigacionTutelada;
  }
}