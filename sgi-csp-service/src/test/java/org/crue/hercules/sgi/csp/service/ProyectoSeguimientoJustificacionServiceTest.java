package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoSeguimientoJustificacionRepository;
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
 * ProyectoSeguimientoJustificacionServiceTest
 */
@Import({ ProyectoSeguimientoJustificacionService.class, ApplicationContextSupport.class })
public class ProyectoSeguimientoJustificacionServiceTest extends BaseServiceTest {

  @MockBean
  private ProyectoSeguimientoJustificacionRepository proyectoSeguimientoJustificacionRepository;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private ProyectoSeguimientoJustificacionService proyectoSeguimientoJustificacionService;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findAllByProyectoSgeRef_WithPaging_ReturnsPage() {
    // given: One hundred ProyectoSeguimientoJustificacion
    String proyectoSgeRef = "1234";
    List<ProyectoSeguimientoJustificacion> proyectoSeguimientoJustificacionList = new ArrayList<>();
    for (long i = 1; i <= 100; i++) {
      proyectoSeguimientoJustificacionList
          .add(generarMockProyectoSeguimientoJustificacion(i, i));
    }

    BDDMockito.given(
        proyectoSeguimientoJustificacionRepository.findAll(
            ArgumentMatchers.<Specification<ProyectoSeguimientoJustificacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<ProyectoSeguimientoJustificacion>>() {
          @Override
          public Page<ProyectoSeguimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<ProyectoSeguimientoJustificacion> content = proyectoSeguimientoJustificacionList.subList(fromIndex,
                toIndex);
            Page<ProyectoSeguimientoJustificacion> page = new PageImpl<>(content, pageable,
                proyectoSeguimientoJustificacionList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<ProyectoSeguimientoJustificacion> page = proyectoSeguimientoJustificacionService.findAllByProyectoSgeRef(
        proyectoSgeRef,
        null, paging);

    // then: A Page with ten ProyectoSeguimientoJustificacion are returned
    // containing
    // justificanteReintegro='ProyectoSeguimientoJustificacion-031' to
    // 'ProyectoSeguimientoJustificacion-040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacion = page.getContent().get(i);
      Assertions.assertThat(proyectoSeguimientoJustificacion.getJustificanteReintegro())
          .isEqualTo("ProyectoSeguimientoJustificacion-" + String.format("%03d", j));
    }
  }

  @Test
  void create_idNotNull_ThrowsIllegalArgumentException() {
    // given: Un ProyectoSeguimientoJustificacion con un id no null
    ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacionToCreate = generarMockProyectoSeguimientoJustificacion(
        1234L, 1L);

    // when: Creamos un ProyectoSeguimientoJustificacion con id no null
    // then: Lanza IllegalArgumentException porque el id debe ser null
    Assertions
        .assertThatThrownBy(
            () -> proyectoSeguimientoJustificacionService.create(proyectoSeguimientoJustificacionToCreate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_ProyectoProyectoSgeNull_ThrowsIllegalArgumentException() {
    // given: Un ProyectoSeguimientoJustificacion con ProyectoProyectoSge null
    ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacionToCreate = generarMockProyectoSeguimientoJustificacion(
        null, null, null);

    // when: Creamos un ProyectoSeguimientoJustificacion con ProyectoProyectoSge
    // null
    // then: Lanza IllegalArgumentException porque el ProyectoProyectoSge no debe
    // ser null
    Assertions
        .assertThatThrownBy(
            () -> proyectoSeguimientoJustificacionService.create(proyectoSeguimientoJustificacionToCreate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_proyectoProyectoSgeIdNull_ThrowsIllegalArgumentException() {
    // given: Un ProyectoSeguimientoJustificacion con un proyectoProyectoSgeId null
    ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacionToCreate = generarMockProyectoSeguimientoJustificacion(
        1234L, null);

    // when: Creamos un ProyectoSeguimientoJustificacion con proyectoProyectoSgeId
    // null
    // then: Lanza IllegalArgumentException porque el proyectoProyectoSgeId no debe
    // ser null
    Assertions
        .assertThatThrownBy(
            () -> proyectoSeguimientoJustificacionService.create(proyectoSeguimientoJustificacionToCreate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_ReturnsProyectoSeguimientoJustificacion() {
    // given: Un ProyectoSeguimientoJustificacion con un id null
    Long id = 1L;
    Long proyectoProyectoSgeId = 1L;
    ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacionToCreate = generarMockProyectoSeguimientoJustificacion(
        null, proyectoProyectoSgeId);
    ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacionCreated = generarMockProyectoSeguimientoJustificacion(
        id, proyectoProyectoSgeId);

    // when: Creamos un ProyectoSeguimientoJustificacion con id null
    BDDMockito
        .given(
            proyectoSeguimientoJustificacionRepository.save(ArgumentMatchers.<ProyectoSeguimientoJustificacion>any()))
        .willReturn(proyectoSeguimientoJustificacionCreated);

    // then: ProyectoSeguimientoJustificacion creado correctamente
    ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacion = proyectoSeguimientoJustificacionService
        .create(proyectoSeguimientoJustificacionToCreate);
    Assertions.assertThat(proyectoSeguimientoJustificacion).isNotNull();
    Assertions.assertThat(proyectoSeguimientoJustificacion.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(proyectoSeguimientoJustificacion.getProyectoProyectoSge().getId())
        .as("getProyectoProyectoSge().getId()").isEqualTo(proyectoProyectoSgeId);
    Assertions.assertThat(proyectoSeguimientoJustificacion.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(
            "ProyectoSeguimientoJustificacion-" + String.format("%03d", id));
  }

  @Test
  void update_idIsNull_ThrowsIllegalArgumentException() {
    // given: Un ProyectoSeguimientoJustificacion con un id null
    ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacionToUpdate = generarMockProyectoSeguimientoJustificacion(
        null, 1L);

    // when: Actualizamos un ProyectoSeguimientoJustificacion con id null
    // then: Lanza IllegalArgumentException porque el id no debe ser null
    Assertions
        .assertThatThrownBy(
            () -> proyectoSeguimientoJustificacionService.update(proyectoSeguimientoJustificacionToUpdate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_ReturnsProyectoSeguimientoJustificacion() {
    // given: Un ProyectoSeguimientoJustificacion con datos correctos
    Long id = 123L;
    String justificanteReintegroUpdated = "Updated";
    ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacionToUpdate = generarMockProyectoSeguimientoJustificacion(
        id, generarMockProyectoProyectoSge(1L), justificanteReintegroUpdated);
    ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacionOnDB = generarMockProyectoSeguimientoJustificacion(
        id, 1L);

    BDDMockito.given(proyectoSeguimientoJustificacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(proyectoSeguimientoJustificacionOnDB));
    BDDMockito
        .given(
            proyectoSeguimientoJustificacionRepository.save(ArgumentMatchers.<ProyectoSeguimientoJustificacion>any()))
        .willReturn(proyectoSeguimientoJustificacionToUpdate);

    // when: Actualizamos un ProyectoSeguimientoJustificacion con datos correctos
    // then: Se actualiza correctamente
    ProyectoSeguimientoJustificacion proyectoSeguimientoJustificacion = proyectoSeguimientoJustificacionService
        .update(proyectoSeguimientoJustificacionToUpdate);
    Assertions.assertThat(proyectoSeguimientoJustificacion).isNotNull();
    Assertions.assertThat(proyectoSeguimientoJustificacion.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(proyectoSeguimientoJustificacion.getJustificanteReintegro()).as("getJustificanteReintegro()")
        .isEqualTo(justificanteReintegroUpdated);
  }

  private ProyectoSeguimientoJustificacion generarMockProyectoSeguimientoJustificacion(Long id,
      Long proyectoProyectoSgeId) {
    String justificanteReintegroSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockProyectoSeguimientoJustificacion(id, generarMockProyectoProyectoSge(proyectoProyectoSgeId),
        "ProyectoSeguimientoJustificacion-" + justificanteReintegroSuffix);
  }

  private ProyectoSeguimientoJustificacion generarMockProyectoSeguimientoJustificacion(Long id,
      ProyectoProyectoSge proyectoProyectoSge, String justificanteReintegro) {
    return ProyectoSeguimientoJustificacion.builder()
        .id(id)
        .proyectoProyectoSge(proyectoProyectoSge)
        .justificanteReintegro(justificanteReintegro)
        .build();
  }

  private ProyectoProyectoSge generarMockProyectoProyectoSge(Long id) {
    String proyectoSgeRef = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return ProyectoProyectoSge.builder().id(id).proyectoSgeRef(proyectoSgeRef).build();
  }
}
