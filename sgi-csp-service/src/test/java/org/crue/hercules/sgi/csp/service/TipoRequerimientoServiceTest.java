package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.TipoRequerimiento;
import org.crue.hercules.sgi.csp.repository.TipoRequerimientoRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.BeforeEach;
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
 * TipoRequerimientoServiceTest
 */
@Import({ TipoRequerimientoService.class, ApplicationContextSupport.class })
class TipoRequerimientoServiceTest extends BaseServiceTest {

  @MockBean
  private TipoRequerimientoRepository tipoRequerimientoRepository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private TipoRequerimientoService tipoRequerimientoService;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findActivos_WithPaging_ReturnsPage() {
    // given: One hundred TipoRequerimientos
    List<TipoRequerimiento> tipoRequerimientoList = new ArrayList<>();
    for (long i = 1; i <= 100; i++) {
      tipoRequerimientoList
          .add(generarMockTipoRequerimiento(i));
    }

    BDDMockito.given(
        tipoRequerimientoRepository.findAll(ArgumentMatchers.<Specification<TipoRequerimiento>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<TipoRequerimiento>>() {
          @Override
          public Page<TipoRequerimiento> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<TipoRequerimiento> content = tipoRequerimientoList.subList(fromIndex, toIndex);
            Page<TipoRequerimiento> page = new PageImpl<>(content, pageable, tipoRequerimientoList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<TipoRequerimiento> page = tipoRequerimientoService.findActivos(null, paging);

    // then: A Page with ten TipoRequerimientoes are returned containing
    // descripcion='TipoRequerimiento031' to 'TipoRequerimiento040'
    Assertions.assertThat(page.getContent().size()).isEqualTo(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      TipoRequerimiento tipoRequerimiento = page.getContent().get(i);
      Assertions.assertThat(tipoRequerimiento.getNombre()).isEqualTo("TipoRequerimiento-" + String.format("%03d", j));
    }
  }

  private TipoRequerimiento generarMockTipoRequerimiento(Long id) {
    String nombreSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockTipoRequerimiento(id, "TipoRequerimiento-" + nombreSuffix, Boolean.TRUE);
  }

  private TipoRequerimiento generarMockTipoRequerimiento(Long id, String nombre, Boolean activo) {
    return TipoRequerimiento.builder()
        .activo(activo)
        .id(id)
        .nombre(nombre)
        .build();
  }
}
