package org.crue.hercules.sgi.prc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;
import org.crue.hercules.sgi.prc.exceptions.IndiceImpactoNotFoundException;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.IndiceImpacto.TipoRanking;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
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
 * IndiceImpactoServiceTest
 */
@Import({ IndiceImpactoService.class, ApplicationContextSupport.class })
public class IndiceImpactoServiceTest extends BaseServiceTest {

  private static final String OTRA_FUENTE_IMPACTO_PREFIX = "Otra-fuente-impacto-";
  private static final TipoFuenteImpacto DEFAULT_DATA_FUENTE_IMPACTO = TipoFuenteImpacto.OTHERS;
  private static final TipoRanking DEFAULT_DATA_RANKING = TipoRanking.CLASE1;
  private static final Integer DEFAULT_DATA_ANIO = 2022;
  private static final String DEFAULT_DATA_OTRA_FUENTE_IMPACTO = OTRA_FUENTE_IMPACTO_PREFIX + "default";
  private static final BigDecimal DEFAULT_DATA_INDICE = new BigDecimal(20);
  private static final BigDecimal DEFAULT_DATA_POSICION_PUBLICACION = new BigDecimal(5056);
  private static final BigDecimal DEFAULT_DATA_NUMERO_REVISTAS = new BigDecimal(50);
  private static final Boolean DEFAULT_DATA_REVISTA_25 = Boolean.TRUE;
  private static final Long DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID = 1L;

  @MockBean
  private IndiceImpactoRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private IndiceImpactoService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  public void findAll_ReturnsPage() {
    // given: Una lista con 37 IndiceImpacto
    List<IndiceImpacto> indicesImpacto = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      indicesImpacto.add(generarMockIndiceImpacto(i, String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<IndiceImpacto>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<IndiceImpacto>>() {
          @Override
          public Page<IndiceImpacto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > indicesImpacto.size() ? indicesImpacto.size() : toIndex;
            List<IndiceImpacto> content = indicesImpacto.subList(fromIndex, toIndex);
            Page<IndiceImpacto> page = new PageImpl<>(content, pageable, indicesImpacto.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<IndiceImpacto> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los IndiceImpacto del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      IndiceImpacto indiceImpacto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(indiceImpacto.getOtraFuenteImpacto()).as("getOtraFuenteImpacto")
          .isEqualTo(OTRA_FUENTE_IMPACTO_PREFIX + String.format("%03d", i));
    }
  }

  @Test
  public void findById_ReturnsIndiceImpacto() {
    // given: IndiceImpacto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockIndiceImpacto(idBuscado)));

    // when: Buscamos IndiceImpacto por su id
    IndiceImpacto indiceImpacto = service.findById(idBuscado);

    // then: el IndiceImpacto
    Assertions.assertThat(indiceImpacto).as("isNotNull()").isNotNull();
    Assertions.assertThat(indiceImpacto.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(indiceImpacto.getAnio()).as("getAnio")
        .isEqualTo(DEFAULT_DATA_ANIO);
    Assertions.assertThat(indiceImpacto.getIndice()).as("getIndice")
        .isEqualTo(DEFAULT_DATA_INDICE);
    Assertions.assertThat(indiceImpacto.getNumeroRevistas()).as("getNumeroRevistas")
        .isEqualTo(DEFAULT_DATA_NUMERO_REVISTAS);
    Assertions.assertThat(indiceImpacto.getOtraFuenteImpacto()).as("getOtraFuenteImpacto")
        .isEqualTo(DEFAULT_DATA_OTRA_FUENTE_IMPACTO);
  }

  @Test
  public void findById_WithIdNotExist_ThrowsIndiceImpactoNotFoundException() {
    // given: Ningun IndiceImpacto con el id buscado
    Long idBuscado = 33L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Buscamos el IndiceImpacto por su id
    // then: Lanza un IndiceImpactoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(IndiceImpactoNotFoundException.class);
  }

  @Test
  public void findAllByProduccionCientificaId_ReturnsList() {
    // given: Una lista con 7 IndiceImpacto y un produccionCientificaId
    Long produccionCientificaId = 1L;
    List<IndiceImpacto> indicesImpacto = new ArrayList<>();
    for (long i = 1; i <= 7; i++) {
      if (i % 2 == 0) {
        indicesImpacto.add(generarMockIndiceImpacto(i, produccionCientificaId, String.format("%03d", i)));
      } else {
        indicesImpacto.add(generarMockIndiceImpacto(i, 2L, String.format("%03d", i)));
      }
    }

    // when: Buscamos IndiceImpacto con produccionCientificaId
    BDDMockito.given(
        repository.findAllByProduccionCientificaId(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Long indiceImpactoIdToFind = invocation.getArgument(0);
          return indicesImpacto.stream()
              .filter(indiceImpacto -> indiceImpactoIdToFind.equals(indiceImpacto.getProduccionCientificaId()))
              .collect(Collectors.toList());
        });
    List<IndiceImpacto> indicesImpactoBuscados = service.findAllByProduccionCientificaId(produccionCientificaId);
    // then: Cada IndiceImpacto tiene produccionCientificaId buscado
    Assertions.assertThat(indicesImpactoBuscados.size()).as("size()").isEqualTo(3);
    indicesImpactoBuscados.stream().forEach(indiceImpactoBuscado -> {
      Assertions.assertThat(indiceImpactoBuscado.getProduccionCientificaId()).as("getProduccionCientificaId")
          .isEqualTo(produccionCientificaId);
    });
  }

  private IndiceImpacto generarMockIndiceImpacto(
      Long id, Long produccionCientificaId, String otraFuenteImpactoId) {
    return generarMockIndiceImpacto(
        id, DEFAULT_DATA_ANIO, DEFAULT_DATA_FUENTE_IMPACTO, DEFAULT_DATA_INDICE,
        DEFAULT_DATA_NUMERO_REVISTAS, OTRA_FUENTE_IMPACTO_PREFIX + otraFuenteImpactoId,
        DEFAULT_DATA_POSICION_PUBLICACION,
        produccionCientificaId, DEFAULT_DATA_RANKING, DEFAULT_DATA_REVISTA_25);
  }

  private IndiceImpacto generarMockIndiceImpacto(
      Long id, String otraFuenteImpactoId) {
    return generarMockIndiceImpacto(
        id, DEFAULT_DATA_ANIO, DEFAULT_DATA_FUENTE_IMPACTO, DEFAULT_DATA_INDICE,
        DEFAULT_DATA_NUMERO_REVISTAS, OTRA_FUENTE_IMPACTO_PREFIX + otraFuenteImpactoId,
        DEFAULT_DATA_POSICION_PUBLICACION,
        DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID, DEFAULT_DATA_RANKING, DEFAULT_DATA_REVISTA_25);
  }

  private IndiceImpacto generarMockIndiceImpacto(
      Long id) {
    return generarMockIndiceImpacto(
        id, DEFAULT_DATA_ANIO, DEFAULT_DATA_FUENTE_IMPACTO, DEFAULT_DATA_INDICE,
        DEFAULT_DATA_NUMERO_REVISTAS, DEFAULT_DATA_OTRA_FUENTE_IMPACTO, DEFAULT_DATA_POSICION_PUBLICACION,
        DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID, DEFAULT_DATA_RANKING, DEFAULT_DATA_REVISTA_25);
  }

  private IndiceImpacto generarMockIndiceImpacto(
      Long id, Integer anio,
      TipoFuenteImpacto fuenteImpacto, BigDecimal indice,
      BigDecimal numeroRevistas, String otraFuenteImpacto,
      BigDecimal posicionPublicacion, Long produccionCientificaId,
      TipoRanking ranking, Boolean revista25) {
    return IndiceImpacto.builder()
        .id(id)
        .anio(anio)
        .fuenteImpacto(fuenteImpacto)
        .indice(indice)
        .numeroRevistas(numeroRevistas)
        .otraFuenteImpacto(otraFuenteImpacto)
        .posicionPublicacion(posicionPublicacion)
        .produccionCientificaId(produccionCientificaId)
        .ranking(ranking)
        .revista25(revista25)
        .build();
  }
}
