package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.repository.TipoOrigenFuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoOrigenFuenteFinanciacionServiceImpl;
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
 * TipoOrigenFuenteFinanciacionServiceTest
 */

public class TipoOrigenFuenteFinanciacionServiceTest extends BaseServiceTest {

  @Mock
  private TipoOrigenFuenteFinanciacionRepository tipoOrigenFuenteFinanciacionRepository;

  private TipoOrigenFuenteFinanciacionService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new TipoOrigenFuenteFinanciacionServiceImpl(tipoOrigenFuenteFinanciacionRepository);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 TipoOrigenFuenteFinanciacion
    List<TipoOrigenFuenteFinanciacion> tiposOrigenFuenteFinanciacion = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposOrigenFuenteFinanciacion
          .add(generarMockTipoOrigenFuenteFinanciacion(i, "TipoOrigenFuenteFinanciacion" + String.format("%03d", i)));
    }

    BDDMockito
        .given(tipoOrigenFuenteFinanciacionRepository.findAll(
            ArgumentMatchers.<Specification<TipoOrigenFuenteFinanciacion>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoOrigenFuenteFinanciacion>>() {
          @Override
          public Page<TipoOrigenFuenteFinanciacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposOrigenFuenteFinanciacion.size() ? tiposOrigenFuenteFinanciacion.size() : toIndex;
            List<TipoOrigenFuenteFinanciacion> content = tiposOrigenFuenteFinanciacion.subList(fromIndex, toIndex);
            Page<TipoOrigenFuenteFinanciacion> page = new PageImpl<>(content, pageable,
                tiposOrigenFuenteFinanciacion.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoOrigenFuenteFinanciacion> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los TipoOrigenFuenteFinanciacion del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(tipoOrigenFuenteFinanciacion.getId()).isEqualTo(Long.valueOf(i));

    }
  }

  @Test
  public void findAll_ReturnsPageEmpty() {
    // given: Una lista con 37 TipoOrigenFuenteFinanciacion
    List<TipoOrigenFuenteFinanciacion> tiposOrigenFuenteFinanciacion = new ArrayList<>();

    BDDMockito
        .given(tipoOrigenFuenteFinanciacionRepository.findAll(
            ArgumentMatchers.<Specification<TipoOrigenFuenteFinanciacion>>any(), ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoOrigenFuenteFinanciacion>>() {
          @Override
          public Page<TipoOrigenFuenteFinanciacion> answer(InvocationOnMock invocation) throws Throwable {
            Page<TipoOrigenFuenteFinanciacion> page = new PageImpl<>(tiposOrigenFuenteFinanciacion);
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoOrigenFuenteFinanciacion> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los TipoOrigenFuenteFinanciacion del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent()").isEmpty();
    ;
  }

  /**
   * Función que devuelve un objeto TipoOrigenFuenteFinanciacion
   * 
   * @param id id del TipoOrigenFuenteFinanciacion
   * @return el objeto TipoOrigenFuenteFinanciacion
   */
  public TipoOrigenFuenteFinanciacion generarMockTipoOrigenFuenteFinanciacion(Long id) {
    return generarMockTipoOrigenFuenteFinanciacion(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoOrigenFuenteFinanciacion
   * 
   * @param id     id del TipoOrigenFuenteFinanciacion
   * @param nombre nombre del TipoOrigenFuenteFinanciacion
   * @return el objeto TipoOrigenFuenteFinanciacion
   */
  public TipoOrigenFuenteFinanciacion generarMockTipoOrigenFuenteFinanciacion(Long id, String nombre) {

    TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion = new TipoOrigenFuenteFinanciacion();
    tipoOrigenFuenteFinanciacion.setId(id);
    tipoOrigenFuenteFinanciacion.setActivo(true);
    tipoOrigenFuenteFinanciacion.setNombre(nombre);

    return tipoOrigenFuenteFinanciacion;
  }

}
