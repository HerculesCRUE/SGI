package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.service.impl.RolProyectoServiceImpl;
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

public class RolProyectoServiceTest extends BaseServiceTest {

  @Mock
  private RolProyectoRepository repository;
  private RolProyectoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new RolProyectoServiceImpl(repository);
  }

  @Test
  public void findById_WithExistingId_ReturnsRolProyecto() throws Exception {
    // given: existing RolProyecto
    RolProyecto rolProyectoExistente = generarMockRolProyecto(1L);

    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(rolProyectoExistente));

    // when: find by id RolProyecto
    RolProyecto responseData = service.findById(rolProyectoExistente.getId());

    // then: returns RolProyecto
    Assertions.assertThat(responseData.getId()).as("getId()").isEqualTo(responseData.getId());
    Assertions.assertThat(responseData.getId()).as("getId()").isNotNull();
    Assertions.assertThat(responseData.getAbreviatura()).as("getAbreviatura()")
        .isEqualTo(responseData.getAbreviatura());
    Assertions.assertThat(responseData.getNombre()).as("getNombre()").isEqualTo(responseData.getNombre());
    Assertions.assertThat(responseData.getDescripcion()).as("getDescripcion()")
        .isEqualTo(responseData.getDescripcion());
    Assertions.assertThat(responseData.getRolPrincipal()).as("getRolPrincipal()")
        .isEqualTo(responseData.getRolPrincipal());
    Assertions.assertThat(responseData.getOrden()).as("getOrden()").isEqualTo(responseData.getOrden());
    Assertions.assertThat(responseData.getEquipo()).as("getEquipo()").isEqualTo(responseData.getEquipo());
    Assertions.assertThat(responseData.getActivo()).as("getActivo()").isEqualTo(rolProyectoExistente.getActivo());
  }

  @Test
  public void findById_WithNoExistingId_ThrowsNotFoundException() throws Exception {
    // given: no existing id
    BDDMockito.given(repository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(
        // when: find by non existing id
        () -> service.findById(1L))
        // then: NotFoundException is thrown
        .isInstanceOf(RolProyectoNotFoundException.class);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred RolProyecto
    List<RolProyecto> rolProyectos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      if (i % 2 == 0) {
        rolProyectos.add(generarMockRolProyecto(Long.valueOf(i)));
      }
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<RolProyecto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolProyecto>>() {
          @Override
          public Page<RolProyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolProyecto> content = rolProyectos.subList(fromIndex, toIndex);
            Page<RolProyecto> page = new PageImpl<>(content, pageable, rolProyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RolProyecto> page = service.findAll(null, paging);

    // then: A Page with ten RolProyecto are returned
    // containing Abreviatura='062' to '080'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(50);

    for (int i = 0, j = 62; i < 10; i++, j += 2) {
      RolProyecto item = page.getContent().get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo(Boolean.TRUE);
    }
  }

  @Test
  public void findAllTodos_WithPaging_ReturnsPage() {
    // given: One hundred RolProyecto
    List<RolProyecto> rolProyectos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      rolProyectos.add(generarMockRolProyecto(Long.valueOf(i), (i % 2 == 0) ? Boolean.TRUE : Boolean.FALSE));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<RolProyecto>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RolProyecto>>() {
          @Override
          public Page<RolProyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RolProyecto> content = rolProyectos.subList(fromIndex, toIndex);
            Page<RolProyecto> page = new PageImpl<>(content, pageable, rolProyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RolProyecto> page = service.findAll(null, paging);

    // then: A Page with ten RolProyecto are returned containing
    // Nombre='nombre-31' to
    // 'nombre-40'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      RolProyecto item = page.getContent().get(i);
      Assertions.assertThat(item.getAbreviatura()).isEqualTo(String.format("%03d", j));
      Assertions.assertThat(item.getActivo()).isEqualTo((j % 2 == 0 ? Boolean.TRUE : Boolean.FALSE));
    }
  }

  /**
   * Función que genera RolProyecto
   * 
   * @param rolProyectoId
   * @return el rolProyecto
   */
  private RolProyecto generarMockRolProyecto(Long rolProyectoId) {

    String suffix = String.format("%03d", rolProyectoId);

    // @formatter:off
    RolProyecto rolProyecto = RolProyecto.builder()
        .id(rolProyectoId)
        .abreviatura(suffix)
        .nombre("nombre-" + suffix)
        .descripcion("descripcion-" + suffix)
        .rolPrincipal(Boolean.FALSE)
        .orden(null)
        .equipo(RolProyecto.Equipo.INVESTIGACION)
        .activo(Boolean.TRUE)
        .build();
    // @formatter:on

    return rolProyecto;
  }

  /**
   * Función que genera RolProyecto con el estado indicado
   * 
   * @param rolProyectoId
   * @param activo
   * @return el rolProyecto
   */
  private RolProyecto generarMockRolProyecto(Long rolProyectoId, Boolean activo) {

    RolProyecto rolProyecto = generarMockRolProyecto(rolProyectoId);
    rolProyecto.setActivo(activo);

    return rolProyecto;
  }

}
