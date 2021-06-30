package org.crue.hercules.sgi.usr.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.usr.exceptions.UnidadNotFoundException;
import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.usr.repository.UnidadRepository;
import org.crue.hercules.sgi.usr.service.impl.UnidadServiceImpl;
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
import org.springframework.security.test.context.support.WithMockUser;

/**
 * UnidadServiceTest
 */

class UnidadServiceTest extends BaseServiceTest {

  @Mock
  private UnidadRepository repository;

  private UnidadService service;

  @BeforeEach
  void setUp() throws Exception {
    service = new UnidadServiceImpl(repository);
  }

  @Test
  void findAll_ReturnsPage() {
    // given: Una lista con 37 Unidad
    List<Unidad> unidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      unidades.add(generarMockUnidad(i, "Unidad" + String.format("%03d", i)));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Unidad>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Unidad>>() {
          @Override
          public Page<Unidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > unidades.size() ? unidades.size() : toIndex;
            List<Unidad> content = unidades.subList(fromIndex, toIndex);
            Page<Unidad> page = new PageImpl<>(content, pageable, unidades.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Unidad> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los Unidad del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Unidad unidad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(unidad.getNombre()).isEqualTo("Unidad" + String.format("%03d", i));
    }
  }

  @Test
  void findById_ReturnsUnidad() {
    // given: Un Unidad con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockUnidad(idBuscado)));

    // when: Buscamos el Unidad por su id
    Unidad unidad = service.findById(idBuscado);

    // then: el Unidad
    Assertions.assertThat(unidad).as("isNotNull()").isNotNull();
    Assertions.assertThat(unidad.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(unidad.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(unidad.getActivo()).as("getActivo()").isTrue();
  }

  @Test
  void findById_WithIdNotExist_ThrowsUnidadNotFoundException() throws Exception {
    // given: Ningun Unidad con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el Unidad por su id
    // then: lanza un UnidadNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado)).isInstanceOf(UnidadNotFoundException.class);
  }

  @Test
  @WithMockUser(username = "user", authorities = { "CSP-CON-E" })
  void findAllTodosRestringidos_ReturnsPage() {
    // given: Una lista con 37 Unidad
    List<Unidad> unidades = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      unidades.add(generarMockUnidad(i, "Unidad" + String.format("%03d", i)));
    }

    BDDMockito
        .given(repository.findAll(ArgumentMatchers.<Specification<Unidad>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Unidad>>() {
          @Override
          public Page<Unidad> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > unidades.size() ? unidades.size() : toIndex;
            List<Unidad> content = unidades.subList(fromIndex, toIndex);
            Page<Unidad> page = new PageImpl<>(content, pageable, unidades.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Unidad> page = service.findAllRestringidos(null, paging);

    // then: Devuelve la pagina 3 con los Unidad del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Unidad unidad = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(unidad.getNombre()).isEqualTo("Unidad" + String.format("%03d", i));
    }
  }

  /**
   * Función que devuelve un objeto Unidad
   * 
   * @param id id del Unidad
   * @return el objeto Unidad
   */
  private Unidad generarMockUnidad(Long id) {
    return generarMockUnidad(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto Unidad
   * 
   * @param id     id del Unidad
   * @param nombre nombre del Unidad
   * @return el objeto Unidad
   */
  private Unidad generarMockUnidad(Long id, String nombre) {
    Unidad unidad = new Unidad();
    unidad.setId(id);
    unidad.setNombre(nombre);
    unidad.setAcronimo("OPE");
    unidad.setDescripcion("descripcion-" + id);
    unidad.setActivo(true);

    return unidad;
  }

}
