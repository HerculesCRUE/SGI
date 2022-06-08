package org.crue.hercules.sgi.prc.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.EstadoProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.repository.EstadoProduccionCientificaRepository;
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
 * EstadoProduccionCientificaServiceTest
 */
@Import({ EstadoProduccionCientificaService.class, ApplicationContextSupport.class })
class EstadoProduccionCientificaServiceTest extends BaseServiceTest {

  private static final String COMENTARIO_PREFIX = "Persona-ref-";
  private static final String DEFAULT_DATA_COMENTARIO = COMENTARIO_PREFIX + "default";
  private static final Instant DEFAULT_DATA_FECHA = Instant.now();
  private static final Long DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID = 1L;
  private static final TipoEstadoProduccion DEFAULT_DATA_ESTADO = TipoEstadoProduccion.PENDIENTE;

  @MockBean
  private EstadoProduccionCientificaRepository repository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  // This bean must be created by Spring so validations can be applied
  @Autowired
  private EstadoProduccionCientificaService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findAll_ReturnsPage() {
    // given: Una lista con 37 EstadoProduccionCientifica
    List<EstadoProduccionCientifica> estadosProduccionCientifica = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      estadosProduccionCientifica.add(generarMockEstadoProduccionCientifica(i, String.format("%03d", i)));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<EstadoProduccionCientifica>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<EstadoProduccionCientifica>>() {
          @Override
          public Page<EstadoProduccionCientifica> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > estadosProduccionCientifica.size() ? estadosProduccionCientifica.size() : toIndex;
            List<EstadoProduccionCientifica> content = estadosProduccionCientifica.subList(fromIndex, toIndex);
            Page<EstadoProduccionCientifica> page = new PageImpl<>(content, pageable,
                estadosProduccionCientifica.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<EstadoProduccionCientifica> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los EstadoProduccionCientifica del 31 al 37
    Assertions.assertThat(page.getContent().size()).as("getContent().size()").isEqualTo(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      EstadoProduccionCientifica estadoProduccionCientifica = page.getContent()
          .get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(estadoProduccionCientifica.getComentario()).as("getComentario")
          .isEqualTo(COMENTARIO_PREFIX + String.format("%03d", i));
    }
  }

  @Test
  void findById_ReturnsEstadoProduccionCientifica() {
    // given: EstadoProduccionCientifica con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado))
        .willReturn(Optional.of(generarMockEstadoProduccionCientifica(idBuscado)));

    // when: Buscamos EstadoProduccionCientifica por su id
    EstadoProduccionCientifica estadoProduccionCientifica = service.findById(idBuscado);

    // then: el EstadoProduccionCientifica
    Assertions.assertThat(estadoProduccionCientifica).as("isNotNull()").isNotNull();
    Assertions.assertThat(estadoProduccionCientifica.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(estadoProduccionCientifica.getComentario()).as("getComentario")
        .isEqualTo(DEFAULT_DATA_COMENTARIO);
    Assertions.assertThat(estadoProduccionCientifica.getFecha()).as("getFecha")
        .isEqualTo(DEFAULT_DATA_FECHA);
    Assertions.assertThat(estadoProduccionCientifica.getProduccionCientificaId()).as("getProduccionCientificaId")
        .isEqualTo(DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID);
    Assertions.assertThat(estadoProduccionCientifica.getEstado()).as("getEstado")
        .isEqualTo(DEFAULT_DATA_ESTADO);
  }

  @Test
  void findById_WithIdNotExist_ThrowsEstadoProduccionCientificaNotFoundException() {
    // given: Ningun EstadoProduccionCientifica con el id buscado
    Long idBuscado = 33L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Buscamos el EstadoProduccionCientifica por su id
    // then: Lanza un EstadoProduccionCientificaNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(EstadoProduccionCientificaNotFoundException.class);
  }

  private EstadoProduccionCientifica generarMockEstadoProduccionCientifica(Long id, String comentarioId) {
    return this.generarMockEstadoProduccionCientifica(
        id, COMENTARIO_PREFIX + comentarioId, DEFAULT_DATA_ESTADO,
        DEFAULT_DATA_FECHA, DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID);
  }

  private EstadoProduccionCientifica generarMockEstadoProduccionCientifica(Long id) {
    return this.generarMockEstadoProduccionCientifica(
        id, DEFAULT_DATA_COMENTARIO, DEFAULT_DATA_ESTADO,
        DEFAULT_DATA_FECHA, DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID);
  }

  private EstadoProduccionCientifica generarMockEstadoProduccionCientifica(
      Long id, String comentario, TipoEstadoProduccion estado,
      Instant fecha, Long produccionCientificaId) {
    return EstadoProduccionCientifica.builder()
        .id(id)
        .comentario(comentario)
        .estado(estado)
        .fecha(fecha)
        .produccionCientificaId(produccionCientificaId)
        .build();
  }
}
