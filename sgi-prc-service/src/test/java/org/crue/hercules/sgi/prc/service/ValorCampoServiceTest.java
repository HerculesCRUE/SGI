package org.crue.hercules.sgi.prc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.ValorCampoNotFoundException;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
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
 * ValorCampoServiceTest
 */
@Import({ ValorCampoService.class, ApplicationContextSupport.class })
class ValorCampoServiceTest extends BaseServiceTest {

  private static final String VALOR_PREFIX = "Valor-";
  private static final String DEFAULT_DATA_VALOR = VALOR_PREFIX + "default";
  private static final Integer DEFAULT_DATA_ORDEN = 1;
  private static final Long DEFAULT_DATA_CAMPO_PRODUCCION_CIENTIFICA_ID = 1L;

  @MockBean
  private ValorCampoRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private ValorCampoService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findAll_ReturnsPage() {
    // given: Una lista con 37 ValorCampo
    List<ValorCampo> valores = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      valores.add(generarMockValorCampo(i, String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<ValorCampo>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ValorCampo>>() {
          @Override
          public Page<ValorCampo> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > valores.size() ? valores.size() : toIndex;
            List<ValorCampo> content = valores.subList(fromIndex, toIndex);
            Page<ValorCampo> page = new PageImpl<>(content, pageable, valores.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ValorCampo> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los ValorCampo del 31 al 37
    int numElements = page.getContent().size();
    Assertions.assertThat(numElements).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      ValorCampo valor = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(valor.getValor()).as("getValor")
          .isEqualTo(VALOR_PREFIX + String.format("%03d", i));
    }
  }

  @Test
  void findById_ReturnsValorCampo() {
    // given: ValorCampo con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockValorCampo(idBuscado)));

    // when: Buscamos ValorCampo por su id
    ValorCampo valor = service.findById(idBuscado);

    // then: el ValorCampo
    Assertions.assertThat(valor).as("isNotNull()").isNotNull();
    Assertions.assertThat(valor.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(valor.getCampoProduccionCientificaId()).as("getCampoProduccionCientificaId")
        .isEqualTo(DEFAULT_DATA_CAMPO_PRODUCCION_CIENTIFICA_ID);
    Assertions.assertThat(valor.getOrden()).as("getOrden")
        .isEqualTo(DEFAULT_DATA_ORDEN);
    Assertions.assertThat(valor.getValor()).as("getValor")
        .isEqualTo(DEFAULT_DATA_VALOR);
  }

  @Test
  void findById_WithIdNotExist_ThrowsValorCampoNotFoundException() {
    // given: Ningun ValorCampo con el id buscado
    Long idBuscado = 33L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Buscamos el ValorCampo por su id
    // then: Lanza un ValorCampoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ValorCampoNotFoundException.class);
  }

  @Test
  void findAllByCampoProduccionCientificaId_ReturnsList() {
    // given: Una lista con 7 ValorCampo y un produccionCientificaId
    Long campoProduccionCientificaId = 1L;
    List<ValorCampo> valores = new ArrayList<>();
    for (long i = 1; i <= 7; i++) {
      if (i % 2 == 0) {
        valores.add(generarMockValorCampo(i, campoProduccionCientificaId, String.format("%03d", i)));
      } else {
        valores.add(generarMockValorCampo(i, 2L, String.format("%03d", i)));
      }
    }

    // when: Buscamos ValorCampo con produccionCientificaId
    BDDMockito.given(
        repository.findAllByCampoProduccionCientificaId(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Long indiceImpactoIdToFind = invocation.getArgument(0);
          return valores.stream()
              .filter(valor -> indiceImpactoIdToFind.equals(valor.getCampoProduccionCientificaId()))
              .collect(Collectors.toList());
        });
    List<ValorCampo> valoresBuscados = service.findAllByCampoProduccionCientificaId(campoProduccionCientificaId);
    // then: Cada ValorCampo tiene produccionCientificaId buscado
    int numElements = valoresBuscados.size();
    Assertions.assertThat(numElements).as("valoresBuscados.size()").isEqualTo(3);
    valoresBuscados.stream().forEach(valorBuscado -> {
      Assertions.assertThat(valorBuscado.getCampoProduccionCientificaId()).as("getProduccionCientificaId")
          .isEqualTo(campoProduccionCientificaId);
    });
  }

  private ValorCampo generarMockValorCampo(
      Long id, Long campoProduccionCientificaId, String valorId) {
    return generarMockValorCampo(
        id, campoProduccionCientificaId,
        DEFAULT_DATA_ORDEN, VALOR_PREFIX + valorId);
  }

  private ValorCampo generarMockValorCampo(
      Long id, String valorId) {
    return generarMockValorCampo(
        id, DEFAULT_DATA_CAMPO_PRODUCCION_CIENTIFICA_ID,
        DEFAULT_DATA_ORDEN, VALOR_PREFIX + valorId);
  }

  private ValorCampo generarMockValorCampo(
      Long id) {
    return generarMockValorCampo(
        id, DEFAULT_DATA_CAMPO_PRODUCCION_CIENTIFICA_ID,
        DEFAULT_DATA_ORDEN, DEFAULT_DATA_VALOR);
  }

  private ValorCampo generarMockValorCampo(
      Long id, Long campoProduccionCientificaId, Integer orden, String valor) {
    return ValorCampo.builder()
        .id(id)
        .campoProduccionCientificaId(campoProduccionCientificaId)
        .orden(orden)
        .valor(valor)
        .build();
  }
}
