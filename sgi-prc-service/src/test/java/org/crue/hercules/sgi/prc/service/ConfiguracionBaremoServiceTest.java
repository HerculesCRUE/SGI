package org.crue.hercules.sgi.prc.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoBaremo;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo.TipoNodo;
import org.crue.hercules.sgi.prc.repository.ConfiguracionBaremoRepository;
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
 * ConfiguracionBaremoServiceTest
 */
@Import({ ConfiguracionBaremoService.class, ApplicationContextSupport.class })
class ConfiguracionBaremoServiceTest extends BaseServiceTest {

  private static final EpigrafeCVN DEFAULT_DATA_EPIGRAFE_CVN = EpigrafeCVN.E030_040_000_000;
  private static final String DEFAULT_DATA_NOMBRE = "Configuracion Baremo";
  private static final TipoBaremo DEFAULT_DATA_TIPO_BAREMO = TipoBaremo.ARTICULO;
  private static final TipoNodo DEFAULT_DATA_TIPO_NODO = TipoNodo.PESO;

  @MockBean
  private ConfiguracionBaremoRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private ConfiguracionBaremoService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findActivos_ReturnsPage() {
    // given: Una lista con 37 ConfiguracionBaremo
    List<ConfiguracionBaremo> configuracionesBaremo = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      configuracionesBaremo.add(generarMockConfiguracionBaremo(i));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ConfiguracionBaremo>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ConfiguracionBaremo>>() {
          @Override
          public Page<ConfiguracionBaremo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > configuracionesBaremo.size() ? configuracionesBaremo.size() : toIndex;
            List<ConfiguracionBaremo> content = configuracionesBaremo.subList(fromIndex, toIndex);
            Page<ConfiguracionBaremo> page = new PageImpl<>(content, pageable,
                configuracionesBaremo.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ConfiguracionBaremo> page = service.findActivos(null, paging);

    // then: Devuelve la pagina 3 con los ConfiguracionBaremo del 31 al 37
    int numResult = page.getContent().size();
    Assertions.assertThat(numResult).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ConfiguracionBaremo configuracionBaremo = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(configuracionBaremo.getNombre()).as("getNombre")
          .isEqualTo(DEFAULT_DATA_NOMBRE + String.format("%03d", i));
    }
  }

  private ConfiguracionBaremo generarMockConfiguracionBaremo(Long id) {
    return this.generarMockConfiguracionBaremo(
        id, DEFAULT_DATA_EPIGRAFE_CVN, DEFAULT_DATA_NOMBRE + String.format("%03d", id),
        DEFAULT_DATA_TIPO_BAREMO, DEFAULT_DATA_TIPO_NODO);
  }

  private ConfiguracionBaremo generarMockConfiguracionBaremo(
      Long id, EpigrafeCVN epigrafeCVN, String nombre, TipoBaremo tipoBaremo, TipoNodo tipoNodo) {
    return ConfiguracionBaremo.builder()
        .id(id)
        .epigrafeCVN(epigrafeCVN)
        .nombre(nombre)
        .tipoBaremo(tipoBaremo)
        .tipoNodo(tipoNodo)
        .build();
  }
}
