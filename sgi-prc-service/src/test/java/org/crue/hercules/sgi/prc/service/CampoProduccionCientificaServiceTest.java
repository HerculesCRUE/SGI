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
import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.exceptions.CampoProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaAuthorityHelper;
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
 * CampoProduccionCientificaServiceTest
 */
@Import({ CampoProduccionCientificaService.class, ApplicationContextSupport.class })
class CampoProduccionCientificaServiceTest extends BaseServiceTest {

  private static final CodigoCVN DEFAULT_DATA_CODIGO_CVN = CodigoCVN.COLECTIVA;
  private static final Long DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID = 1L;

  @MockBean
  private CampoProduccionCientificaRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @MockBean
  private ProduccionCientificaAuthorityHelper authorityHelper;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private CampoProduccionCientificaService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findAll_ReturnsPage() {
    // given: Una lista con 37 CampoProduccionCientifica
    List<CampoProduccionCientifica> camposProduccionesCientificas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      camposProduccionesCientificas.add(generarMockCampoProduccionCientifica(i, i));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<CampoProduccionCientifica>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<CampoProduccionCientifica>>() {
          @Override
          public Page<CampoProduccionCientifica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > camposProduccionesCientificas.size() ? camposProduccionesCientificas.size() : toIndex;
            List<CampoProduccionCientifica> content = camposProduccionesCientificas.subList(fromIndex, toIndex);
            Page<CampoProduccionCientifica> page = new PageImpl<>(content, pageable,
                camposProduccionesCientificas.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<CampoProduccionCientifica> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los CampoProduccionCientifica del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().hasSize()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      CampoProduccionCientifica campoProduccionCientifica = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(campoProduccionCientifica.getProduccionCientificaId()).as("getProduccionCientificaId")
          .isEqualTo(i);
    }
  }

  @Test
  void findById_ReturnsCampoProduccionCientifica() {
    // given: CampoProduccionCientifica con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockCampoProduccionCientifica(idBuscado)));

    // when: Buscamos CampoProduccionCientifica por su id
    CampoProduccionCientifica campoProduccionCientifica = service.findById(idBuscado);

    // then: el CampoProduccionCientifica
    Assertions.assertThat(campoProduccionCientifica).as("isNotNull()").isNotNull();
    Assertions.assertThat(campoProduccionCientifica.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(campoProduccionCientifica.getCodigoCVN()).as("getCodigoCVN")
        .isEqualTo(DEFAULT_DATA_CODIGO_CVN);
    Assertions.assertThat(campoProduccionCientifica.getProduccionCientificaId()).as("getProduccionCientificaId")
        .isEqualTo(DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID);
  }

  @Test
  void findById_WithIdNotExist_ThrowsCampoProduccionCientificaNotFoundException() {
    // given: Ningun CampoProduccionCientifica con el id buscado
    Long idBuscado = 33L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Buscamos el CampoProduccionCientifica por su id
    // then: Lanza un CampoProduccionCientificaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(CampoProduccionCientificaNotFoundException.class);
  }

  @Test
  void findAllByProduccionCientificaId_ReturnsList() {
    // given: Una lista con 7 CampoProduccionCientifica y un produccionCientificaId
    Long produccionCientificaId = 1L;
    List<CampoProduccionCientifica> camposProduccionesCientificas = new ArrayList<>();
    for (long i = 1; i <= 7; i++) {
      if (i % 2 == 0) {
        camposProduccionesCientificas.add(generarMockCampoProduccionCientifica(i, produccionCientificaId));
      } else {
        camposProduccionesCientificas.add(generarMockCampoProduccionCientifica(i, 2L));
      }
    }

    // when: Buscamos CampoProduccionCientifica con produccionCientificaId
    BDDMockito.given(
        repository.findAllByProduccionCientificaId(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Long produccionCientificaIdToFind = invocation.getArgument(0);
          return camposProduccionesCientificas.stream().filter(
              campoProduccionCientifica -> produccionCientificaIdToFind
                  .equals(campoProduccionCientifica.getProduccionCientificaId()))
              .collect(Collectors.toList());
        });
    List<CampoProduccionCientifica> campoProduccionCientificasBuscados = service
        .findAllByProduccionCientificaId(produccionCientificaId);
    // then: Cada CampoProduccionCientifica tiene produccionCientificaId buscado
    Assertions.assertThat(campoProduccionCientificasBuscados).as("hasSize()").hasSize(3);
    campoProduccionCientificasBuscados.stream().forEach(autoGrupoBuscado -> {
      Assertions.assertThat(autoGrupoBuscado.getProduccionCientificaId()).as("getProduccionCientificaId")
          .isEqualTo(produccionCientificaId);
    });
  }

  @Test
  void findAllByProduccionCientificaId_ReturnsPage() {
    // given: Una lista con 37 CampoProduccionCientifica que pertenecen a una
    // ProduccionCientifica
    Long produccionCientificaId = 1L;
    List<CampoProduccionCientifica> camposProduccionesCientificas = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      camposProduccionesCientificas.add(generarMockCampoProduccionCientifica(i, 1L));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<CampoProduccionCientifica>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<CampoProduccionCientifica>>() {
          @Override
          public Page<CampoProduccionCientifica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<CampoProduccionCientifica> camposFiltered = camposProduccionesCientificas.stream().filter(
                campoProduccionCientifica -> produccionCientificaId
                    .equals(campoProduccionCientifica.getProduccionCientificaId()))
                .collect(Collectors.toList());
            toIndex = toIndex > camposFiltered.size() ? camposFiltered.size() : toIndex;
            List<CampoProduccionCientifica> content = camposFiltered.subList(fromIndex, toIndex);
            Page<CampoProduccionCientifica> page = new PageImpl<>(content, pageable,
                camposFiltered.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<CampoProduccionCientifica> page = service.findAllByProduccionCientificaId(produccionCientificaId, null,
        paging);

    // then: Devuelve la pagina 3 con los CampoProduccionCientifica del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().hasSize()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      CampoProduccionCientifica campoProduccionCientifica = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(campoProduccionCientifica.getProduccionCientificaId()).as("getProduccionCientificaId")
          .isEqualTo(produccionCientificaId);
    }
  }

  private CampoProduccionCientifica generarMockCampoProduccionCientifica(Long id, Long produccionCientificaId) {
    return this.generarMockCampoProduccionCientifica(
        id, DEFAULT_DATA_CODIGO_CVN, produccionCientificaId);
  }

  private CampoProduccionCientifica generarMockCampoProduccionCientifica(Long id) {
    return this.generarMockCampoProduccionCientifica(
        id, DEFAULT_DATA_CODIGO_CVN, DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID);
  }

  private CampoProduccionCientifica generarMockCampoProduccionCientifica(
      Long id, CodigoCVN codigoCVN,
      Long produccionCientificaId) {
    return CampoProduccionCientifica.builder()
        .id(id)
        .codigoCVN(codigoCVN)
        .produccionCientificaId(produccionCientificaId)
        .build();
  }
}
