package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoRegimenConcurrenciaNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.TipoRegimenConcurrenciaRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoRegimenConcurrenciaServiceImpl;
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

public class TipoRegimenConcurrenciaServiceTest extends BaseServiceTest {

  @Mock
  private TipoRegimenConcurrenciaRepository repository;

  private TipoRegimenConcurrenciaService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new TipoRegimenConcurrenciaServiceImpl(repository);
  }

  @Test
  public void findById_WithExistingId_ReturnsTipoRegimenConcurrencia() throws Exception {
    // given: existing TipoRegimenConcurrencia
    TipoRegimenConcurrencia givenData = generarMockTipoRegimenConcurrencia(1L, Boolean.TRUE);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(givenData));

    // when: find by id TipoRegimenConcurrencia
    TipoRegimenConcurrencia data = service.findById(givenData.getId());

    // then: returns TipoRegimenConcurrencia
    Assertions.assertThat(data).isNotNull();
    Assertions.assertThat(data.getId()).isNotNull();
    Assertions.assertThat(data.getId()).isEqualTo(data.getId());
    Assertions.assertThat(data.getNombre()).isEqualTo(data.getNombre());
    Assertions.assertThat(data.getActivo()).isEqualTo(data.getActivo());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(TipoRegimenConcurrenciaNotFoundException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoRegimenConcurrencia
    List<TipoRegimenConcurrencia> data = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      data.add(generarMockTipoRegimenConcurrencia(Long.valueOf(i), Boolean.TRUE));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<TipoRegimenConcurrencia>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoRegimenConcurrencia>>() {
          @Override
          public Page<TipoRegimenConcurrencia> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoRegimenConcurrencia> content = data.subList(fromIndex, toIndex);
            Page<TipoRegimenConcurrencia> page = new PageImpl<>(content, pageable, data.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoRegimenConcurrencia> page = service.findAll(null, paging);

    // then: A Page with ten TipoRegimenConcurrencia are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoRegimenConcurrencia item = page.getContent().get(i);
      Assertions.assertThat(item.getNombre()).isEqualTo("nombre-" + j);
    }
  }

  /**
   * Función que devuelve un objeto TipoRegimenConcurrencia
   * 
   * @param id
   * @param activo
   * @return TipoRegimenConcurrencia
   */
  private TipoRegimenConcurrencia generarMockTipoRegimenConcurrencia(Long id, Boolean activo) {
    return TipoRegimenConcurrencia.builder().id(id).nombre("nombre-" + id).activo(activo).build();
  }

}
