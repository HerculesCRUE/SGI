package org.crue.hercules.sgi.eti.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eti.exceptions.TipoDocumentoNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eti.service.impl.TipoDocumentoServiceImpl;
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
 * TipoDocumentoServiceTest
 */
public class TipoDocumentoServiceTest extends BaseServiceTest {

  @Mock
  private TipoDocumentoRepository tipoDocumentoRepository;

  private TipoDocumentoService tipoDocumentoService;

  @BeforeEach
  public void setUp() throws Exception {
    tipoDocumentoService = new TipoDocumentoServiceImpl(tipoDocumentoRepository);
  }

  @Test
  public void find_WithId_ReturnsTipoDocumento() {

    BDDMockito.given(tipoDocumentoRepository.findById(1L))
        .willReturn(Optional.of(generarMockTipoDocumento(1L, "TipoDocumento1")));

    TipoDocumento tipoDocumento = tipoDocumentoService.findById(1L);

    Assertions.assertThat(tipoDocumento.getId()).isEqualTo(1L);

    Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento1");

  }

  @Test
  public void find_NotFound_ThrowsTipoDocumentoNotFoundException() throws Exception {
    BDDMockito.given(tipoDocumentoRepository.findById(1L)).willReturn(Optional.empty());

    Assertions.assertThatThrownBy(() -> tipoDocumentoService.findById(1L))
        .isInstanceOf(TipoDocumentoNotFoundException.class);
  }

  @Test
  public void findAll_Unlimited_ReturnsFullTipoDocumentoList() {
    // given: One hundred TipoDocumento
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoRepository.findAll(ArgumentMatchers.<Specification<TipoDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willReturn(new PageImpl<>(tipoDocumentos));

    // when: find unlimited
    Page<TipoDocumento> page = tipoDocumentoService.findAll(null, Pageable.unpaged());

    // then: Get a page with one hundred TipoDocumentos
    Assertions.assertThat(page.getContent().size()).isEqualTo(100);
    Assertions.assertThat(page.getNumber()).isEqualTo(0);
    Assertions.assertThat(page.getSize()).isEqualTo(100);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
  }

  @Test
  public void findAll_WithPaging_ReturnsPage() {
    // given: One hundred TipoDocumentos
    List<TipoDocumento> tipoDocumentos = new ArrayList<>();
    for (int i = 1; i <= 100; i++) {
      tipoDocumentos.add(generarMockTipoDocumento(Long.valueOf(i), "TipoDocumento" + String.format("%03d", i)));
    }

    BDDMockito.given(tipoDocumentoRepository.findAll(ArgumentMatchers.<Specification<TipoDocumento>>any(),
        ArgumentMatchers.<Pageable>any())).willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoDocumento> content = tipoDocumentos.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tipoDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoDocumento> page = tipoDocumentoService.findAll(null, paging);

    // then: A Page with ten TipoDocumentos are returned containing
    // nombre='TipoDocumento031' to 'TipoDocumento040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoDocumento tipoDocumento = page.getContent().get(i);
      Assertions.assertThat(tipoDocumento.getNombre()).isEqualTo("TipoDocumento" + String.format("%03d", j));
    }
  }

  /**
   * Función que devuelve un objeto TipoDocumento
   * 
   * @param id     id del tipoDocumento
   * @param nombre la descripción del tipoDocumento
   * @return el objeto tipoDocumento
   */

  public TipoDocumento generarMockTipoDocumento(Long id, String nombre) {

    Formulario formulario = new Formulario();
    formulario.setId(1L);
    formulario.setNombre("M10");
    formulario.setDescripcion("Formulario M10");

    TipoDocumento tipoDocumento = new TipoDocumento();
    tipoDocumento.setId(id);
    tipoDocumento.setNombre(nombre);
    tipoDocumento.setFormulario(formulario);
    tipoDocumento.setActivo(Boolean.TRUE);

    return tipoDocumento;
  }
}