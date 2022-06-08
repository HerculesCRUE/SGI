package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.prc.model.Baremo;
import org.crue.hercules.sgi.prc.model.Baremo.TipoCuantia;
import org.crue.hercules.sgi.prc.repository.BaremoRepository;
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
 * BaremoServiceTest
 */
@Import({ BaremoService.class, ApplicationContextSupport.class })
class BaremoServiceTest extends BaseServiceTest {

  private static final Integer DEFAULT_DATA_PESO = 100;
  private static final BigDecimal DEFAULT_DATA_PUNTOS = new BigDecimal(20.5);
  private static final BigDecimal DEFAULT_DATA_CUANTIA = new BigDecimal(50.20);
  private static final TipoCuantia DEFAULT_DATA_TIPO_CUANTIA = TipoCuantia.PUNTOS;
  private static final Long DEFAULT_DATA_CONFIGURACION_BAREMO_ID = 1L;
  private static final Long DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID = 1L;

  @MockBean
  private BaremoRepository repository;

  @MockBean
  private ConfiguracionBaremoRepository configuracionBaremoRepository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private BaremoService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findByConvocatoriaBaremacionId_ReturnsPage() {
    // given: Una lista con 37 Baremo y un id de ConvocatoriaBaremacion
    final Long convocatoriaBaremacionId = 1L;
    List<Baremo> baremos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      baremos.add(generarMockBaremo(i));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<Baremo>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Baremo>>() {
          @Override
          public Page<Baremo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > baremos.size() ? baremos.size() : toIndex;
            List<Baremo> content = baremos.subList(fromIndex, toIndex);
            Page<Baremo> page = new PageImpl<>(content, pageable,
                baremos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Baremo> page = service.findByConvocatoriaBaremacionId(convocatoriaBaremacionId, null, paging);

    // then: Devuelve la pagina 3 con los Baremo del 31 al 37
    int numResult = page.getContent().size();
    Assertions.assertThat(numResult).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Baremo baremo = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(baremo.getId()).as("getId")
          .isEqualTo(i);
    }
  }

  @Test
  void updateBaremos_ThrowsNoRelatedEntitiesException() {
    // given: Una lista de Baremo asignados una ConvocatoriaBaremacion diferente de
    // la esperada
    final Long convocatoriaBaremacionIdExpected = DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID;
    final Long convocatoriaBaremacionIdActual = 2L;
    List<Baremo> baremos = new ArrayList<>();
    baremos.add(generarMockBaremoPeso(convocatoriaBaremacionIdActual));

    Assertions.assertThatThrownBy(
        // when: update Baremo asociados a la ConvocatoriaBaremacion
        () -> service.updateBaremos(convocatoriaBaremacionIdExpected, baremos))
        // then: throw exception el id de la ConvocatoriaBaremacion que contiene el
        // Baremo no coincide con el pasado por parámetros
        .isInstanceOf(NoRelatedEntitiesException.class);
  }

  private Baremo generarMockBaremo(Long id) {
    return this.generarMockBaremo(
        id, DEFAULT_DATA_PESO, DEFAULT_DATA_PUNTOS, DEFAULT_DATA_CUANTIA, DEFAULT_DATA_TIPO_CUANTIA,
        DEFAULT_DATA_CONFIGURACION_BAREMO_ID, DEFAULT_DATA_CONVOCATORIA_BAREMACION_ID);
  }

  private Baremo generarMockBaremoPeso(Long convocatoriaBaremacionId) {
    return this.generarMockBaremo(
        null, DEFAULT_DATA_PESO, null, null, null,
        DEFAULT_DATA_CONFIGURACION_BAREMO_ID, convocatoriaBaremacionId);
  }

  private Baremo generarMockBaremo(
      Long id, Integer peso, BigDecimal puntos, BigDecimal cuantia,
      TipoCuantia tipoCuantia, Long configuracionBaremoId, Long convocatoriaBaremacionId) {
    return Baremo.builder()
        .id(id)
        .peso(peso)
        .cuantia(cuantia)
        .puntos(puntos)
        .tipoCuantia(tipoCuantia)
        .configuracionBaremoId(configuracionBaremoId)
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .build();
  }
}
