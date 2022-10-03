package org.crue.hercules.sgi.eer.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.eer.model.TipoDocumento;
import org.crue.hercules.sgi.eer.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * TipoDocumentoServiceTest
 */
@Import({ TipoDocumentoService.class, ApplicationContextSupport.class })
class TipoDocumentoServiceTest extends BaseServiceTest {
  private static final String NOMBRE_PREFIX = "Tipo Documento ";

  @MockBean
  private TipoDocumentoRepository repository;

  @Autowired
  private TipoDocumentoService service;

  @Test
  void findTiposActivos_ReturnsPage() {
    // given: Una lista con 37 TipoDocumento
    List<TipoDocumento> tiposDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposDocumentos
          .add(generateTipoDocumento(i, NOMBRE_PREFIX + String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<TipoDocumento>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposDocumentos.size() ? tiposDocumentos.size() : toIndex;
            List<TipoDocumento> content = tiposDocumentos.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tiposDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoDocumento> page = service.findTiposActivos(null, paging);

    // then: Devuelve la pagina 3 con los TipoDocumento del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      TipoDocumento tipoDocumento = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(tipoDocumento.getNombre())
          .isEqualTo(NOMBRE_PREFIX + String.format("%03d", i));
    }
  }

  @Test
  void findSubtiposActivos_ReturnsPage() {
    // given: Una lista con 37 TipoDocumento pertenecientes a un padreId
    Long padreId = 55L;
    List<TipoDocumento> tiposDocumentos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      tiposDocumentos
          .add(generateTipoDocumento(i, NOMBRE_PREFIX + String.format("%03d", i), padreId));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<TipoDocumento>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoDocumento>>() {
          @Override
          public Page<TipoDocumento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > tiposDocumentos.size() ? tiposDocumentos.size() : toIndex;
            List<TipoDocumento> content = tiposDocumentos.subList(fromIndex, toIndex);
            Page<TipoDocumento> page = new PageImpl<>(content, pageable, tiposDocumentos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoDocumento> page = service.findSubtiposActivos(padreId, null, paging);

    // then: Devuelve la pagina 3 con los TipoDocumento del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      TipoDocumento tipoDocumento = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(tipoDocumento.getNombre())
          .isEqualTo(NOMBRE_PREFIX + String.format("%03d", i));
      Assertions.assertThat(tipoDocumento.getPadre().getId())
          .isEqualTo(padreId);
    }
  }

  private TipoDocumento generateTipoDocumento(Long id, String nombre) {
    return generateTipoDocumento(id, Boolean.TRUE, nombre, "Descripcion", null);
  }

  private TipoDocumento generateTipoDocumento(Long id, String nombre, Long padreId) {
    return generateTipoDocumento(id, Boolean.TRUE, nombre, "Descripcion",
        generateTipoDocumento(padreId, Boolean.TRUE, "Padre", "Padre", null));
  }

  private TipoDocumento generateTipoDocumento(Long id, Boolean activo, String nombre, String descripcion,
      TipoDocumento padre) {
    return TipoDocumento.builder()
        .activo(activo)
        .descripcion(descripcion)
        .id(id)
        .nombre(nombre)
        .padre(padre)
        .build();
  }
}
