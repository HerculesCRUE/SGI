package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.validation.ValidationException;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.exceptions.RequerimientoJustificacionNotDeleteableException;
import org.crue.hercules.sgi.csp.exceptions.RequerimientoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.TipoRequerimiento;
import org.crue.hercules.sgi.csp.repository.RequerimientoJustificacionRepository;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * TipoRequerimientoServiceTest
 */
@Import({ RequerimientoJustificacionService.class, ApplicationContextSupport.class })
class RequerimientoJustificacionServiceTest extends BaseServiceTest {

  @MockBean
  private RequerimientoJustificacionRepository requerimientoJustificacionRepository;

  @MockBean
  private IncidenciaDocumentacionRequerimientoService incidenciaDocumentacionRequerimientoService;

  @MockBean
  private GastoRequerimientoJustificacionService gastoRequerimientoJustificacionService;

  @MockBean
  private AlegacionRequerimientoService alegacionRequerimientoService;

  @MockBean
  private ProyectoPeriodoJustificacionSeguimientoService proyectoPeriodoJustificacionSeguimientoService;

  @MockBean
  private EntityManager entityManager;

  @MockBean
  private EntityManagerFactory entityManagerFactory;

  @MockBean
  private PersistenceUnitUtil persistenceUnitUtil;

  @Autowired
  private RequerimientoJustificacionService requerimientoJustificacionService;

  @BeforeEach
  void setUp() {
    BDDMockito.given(entityManagerFactory.getPersistenceUnitUtil()).willReturn(persistenceUnitUtil);
    BDDMockito.given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
  }

  @Test
  void findAllByProyectoSgeRef_WithPaging_ReturnsPage() {
    // given: One hundred RequerimientoJustificacion
    String proyectoSgeRef = "1234";
    List<RequerimientoJustificacion> requerimientoJustificacionList = new ArrayList<>();
    for (long i = 1; i <= 100; i++) {
      requerimientoJustificacionList
          .add(generarMockRequerimientoJustificacion(i));
    }

    BDDMockito.given(
        requerimientoJustificacionRepository.findAll(ArgumentMatchers.<Specification<RequerimientoJustificacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RequerimientoJustificacion>>() {
          @Override
          public Page<RequerimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RequerimientoJustificacion> content = requerimientoJustificacionList.subList(fromIndex, toIndex);
            Page<RequerimientoJustificacion> page = new PageImpl<>(content, pageable,
                requerimientoJustificacionList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RequerimientoJustificacion> page = requerimientoJustificacionService.findAllByProyectoSgeRef(proyectoSgeRef,
        null, paging);

    // then: A Page with ten RequerimientoJustificacion are returned containing
    // observaciones='RequerimientoJustificacion-031' to
    // 'RequerimientoJustificacion-040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      RequerimientoJustificacion requerimientoJustificacion = page.getContent().get(i);
      Assertions.assertThat(requerimientoJustificacion.getObservaciones())
          .isEqualTo("RequerimientoJustificacion-" + String.format("%03d", j));
    }
  }

  @Test
  void findById_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long id = null;
    // when: Buscamos por id null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions.assertThatThrownBy(() -> requerimientoJustificacionService.findById(id))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void findById_ThrowsRequerimientoJustificacionNotFoundException() {
    // given: id no existente
    Long id = 1234L;
    // when: Buscamos por un id que no existe
    // then: Lanza RequerimientoJustificacionNotFoundException porque no existe un
    // RequerimientoJustificacion con dicho id
    Assertions.assertThatThrownBy(() -> requerimientoJustificacionService.findById(id)).isInstanceOf(
        RequerimientoJustificacionNotFoundException.class);
  }

  @Test
  void findById_ReturnsRequerimientoJustificacion() {
    // given: id existente
    Long id = 1234L;

    BDDMockito.given(requerimientoJustificacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(generarMockRequerimientoJustificacion(id)));
    // when: Buscamos por un id que existe
    RequerimientoJustificacion requerimientoJustificacion = requerimientoJustificacionService.findById(id);

    // then: Devuelve el RequerimientoJustificacion con dicho id
    Assertions.assertThat(requerimientoJustificacion).isNotNull();
    Assertions.assertThat(requerimientoJustificacion.getId()).as("getId()").isEqualTo(id);
  }

  @Test
  void deleteById_WithIdNull_ThrowsIllegalArgumentException() {
    // given: id null
    Long id = null;
    // when: Eliminamos por id null
    // then: Lanza IllegalArgumentException porque el id no puede ser null
    Assertions.assertThatThrownBy(() -> requerimientoJustificacionService.deleteById(id))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void deleteById_ThrowsRequerimientoJustificacionNotDeleteableException() {
    // given: Un RequerimientoJustificacion con un id y que es un requerimiento
    // previo de otro requerimiento
    Long id = 1234L;
    BDDMockito
        .given(requerimientoJustificacionRepository
            .count(ArgumentMatchers.<Specification<RequerimientoJustificacion>>any()))
        .willReturn(1L);

    // when: Eliminamos por un id que existe
    // then: Lanza RequerimientoJustificacionNotDeleteableException porque un
    // RequerimientoJustificacion no puede ser eliminado cuando es el previo de otro
    // RequerimientoJustificacion
    Assertions.assertThatThrownBy(() -> requerimientoJustificacionService.deleteById(id)).isInstanceOf(
        RequerimientoJustificacionNotDeleteableException.class);
  }

  @Test
  void create_idNotNull_ThrowsIllegalArgumentException() {
    // given: Un RequerimientoJustificacion con un id no null
    RequerimientoJustificacion requerimientoJustificacionToCreate = generarMockRequerimientoJustificacion(1234L);

    // when: Creamos un RequerimientoJustificacion con id no null
    // then: Lanza IllegalArgumentException porque el id debe ser null
    Assertions
        .assertThatThrownBy(() -> requerimientoJustificacionService.create(requerimientoJustificacionToCreate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void create_ReturnsRequerimientoJustificacion() {
    // given: Un RequerimientoJustificacion con un id null
    Long id = 1L;
    RequerimientoJustificacion requerimientoJustificacionToCreate = generarMockRequerimientoJustificacion(null);
    RequerimientoJustificacion requerimientoJustificacionCreated = generarMockRequerimientoJustificacion(id);

    // when: Creamos un RequerimientoJustificacion con id null
    BDDMockito.given(requerimientoJustificacionRepository.save(ArgumentMatchers.<RequerimientoJustificacion>any()))
        .willReturn(requerimientoJustificacionCreated);
    BDDMockito.given(requerimientoJustificacionRepository
        .findAll(ArgumentMatchers.<Specification<RequerimientoJustificacion>>any(), ArgumentMatchers.<Sort>any()))
        .willReturn(Arrays.asList(requerimientoJustificacionCreated));

    // then: RequerimientoJustificacion creado correctamente
    RequerimientoJustificacion requerimientoJustificacion = requerimientoJustificacionService
        .create(requerimientoJustificacionToCreate);
    Assertions.assertThat(requerimientoJustificacion).isNotNull();
    Assertions.assertThat(requerimientoJustificacion.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(requerimientoJustificacion.getNumRequerimiento()).as("getNumRequerimiento()").isEqualTo(1);
  }

  @Test
  void update_idIsNull_ThrowsIllegalArgumentException() {
    // given: Un RequerimientoJustificacion con un id null
    RequerimientoJustificacion requerimientoJustificacionToUpdate = generarMockRequerimientoJustificacion(null);

    // when: Actualizamos un RequerimientoJustificacion con id null
    // then: Lanza IllegalArgumentException porque el id no debe ser null
    Assertions
        .assertThatThrownBy(() -> requerimientoJustificacionService.update(requerimientoJustificacionToUpdate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_tipoRequerimientoIsNull_ThrowsIllegalArgumentException() {
    // given: Un RequerimientoJustificacion con un id no null y con
    // TipoRequerimiento null
    RequerimientoJustificacion requerimientoJustificacionToUpdate = generarMockRequerimientoJustificacion(123L);

    // when: Actualizamos un RequerimientoJustificacion con id null
    // then: Lanza IllegalArgumentException porque el TipoRequerimiento no debe ser
    // null
    Assertions
        .assertThatThrownBy(() -> requerimientoJustificacionService.update(requerimientoJustificacionToUpdate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_tipoRequerimientoIdIsNull_ThrowsIllegalArgumentException() {
    // given: Un RequerimientoJustificacion con un id no null y con
    // TipoRequerimiento con id null
    RequerimientoJustificacion requerimientoJustificacionToUpdate = generarMockRequerimientoJustificacion(123L,
        generarMockTipoRequerimiento(null));

    // when: Actualizamos un RequerimientoJustificacion con id null
    // then: Lanza IllegalArgumentException porque el id del TipoRequerimiento no
    // debe ser null
    Assertions
        .assertThatThrownBy(() -> requerimientoJustificacionService.update(requerimientoJustificacionToUpdate))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_tipoRequerimientoNoActivo_ThrowsValidationException() {
    // given: Un RequerimientoJustificacion con un TipoRequerimiento no activo
    Long id = 123L;
    TipoRequerimiento tipoRequerimientoToUpdate = generarMockTipoRequerimiento(2L, "No activo", Boolean.FALSE);
    RequerimientoJustificacion requerimientoJustificacionToUpdate = generarMockRequerimientoJustificacion(id,
        tipoRequerimientoToUpdate);
    RequerimientoJustificacion requerimientoJustificacionOnDB = generarMockRequerimientoJustificacion(id,
        generarMockTipoRequerimiento(1L));

    BDDMockito.given(requerimientoJustificacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(requerimientoJustificacionOnDB));
    mockActivableIsActivo(TipoRequerimiento.class, requerimientoJustificacionToUpdate.getTipoRequerimiento());

    // when: Actualizamos un RequerimientoJustificacion con un TipoRequerimiento no
    // activo
    // then: Lanza una ValidationException porque el TipoRequerimiento a actualizar
    // debe estar activo
    Assertions
        .assertThatThrownBy(() -> requerimientoJustificacionService.update(requerimientoJustificacionToUpdate))
        .isInstanceOf(ValidationException.class);
  }

  @Test
  void update_ReturnsRequerimientoJustificacion() {
    // given: Un RequerimientoJustificacion con datos correctos
    Long id = 123L;
    TipoRequerimiento tipoRequerimientoToUpdate = generarMockTipoRequerimiento(2L);
    RequerimientoJustificacion requerimientoJustificacionToUpdate = generarMockRequerimientoJustificacion(id,
        tipoRequerimientoToUpdate);
    RequerimientoJustificacion requerimientoJustificacionOnDB = generarMockRequerimientoJustificacion(id,
        generarMockTipoRequerimiento(1L));

    BDDMockito.given(requerimientoJustificacionRepository.findById(ArgumentMatchers.anyLong()))
        .willReturn(Optional.of(requerimientoJustificacionOnDB));
    mockActivableIsActivo(TipoRequerimiento.class, requerimientoJustificacionToUpdate.getTipoRequerimiento());
    BDDMockito.given(requerimientoJustificacionRepository.save(ArgumentMatchers.<RequerimientoJustificacion>any()))
        .willReturn(requerimientoJustificacionToUpdate);
    BDDMockito.given(requerimientoJustificacionRepository
        .findAll(ArgumentMatchers.<Specification<RequerimientoJustificacion>>any(), ArgumentMatchers.<Sort>any()))
        .willReturn(Arrays.asList(requerimientoJustificacionToUpdate));

    // when: Actualizamos un RequerimientoJustificacion con datos correctos
    // then: Se actualiza correctamente
    RequerimientoJustificacion requerimientoJustificacion = requerimientoJustificacionService
        .update(requerimientoJustificacionToUpdate);
    Assertions.assertThat(requerimientoJustificacion).isNotNull();
    Assertions.assertThat(requerimientoJustificacion.getId()).as("getId()").isEqualTo(id);
    Assertions.assertThat(requerimientoJustificacion.getNumRequerimiento()).as("getNumRequerimiento()").isEqualTo(1);
    Assertions.assertThat(requerimientoJustificacion.getTipoRequerimiento().getId())
        .as("getTipoRequerimiento().getId()").isEqualTo(tipoRequerimientoToUpdate.getId());
  }

  @Test
  void findAllByProyectoId_WithPaging_ReturnsPage() {
    // given: One hundred RequerimientoJustificacion
    Long proyectoId = 1234L;
    List<RequerimientoJustificacion> requerimientoJustificacionList = new ArrayList<>();
    for (long i = 1; i <= 100; i++) {
      requerimientoJustificacionList
          .add(generarMockRequerimientoJustificacion(i));
    }

    BDDMockito.given(
        requerimientoJustificacionRepository.findAll(ArgumentMatchers.<Specification<RequerimientoJustificacion>>any(),
            ArgumentMatchers.<Pageable>any()))
        .willAnswer(new Answer<Page<RequerimientoJustificacion>>() {
          @Override
          public Page<RequerimientoJustificacion> answer(InvocationOnMock invocation) throws Throwable {
            Pageable pageable = invocation.getArgument(1, Pageable.class);
            int size = pageable.getPageSize();
            int index = pageable.getPageNumber();
            int fromIndex = size * index;
            int toIndex = fromIndex + size;
            List<RequerimientoJustificacion> content = requerimientoJustificacionList.subList(fromIndex, toIndex);
            Page<RequerimientoJustificacion> page = new PageImpl<>(content, pageable,
                requerimientoJustificacionList.size());
            return page;
          }
        });

    // when: Get page=3 with pagesize=10
    Pageable paging = PageRequest.of(3, 10);
    Page<RequerimientoJustificacion> page = requerimientoJustificacionService.findAllByProyectoId(proyectoId,
        null, paging);

    // then: A Page with ten RequerimientoJustificacion are returned containing
    // observaciones='RequerimientoJustificacion-031' to
    // 'RequerimientoJustificacion-040'
    Assertions.assertThat(page.getContent()).hasSize(10);
    Assertions.assertThat(page.getNumber()).isEqualTo(3);
    Assertions.assertThat(page.getSize()).isEqualTo(10);
    Assertions.assertThat(page.getTotalElements()).isEqualTo(100);
    for (int i = 0, j = 31; i < 10; i++, j++) {
      RequerimientoJustificacion tipoRequerimiento = page.getContent().get(i);
      Assertions.assertThat(tipoRequerimiento.getObservaciones())
          .isEqualTo("RequerimientoJustificacion-" + String.format("%03d", j));
    }
  }

  @Test
  void existsAnyByProyectoPeriodoJustificacionId_ReturnsTrue() {
    // given: Un proyectoPeriodoJustificacionId
    Long proyectoPeriodoJustificacionId = 123L;

    // when: Existe un RequerimientoJustificacion asociado al
    // proyectoPeriodoJustificacionId
    BDDMockito
        .given(requerimientoJustificacionRepository
            .count(ArgumentMatchers.<Specification<RequerimientoJustificacion>>any()))
        .willReturn(1L);

    // then: Devuelve true
    boolean existsAny = requerimientoJustificacionService
        .existsAnyByProyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId);
    Assertions.assertThat(existsAny).isTrue();
  }

  @Test
  void existsAnyByProyectoPeriodoJustificacionId_ReturnsFalse() {
    // given: Un proyectoPeriodoJustificacionId
    Long proyectoPeriodoJustificacionId = 124L;

    // when: Existe un RequerimientoJustificacion asociado al
    // proyectoPeriodoJustificacionId
    BDDMockito
        .given(requerimientoJustificacionRepository
            .count(ArgumentMatchers.<Specification<RequerimientoJustificacion>>any()))
        .willReturn(0L);

    // then: Devuelve true
    boolean existsAny = requerimientoJustificacionService
        .existsAnyByProyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId);
    Assertions.assertThat(existsAny).isFalse();
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(Long id) {
    String observacionSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockRequerimientoJustificacion(id, "RequerimientoJustificacion-" + observacionSuffix,
        null, null);
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(Long id, Long requerimientoPrevioId) {
    String observacionSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockRequerimientoJustificacion(id, "RequerimientoJustificacion-" + observacionSuffix,
        requerimientoPrevioId, null);
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(Long id,
      TipoRequerimiento tipoRequerimiento) {
    String observacionSuffix = id != null ? String.format("%03d", id) : String.format("%03d", 1);
    return generarMockRequerimientoJustificacion(id, "RequerimientoJustificacion-" + observacionSuffix,
        null, tipoRequerimiento);
  }

  private RequerimientoJustificacion generarMockRequerimientoJustificacion(Long id, String observaciones,
      Long requerimientoPrevioId, TipoRequerimiento tipoRequerimiento) {
    return RequerimientoJustificacion.builder()
        .id(id)
        .observaciones(observaciones)
        .requerimientoPrevioId(requerimientoPrevioId)
        .tipoRequerimiento(tipoRequerimiento)
        .build();
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

  private <T> void mockActivableIsActivo(Class<T> clazz, T object) {
    BDDMockito.given(persistenceUnitUtil.getIdentifier(ArgumentMatchers.any(clazz)))
        .willAnswer((InvocationOnMock invocation) -> {
          Object arg0 = invocation.getArgument(0);
          if (arg0 == null) {
            return null;
          }
          BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(arg0);
          Object id = wrapper.getPropertyValue("id");
          return id;
        });
    BDDMockito.given(entityManager.find(ArgumentMatchers.eq(clazz), ArgumentMatchers.anyLong())).willReturn(object);
  }
}
