package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.RolSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.repository.RolSocioRepository;
import org.crue.hercules.sgi.csp.service.impl.RolSocioServiceImpl;
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

class RolSocioServiceTest extends BaseServiceTest {

  @Mock
  private RolSocioRepository repository;
  private RolSocioService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new RolSocioServiceImpl(repository);
  }

  @Test
  void findById_WithExistingId_ReturnsRolSocio() throws Exception {
    // given: existing RolSocio
    RolSocio rolSocioExistente = generarMockRolSocio(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolSocioExistente));

    // when: find by id RolSocio
    RolSocio responseData = service.findById(rolSocioExistente.getId());

    // then: returns RolSocio
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(responseData.getId());
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(responseData.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(responseData.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()")
        .isEqualTo(responseData.getDescripcion());
    Assertions.assertThat(responseData.getCoordinador()).as("getCoordinador()")
        .isEqualTo(responseData.getCoordinador());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(rolSocioExistente.getActivo());
  }

  @Test
  void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(RolSocioNotFoundException.class);
  }

  @Test
  void findAll_WithPaging_ReturnsPage() {
    // given: One hundred RolSocio
    List<RolSocio> rolSocios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      if (i % 2 == 0) {
        rolSocios.add(generarMockRolSocio(Long.valueOf(i)));
      }
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<RolSocio>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolSocio>>() {
          @Override
          public Page<RolSocio> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolSocio> content = rolSocios.subList(fromIndex, toIndex);
            Page<RolSocio> page = new PageImpl<>(content, pageable, rolSocios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RolSocio> page = service.findAll(null, paging);

    // then: A Page with ten RolSocio are returned
    // containing Abreviatura='062' to '080'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(50);

    for (int i = 0, j = 62; i < 10; i++, j += 2) {
      RolSocio item = page.getContent().get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo(Boolean.TRUE);
    }
  }

  @Test
  void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred RolSocio
    List<RolSocio> rolSocios = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      rolSocios.add(generarMockRolSocio(Long.valueOf(i), (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<RolSocio>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolSocio>>() {
          @Override
          public Page<RolSocio> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolSocio> content = rolSocios.subList(fromIndex, toIndex);
            Page<RolSocio> page = new PageImpl<>(content, pageable, rolSocios.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RolSocio> page = service.findAll(null, paging);

    // then: A Page with ten RolSocio are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page).hasSize(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      RolSocio item = page.getContent().get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  /**
   * Función que genera RolSocio
   * 
   * @param rolSocioId
   * @return el rolSocio
   */
  private RolSocio generarMockRolSocio(Long rolSocioId) {

    String suffix = String.format("%03d", rolSocioId);

    // @formatter:off
    RolSocio rolSocio = RolSocio.builder()
        .id(rolSocioId)
        .abreviatura(suffix)
        .nombre("nombre-" + suffix)
        .descripcion("descripcion-" + suffix)
        .coordinador(Boolean.FALSE)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on

    return rolSocio;
  }

  /**
   * Función que genera RolSocio con el estado indicado
   * 
   * @param rolSocioId
   * @param activo
   * @return el rolSocio
   */
  private RolSocio generarMockRolSocio(Long rolSocioId, Boolean activo) {

    RolSocio rolSocio = generarMockRolSocio(rolSocioId);
    rolSocio.setActivo(activo);

    return rolSocio;
  }

}
