package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.TipoAmbitoGeograficoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.service.impl.TipoAmbitoGeograficoServiceImpl;
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
 * TipoAmbitoGeograficoServiceTest
 */
public class TipoAmbitoGeograficoServiceTest extends BaseServiceTest {

  @Mock
  private TipoAmbitoGeograficoRepository repository;

  private TipoAmbitoGeograficoService service;

  @BeforeEach
  public void setUp() throws Exception {
    service = new TipoAmbitoGeograficoServiceImpl(repository);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 TipoAmbitoGeografico
    List<TipoAmbitoGeografico> tipoAmbitoGeograficos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tipoAmbitoGeograficos.add(generarMockTipoAmbitoGeografico(i, "TipoAmbitoGeografico" + String.format("%03d", i)));
    }

    BDDMockito.given(repository.findAll(ArgumentMatchers.<Specification<TipoAmbitoGeografico>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoAmbitoGeografico>>() {
          @Override
          public Page<TipoAmbitoGeografico> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tipoAmbitoGeograficos.size() ? tipoAmbitoGeograficos.size() : toIndex;
            List<TipoAmbitoGeografico> content = tipoAmbitoGeograficos.subList(fromIndex, toIndex);
            Page<TipoAmbitoGeografico> page = new PageImpl<>(content, pageable, tipoAmbitoGeograficos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoAmbitoGeografico> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los TipoAmbitoGeografico del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      TipoAmbitoGeografico tipoAmbitoGeografico = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(tipoAmbitoGeografico.getNombre())
          .isEqualTo("TipoAmbitoGeografico" + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsTipoAmbitoGeografico() {
    // given: Un TipoAmbitoGeografico con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockTipoAmbitoGeografico(idBuscado)));

    // when: Buscamos el TipoAmbitoGeografico por su id
    TipoAmbitoGeografico tipoAmbitoGeografico = service.findById(idBuscado);

    // then: el TipoAmbitoGeografico
    Assertions.assertThat(tipoAmbitoGeografico).as("isNotNull()").isNotNull();
    Assertions.assertThat(tipoAmbitoGeografico.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(tipoAmbitoGeografico.getNombre()).as("getNombre()").isEqualTo("nombre-1");
    Assertions.assertThat(tipoAmbitoGeografico.getActivo()).as("getActivo()").isEqualTo(true);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsTipoAmbitoGeograficoNotFoundException() throws Exception {
    // given: Ningun TipoAmbitoGeografico con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.empty());

    // when: Buscamos el TipoAmbitoGeografico por su id
    // then: lanza un TipoAmbitoGeograficoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(TipoAmbitoGeograficoNotFoundException.class);
  }

  /**
   * Función que devuelve un objeto TipoAmbitoGeografico
   * 
   * @param id id del TipoAmbitoGeografico
   * @return el objeto TipoAmbitoGeografico
   */
  private TipoAmbitoGeografico generarMockTipoAmbitoGeografico(Long id) {
    return generarMockTipoAmbitoGeografico(id, "nombre-" + id);
  }

  /**
   * Función que devuelve un objeto TipoAmbitoGeografico
   * 
   * @param id     id del TipoAmbitoGeografico
   * @param nombre nombre del TipoAmbitoGeografico
   * @return el objeto TipoAmbitoGeografico
   */
  private TipoAmbitoGeografico generarMockTipoAmbitoGeografico(Long id, String nombre) {
    TipoAmbitoGeografico tipoAmbitoGeografico = new TipoAmbitoGeografico();
    tipoAmbitoGeografico.setId(id);
    tipoAmbitoGeografico.setNombre(nombre);
    tipoAmbitoGeografico.setActivo(true);

    return tipoAmbitoGeografico;
  }

}
