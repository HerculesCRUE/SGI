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
import org.crue.hercules.sgi.prc.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.prc.model.Proyecto;
import org.crue.hercules.sgi.prc.repository.ProyectoRepository;
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
 * ProyectoServiceTest
 */
@Import({ ProyectoService.class, ApplicationContextSupport.class })
class ProyectoServiceTest extends BaseServiceTest {

  private static final Long DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID = 1L;
  private static final Long DEFAULT_DATA_PROYECTO_REF = 1L;

  @MockBean
  private ProyectoRepository repository;

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
  private ProyectoService service;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findAll_ReturnsPage() {
    // given: Una lista con 37 Proyecto
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 37; i++) {
      proyectos.add(generarMockProyecto(i, i));
    }

    BDDMockito.given(
        repository.findAll(ArgumentMatchers.<Specification<Proyecto>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<Proyecto>>() {
          @Override
          public Page<Proyecto> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            toIndex = toIndex > proyectos.size() ? proyectos.size() : toIndex;
            List<Proyecto> content = proyectos.subList(fromIndex, toIndex);
            Page<Proyecto> page = new PageImpl<>(content, pageable, proyectos.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<Proyecto> page = service.findAll(null, paging);

    // then: Devuelve la pagina 3 con los Proyecto del 31 al 37
    Assertions.assertThat(page.getContent()).as("getContent().hasSize()").hasSize(7);
    Assertions.assertThat(page.getNumber()).as("getNumber()").isEqualTo(3);
    Assertions.assertThat(page.getSize()).as("getSize()").isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).as("getTotalElements()").isEqualTo(37);
    for (int i = 31; i <= 37; i++) {
      Proyecto proyecto = page.getContent().get(i - (page.getSize() * page.getNumber()) - 1);
      Assertions.assertThat(proyecto.getProyectoRef()).as("getProyectoRef")
          .isEqualTo(i);
    }
  }

  @Test
  void findById_ReturnsProyecto() {
    // given: Proyecto con el id buscado
    Long idBuscado = 1L;
    BDDMockito.given(repository.findById(idBuscado)).willReturn(Optional.of(generarMockProyecto(idBuscado)));

    // when: Buscamos Proyecto por su id
    Proyecto proyecto = service.findById(idBuscado);

    // then: el Proyecto
    Assertions.assertThat(proyecto).as("isNotNull()").isNotNull();
    Assertions.assertThat(proyecto.getId()).as("getId()").isEqualTo(idBuscado);
    Assertions.assertThat(proyecto.getProduccionCientificaId()).as("getProduccionCientificaId")
        .isEqualTo(DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID);
    Assertions.assertThat(proyecto.getProyectoRef()).as("getProyectoRef")
        .isEqualTo(DEFAULT_DATA_PROYECTO_REF);
  }

  @Test
  void findById_WithIdNotExist_ThrowsProyectoNotFoundException() {
    // given: Ningun Proyecto con el id buscado
    Long idBuscado = 33L;
    BDDMockito.given(repository.findById(ArgumentMatchers.<Long>any())).willReturn(Optional.empty());

    // when: Buscamos el Proyecto por su id
    // then: Lanza un ProyectoNotFoundException
    Assertions.assertThatThrownBy(() -> service.findById(idBuscado))
        .isInstanceOf(ProyectoNotFoundException.class);
  }

  @Test
  void findAllByProduccionCientificaId_ReturnsList() {
    // given: Una lista con 7 Proyecto y un produccionCientificaId
    Long produccionCientificaId = 1L;
    List<Proyecto> proyectos = new ArrayList<>();
    for (long i = 1; i <= 7; i++) {
      if (i % 2 == 0) {
        proyectos.add(generarMockProyecto(i, produccionCientificaId, i));
      } else {
        proyectos.add(generarMockProyecto(i, 2L, i));
      }
    }

    // when: Buscamos Proyecto con produccionCientificaId
    BDDMockito.given(
        repository.findAllByProduccionCientificaId(ArgumentMatchers.<Long>any()))
        .willAnswer((InvocationOnMock invocation) -> {
          Long proyectoIdToFind = invocation.getArgument(0);
          return proyectos.stream()
              .filter(proyecto -> proyectoIdToFind.equals(proyecto.getProduccionCientificaId()))
              .collect(Collectors.toList());
        });
    List<Proyecto> proyectosBuscados = service.findAllByProduccionCientificaId(produccionCientificaId);
    // then: Cada Proyecto tiene produccionCientificaId buscado
    Assertions.assertThat(proyectosBuscados).as("hasSize()").hasSize(3);
    proyectosBuscados.stream().forEach(proyectoBuscado -> {
      Assertions.assertThat(proyectoBuscado.getProduccionCientificaId()).as("getProduccionCientificaId")
          .isEqualTo(produccionCientificaId);
    });
  }

  private Proyecto generarMockProyecto(
      Long id, Long proyectoRef) {
    return generarMockProyecto(id, DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID, proyectoRef);
  }

  private Proyecto generarMockProyecto(
      Long id) {
    return generarMockProyecto(id, DEFAULT_DATA_PRODUCCION_CIENTIFICA_ID, DEFAULT_DATA_PROYECTO_REF);
  }

  private Proyecto generarMockProyecto(
      Long id, Long produccionCientificaId, Long proyectoRef) {
    return Proyecto.builder()
        .id(id)
        .produccionCientificaId(produccionCientificaId)
        .proyectoRef(proyectoRef)
        .build();
  }
}
